package org.emmef.audio.noisereduction;


import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.filter.tools.Integrator;
import org.emmef.audio.noisedetection.NrMeasurementSettings;
import org.emmef.logging.Logger;

public class NoiseReductionFilter implements ChainableFilter {
	private static final Logger logger = Logger.getDefault();
	private final double attackFactor;
	private final double releaseFactor;
	private final double[] filterMemory;
	private final double[] delay;
	
	
	private final NrDynamics dynamics;
	private final BucketScanner bucketScanner;
	private final int maximumHoldCount;
	private final Times times;
	
	private final int maxFilterPosition;
	private final int maxDelayPosition;
	private int filterPosition;
	private int delayPosition;
	private double rmsValue;
	private int holdCount = 0;
	private double runningMax = 0.0;

	public NoiseReductionFilter(int latency, RatedTimings timings, NrMeasurementSettings nrSettings, double minFreq, NrDynamics dynamics) {
		this.dynamics = dynamics;
		this.times = new Times(timings, nrSettings, minFreq);
		
		this.bucketScanner = new BucketScanner(times.measurementSamples, BucketScanner.SCALE_48BIT);
		this.attackFactor = Integrator.characteristicDecayPerSample(times.attackSamples);
		this.releaseFactor = Integrator.characteristicDecayPerSample(times.releaseSamples);
		int filterLatency = times.predictionSamples;
		this.filterMemory = new double[filterLatency];
		this.maxFilterPosition = filterLatency - 1;
		this.maximumHoldCount = filterLatency;
		final int delayTime = latency - filterLatency;
		this.delay = new double[delayTime];
		this.maxDelayPosition = delayTime - 1;
		this.filterPosition = 0;
		this.delayPosition = 0;
		logger.config(this);
	}
	
	@Override
	public String toString() {
		return String.format("NoiseReduction: measurement-window=%1.3fs; attack-time=%1.3fs; release-time=%1.3fs; prediction=%1.3fs", 
				times.measurementTime, 
				times.attackTime, 
				times.releaseTime, 
				times.predictionTime); 
	}
	
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
	}

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
		
		bucketScanner.addUnscaledSample(filterSample * filterSample);
		final double max = Math.sqrt(bucketScanner.getAverage());
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
		
		return dynamics.amplification(rmsValue) * sample;		
	}
	
	public Object getMetaData() {
		return null;
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
			this.latency = slowTimes.predictionSamples;
		}

		public ChainableFilter createFilter(Object metaData, double minFreq, double maxFreq, byte[] markers) {
			double noiseLevel = metaData instanceof Double ? (Double)metaData : 1e-3;
			return new NoiseReductionFilter(latency, ratedTimings, nrSettings, minFreq, nrDynamicsFactory.create(noiseLevel));
		}

		public Object[] filterCallBack(ChainableFilter[] filters) {
			return new Object[filters.length];
		}

		public int getLatency() {
			return latency;
		}		
		
		public int getEndOffset() {
			return 0;
		}
		
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
			this.measurementSamples = ratedTimings.getMeasurementSamples(lowestFrequencyInBand);
			this.attackSamples = ratedTimings.getAttackSamples(lowestFrequencyInBand);
			this.releaseSamples = ratedTimings.getReleaseSamples(lowestFrequencyInBand);
			final int rmsSamples = ratedTimings.getSamples(0.5 * nrSettings.rmsWin);
			final int window = Math.min(measurementSamples, 5 * attackSamples);
			this.predictionSamples = Math.min(rmsSamples, (window + 2 * attackSamples) / 2);
			final double scale = 1.0 / ratedTimings.sampleRate;
			this.measurementTime = scale * measurementSamples;
			this.attackTime = scale * attackSamples;
			this.releaseTime = scale * releaseSamples;
			this.predictionTime = scale * predictionSamples;
		}
		
	}
}
