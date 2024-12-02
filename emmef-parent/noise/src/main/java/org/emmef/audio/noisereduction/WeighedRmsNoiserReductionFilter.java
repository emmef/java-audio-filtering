package org.emmef.audio.noisereduction;

import org.emmef.audio.buckets.Detection;
import org.emmef.audio.buckets.RmsDetection;
import org.emmef.audio.noisedetection.NrMeasurementSettings;
import org.emmef.audio.noisedetection.WeighedRmsLoudnessMeasurementFilter;
import org.emmef.logging.FormatLogger;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class WeighedRmsNoiserReductionFilter implements ChainableFilter {
	private static final FormatLogger logger = FormatLogger.getLogger(MultiBandNoiseFilter.class);
	private final @Nonnull NrDynamics nrDynamics;
	private final @Nonnull NrMeasurementSettings nrMeasurements;
	private final double minFreq;
	private final double maxFreq;
	private final long sampleRate;
	private final Detection rmsDetection;
	private final double[] delayBuffer;
	private int delayPointer = 0;

	static Detection createDetection(long sampleRate) {
		return new RmsDetection(sampleRate, WeighedRmsLoudnessMeasurementFilter.ATTACK_SECONDS, WeighedRmsLoudnessMeasurementFilter.RELEASE_SECONDS, 0.05, 0.1);
	}

	public WeighedRmsNoiserReductionFilter(@Nonnull NrDynamics nrDynamics, @Nonnull NrMeasurementSettings nrMeasurements, double minFreq, double maxFreq, long sampleRate) {
		this.nrDynamics = nrDynamics;
		this.nrMeasurements = nrMeasurements;
		this.minFreq = minFreq;
		this.maxFreq = maxFreq;
		this.sampleRate = sampleRate;
		this.rmsDetection = createDetection(sampleRate);
		this.delayBuffer = new double[rmsDetection.getHoldCount()];
		logger.info(this);
	}

	@Override
	public double filter(double input) {
		double delayedSample = delayBuffer[delayPointer];
		delayBuffer[delayPointer] = input;
		delayPointer++;
		delayPointer %= delayBuffer.length;
		double detection = rmsDetection.addSample(input);
		return nrDynamics.amplification(detection) * delayedSample;
	}

	@Override
	public void reset() {
		Arrays.fill(delayBuffer, 0);
		delayPointer = 0;
	}

	public String toString() {
		return String.format("WeighedRmsNoiseReduction: %1.0fHz~%1.0fHz: %s", minFreq, maxFreq, nrDynamics);
	}

	public static class Factory implements FilterFactory {
		private final long sampleRate;
		private final NrDynamicsFactory nrDynamicsFactory;
		private final NrMeasurementSettings nrMeasurements;
		private final int latency;

		public Factory(long sampleRate, NrDynamicsFactory nrDynamicsFactory, NrMeasurementSettings nrMeasurements) {
			this.sampleRate = sampleRate;
			this.nrDynamicsFactory = nrDynamicsFactory;
			this.nrMeasurements = nrMeasurements;
			this.latency = createDetection(sampleRate).getHoldCount();
		}

		@Override
		public ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers) {
			double noiseLevel;
			if (filterMetaData instanceof Double level) {
				noiseLevel = Math.max(1e-14, Math.min(1.0, level));
			}
			else {
				noiseLevel = 1e-6;
			}
			NrDynamics nrDynamics = nrDynamicsFactory.create(noiseLevel);
			return new WeighedRmsNoiserReductionFilter(nrDynamics, nrMeasurements, minFreq, maxFreq, sampleRate);
		}

		@Override
		public int getLatency() {
			return latency;
		}
	}
}
