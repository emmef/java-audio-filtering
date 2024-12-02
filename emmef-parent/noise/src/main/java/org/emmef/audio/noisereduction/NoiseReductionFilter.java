package org.emmef.audio.noisereduction;


import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.buckets.Detection;
import org.emmef.audio.buckets.Integration;
import org.emmef.audio.filter.tools.Integrator;
import org.emmef.audio.noisedetection.NrMeasurementSettings;
import org.emmef.logging.FormatLogger;

public class NoiseReductionFilter implements ChainableFilter {
	private static final FormatLogger logger = FormatLogger.getLogger(NoiseReductionFilter.class);
	private final double attackFactor;
	private final double releaseFactor;
	private final double[] filterMemory;
	private final double[] delay;
	
	
	private final NrDynamics dynamics;
	private final Detection bucketScanner;
	private final int maximumHoldCount;
	private final Times times;
	
	private final int maxFilterPosition;
	private final int maxDelayPosition;
	private int filterPosition;
	private int delayPosition;
	private double rmsValue;
	private int holdCount = 0;
	private double runningMax = 0.0;
	private Integration.Factors longFactors;
	private Integration.Integrator longIntegrator;
	private double longRunning = 0.0;

	public NoiseReductionFilter(int latency, RatedTimings timings, NrMeasurementSettings nrSettings, double minFreq, NrDynamics dynamics) {
		this.dynamics = dynamics;
		times = new Times(timings, nrSettings, minFreq);
		
		bucketScanner = new BucketScanner(times.measurementSamples, BucketScanner.SCALE_48BIT);
		attackFactor = Integrator.characteristicDecayPerSample(times.attackSamples);
		releaseFactor = Integrator.characteristicDecayPerSample(times.releaseSamples);
		int filterLatency = times.predictionSamples;
		filterMemory = new double[filterLatency];
		maxFilterPosition = filterLatency - 1;
		maximumHoldCount = filterLatency;
		final int delayTime = latency - filterLatency;
		delay = new double[delayTime];
		maxDelayPosition = delayTime - 1;
		filterPosition = 0;
		delayPosition = 0;
		logger.info(this);

		longFactors = new Integration.Factors(timings.sampleRate * 10);
		longIntegrator = new Integration.Integrator(longFactors);
	}
	
	@Override
	public String toString() {
		return String.format("NoiseReduction: noise-level=%1.1fdB; measurement-window=%1.3fs; attack-time=%1.3fs; release-time=%1.3fs; prediction=%1.3fs",
				NrDynamics.valueToDecibel(dynamics.getNoiseLevel()),
				times.measurementTime,
				times.attackTime,
				times.releaseTime,
				times.predictionTime);
	}
	
	@Override
	public void reset() {
		filterPosition = 0;
		delayPosition = 0;
		rmsValue = 0.0;
		for (int i = 0; i < filterMemory.length; i++) {
			filterMemory[i] = 0.0;
		}
		for (int i = 0; i < delay.length; i++) {
			delay[i] = 0.0;
		}
		longIntegrator.setValue(0);
		longRunning = 0.0;
	}

	@Override
	public double filter(double source) {
		final double filterSample;
		if (maxDelayPosition == 0) {
			filterSample = delay[0];
			delay[0] = source;
		}
		else if (maxDelayPosition > 0) {
			filterSample = delay[delayPosition];
			delay[delayPosition] = source;
			if (delayPosition == maxDelayPosition) {
				delayPosition = 0;
			}
			else {
				delayPosition++;
			}
		}
		else {
			filterSample = source;
		}
		
		final double sample = filterMemory[filterPosition];
		filterMemory[filterPosition] = filterSample;
		
		if (filterPosition == maxFilterPosition) {
			filterPosition = 0;
		}
		else {
			filterPosition++;
		}

		final double max = bucketScanner.addSample(filterSample); // Math.sqrt(bucketScanner.getMeanSquared());
		if (max > runningMax) {
			runningMax = max;
			holdCount = maximumHoldCount;
		}
		else if (holdCount > 0) {
			holdCount--;
		}
		else {
			runningMax = max;
		}
		final double factor = rmsValue < runningMax ? attackFactor : releaseFactor;
		
		rmsValue += (runningMax - rmsValue) * factor;

		double longIntegrated = longIntegrator.integrate(rmsValue);
		if (longRunning == 0.0) {
			longRunning = longIntegrated;
		}
		else {
			double v = Math.abs(longRunning - longIntegrated) / longRunning;
			if (v < 0.9 || v > 1.1) {
				longRunning = longIntegrated;
//				logger.info(this + " avgRms=" + longRunning);
			}
		}

		return dynamics.amplification(rmsValue) * sample;
	}

	public static class Factory implements FilterFactory {
		private final int latency;
		
		private final NrDynamicsFactory nrDynamicsFactory;
		private final RatedTimings ratedTimings;
		private final NrMeasurementSettings nrSettings;
		
		
		public Factory(RatedTimings ratedTimings, NrDynamicsFactory nrDynamicsFactory, NrMeasurementSettings nrSettings) {
			this.ratedTimings = ratedTimings;
			this.nrDynamicsFactory = nrDynamicsFactory;
			this.nrSettings = nrSettings;
			final Times slowTimes = new Times(ratedTimings, this.nrSettings, ratedTimings.getLowestFrequency());
			latency = slowTimes.predictionSamples;
		}

		@Override
		public ChainableFilter createFilter(Object metaData, double minFreq, double maxFreq, byte[] markers) {
			double noiseLevel = metaData instanceof Double ? (Double)metaData : 1e-3;
			return new NoiseReductionFilter(latency, ratedTimings, nrSettings, minFreq, nrDynamicsFactory.create(noiseLevel));
		}

		@Override
		public int getLatency() {
			return latency;
		}
		
		@Override
		public int getEndOffset() {
			return 0;
		}
		
		@Override
		public int getStartOffset() {
			return 0;
		}
	}
	
	public static class Times {
		public final double measurementTime;
		public final double attackTime;
		public final double releaseTime;
		public final double predictionTime;
		public final int measurementSamples;
		public final int attackSamples;
		public final int releaseSamples;
		public final int predictionSamples;

		public Times(RatedTimings ratedTimings, NrMeasurementSettings nrSettings, double lowestFrequencyInBand) {
			measurementSamples = ratedTimings.getMeasurementSamples(lowestFrequencyInBand);
			attackSamples = ratedTimings.getAttackSamples(lowestFrequencyInBand);
			releaseSamples = ratedTimings.getReleaseSamples(lowestFrequencyInBand);
			final int rmsSamples = ratedTimings.getSamples(0.5 * nrSettings.rmsWin);
			final int window = Math.min(measurementSamples, 5 * attackSamples);
			predictionSamples = Math.min(rmsSamples, (window + 2 * attackSamples) / 2);
			final double scale = 1.0 / ratedTimings.sampleRate;
			measurementTime = scale * measurementSamples;
			attackTime = scale * attackSamples;
			releaseTime = scale * releaseSamples;
			predictionTime = scale * predictionSamples;
		}
		
	}
}
