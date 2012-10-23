package org.emmef.audio.noisereduction;

import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.noisedetection.NrMeasurementSettings;
import org.emmef.logging.Logger;

public class MaxRmsDetectionFilter implements ChainableFilter {
	private static final Logger logger = Logger.getDefault();

	private final BucketScanner bucketScanner;
	private int count = 0;

	public MaxRmsDetectionFilter(int bucketSize) {
		bucketScanner = new BucketScanner(bucketSize, BucketScanner.SCALE_48BIT);
	}
	
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
	
	public void reset() {
		bucketScanner.reset();
	}
	
	public Double getMetaData() {
		final double maximum = Math.sqrt(bucketScanner.getMaximum());
		logger.fine("Max RMS = %1.1e (%1.1fdB)", maximum, 20* Math.log(maximum) / Math.log(10));
		return maximum;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	public static class Factory implements FilterFactory {
		private final int bucketSize;

		public Factory(int samplerate, NrMeasurementSettings nrMeasurements) {
			bucketSize = Math.max(1, (int)(0.5 + nrMeasurements.rmsWin * samplerate));
		}

		public ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers) {
			return new MaxRmsDetectionFilter(bucketSize);
		}

		public int getEndOffset() {
			return 0;
		}

		public int getLatency() {
			return 0;
		}

		public int getStartOffset() {
			return 0;
		}

	}

}
