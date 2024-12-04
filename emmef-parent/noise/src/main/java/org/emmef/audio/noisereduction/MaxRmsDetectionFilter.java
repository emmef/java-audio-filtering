package org.emmef.audio.noisereduction;

import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.buckets.Detection;
import org.emmef.audio.noisedetection.NrMeasurementSettings;
import org.emmef.logging.FormatLogger;

public class MaxRmsDetectionFilter implements ChainableFilter {
	private static final FormatLogger logger = FormatLogger.getLogger(MaxRmsDetectionFilter.class);

	private final Detection bucketScanner;
	private int count = 0;

	public MaxRmsDetectionFilter(long sampleRate, double windowSeconds) {
		bucketScanner = new BucketScanner(sampleRate, windowSeconds);
	}
	
	@Override
	public double filter(double source) {
		if (source == 0.0) {
			logger.trace("%8d MaxRmsDetection sample %1.3e", count, source);
		}
		count++;
		bucketScanner.addSample(source);
		return source;
	}
	
	@Override
	public void reset() {
		bucketScanner.reset();
	}
	
	@Override
	public Double getMetaData() {
		final double maximum = Math.sqrt(bucketScanner.getMaximum());
		logger.trace("Max RMS = %1.1e (%1.1fdB)", maximum, 20* Math.log(maximum) / Math.log(10));
		return maximum;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	public static class Factory implements FilterFactory {
		private final double windowSeconds;
		private final long sampleRate;

		public Factory(long sampleRate, NrMeasurementSettings nrMeasurements) {
			this.windowSeconds = nrMeasurements.rmsWin;
			this.sampleRate = sampleRate;
		}

		@Override
		public ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers) {
			return new MaxRmsDetectionFilter(sampleRate, windowSeconds);
		}

		@Override
		public int getEndOffset() {
			return 0;
		}

		@Override
		public int getLatency() {
			return 0;
		}

		@Override
		public int getStartOffset() {
			return 0;
		}

	}

}
