package org.emmef.audio.noisereduction;

import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.noisedetection.NrMeasurementSettings;
import org.emmef.logging.FormatLogger;

public class MaxRmsDetectionFilter implements ChainableFilter {
	private static final FormatLogger logger = FormatLogger.getLogger(MaxRmsDetectionFilter.class);

	private final BucketScanner bucketScanner;
	private int count = 0;

	private MaxRmsDetectionFilter(double sampleRate, double bucketSeconds) {
		this.bucketScanner = new BucketScanner(sampleRate, bucketSeconds);
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
		final double maximum = bucketScanner.getMaximum();
		logger.trace("Max RMS = %1.1e (%1.1fdB)", maximum, 20* Math.log(maximum) / Math.log(10));
		return maximum;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	public static class Factory implements FilterFactory {
		private final double bucketSeconds;
		private final double sampleRate;

		public Factory(long sampleRate, NrMeasurementSettings nrMeasurements) {
			this.bucketSeconds = nrMeasurements.rmsWin;
			this.sampleRate = sampleRate;
		}

		@Override
		public ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers) {
			return new MaxRmsDetectionFilter(sampleRate, bucketSeconds);
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
