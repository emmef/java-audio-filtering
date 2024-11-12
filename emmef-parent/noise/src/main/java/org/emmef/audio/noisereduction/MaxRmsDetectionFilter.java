package org.emmef.audio.noisereduction;

import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.noisedetection.NrMeasurementSettings;
import org.emmef.logging.FormatLogger;

public class MaxRmsDetectionFilter implements ChainableFilter {
	private static final FormatLogger logger = FormatLogger.getLogger(MaxRmsDetectionFilter.class);

	private final BucketScanner bucketScanner;
	private int count = 0;

	public MaxRmsDetectionFilter(int bucketSize) {
		bucketScanner = new BucketScanner(bucketSize, BucketScanner.SCALE_48BIT);
	}
	
	@Override
	public double filter(double source) {
//		if (count < 120000 || (count > 7000000 && count < 8000000)) {
//			logger.trace("%8d MaxRmsDetection sample %1.3e", count, source);
//		}
		if (source == 0.0) {
			logger.trace("%8d MaxRmsDetection sample %1.3e", count, source);
		}
		count++;
		bucketScanner.addUnscaledSample(source * source);
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
		private final int bucketSize;

		public Factory(long samplerate, NrMeasurementSettings nrMeasurements) {
			bucketSize = Math.max(1, (int)(0.5 + nrMeasurements.rmsWin * samplerate));
		}

		@Override
		public ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers) {
			return new MaxRmsDetectionFilter(bucketSize);
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
