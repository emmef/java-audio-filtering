package org.emmef.audio.noisedetection;

import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.noisereduction.ChainableFilter;
import org.emmef.audio.noisereduction.FilterFactory;
import org.emmef.logging.Logger;

public class NoiseLevelDiscardFilter implements ChainableFilter {
	public static final byte MARK = 1;
	public static final byte UNMARK = (byte)(0xFF ^ MARK);
	
	private static final Logger logger = Logger.getDefault();
	private final byte[] ignored;
	private final double thresholdLo;
	private final double thresholdUnsquared;
	
	private BucketScanner scanner;
	private int position = 0;
	private boolean isWiping;
	private int ignoredCount;
	private final NrMeasurementValues nrMeasurements;
	private final int endPosition;
	private final double maxRmsLevel;

	public NoiseLevelDiscardFilter(byte[] ignored, BucketScanner scanner, NrMeasurementValues nrMeasurements, double maxRmsLevel) {
		this.ignored = ignored;
		this.scanner = scanner;
		this.nrMeasurements = nrMeasurements;
		this.maxRmsLevel = maxRmsLevel;
		this.thresholdUnsquared = maxRmsLevel / nrMeasurements.maxSnRatio;
		this.thresholdLo = thresholdUnsquared * thresholdUnsquared;
		this.endPosition = ignored.length - nrMeasurements.skipEndSamples;
		reset();
	}
	
	public void reset() {
		position = 0;
		ignoredCount = 0;
		for (int i = 0; i < ignored.length; i++) {
			ignored[i] &= UNMARK;
		}
		isWiping = false;
		scanner.reset();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	public double filter(final double source) {
		if (position < nrMeasurements.skipStartSamples || position > endPosition) {
			ignored[position] |= MARK;
			ignoredCount++;
			position++;
			return source;
		}
		scanner.addUnscaledSample(source * source);
		
		if (scanner.isWholeBucketScanned()) {
			final double minimum = scanner.getAverage();
			if (isWiping) {
				if (minimum < thresholdLo) {
					ignored[position] |= MARK;
					ignoredCount++;
				}
				else {
					isWiping = false;
				}
			}
			else if (minimum < thresholdLo) {
				for (int i = position + 1 - scanner.getBucketSize(); i <= position; i++) {
					ignored[i] |= MARK;
					ignoredCount++;
				}
				isWiping = true;
			}
		}
		position++;
		
		return source;
	}
	
	public Double getMetaData() {
		if (10 * ignoredCount / position > 0) {
			logger.warn(this + " ignored " + ignoredCount + " samples");
		}
		else {
			logger.trace(this + " ignored " + ignoredCount + " samples");
		}
		return maxRmsLevel;
	}

	public static class Factory implements FilterFactory {
		private final NrMeasurementValues nrMeasurements;
		private final ThreadLocal<BucketScanner> scanner = new ThreadLocal<BucketScanner>();

		public Factory(final int samplerate, final NrMeasurementSettings nrMeasurements) {
			this.nrMeasurements = nrMeasurements.withSampleRate(samplerate);
		}

		@SuppressWarnings("unused")
		public NoiseLevelDiscardFilter createFilter(Object maxRmsLevel, double minFreq, double maxFreq, byte[] markers) {
			if (maxRmsLevel == null) {
				throw new NullPointerException("maxRmsLevel");
			}
			final Double maxRms = (Double)maxRmsLevel;
			if (maxRms == null) {
				throw new NullPointerException("maxRms");
			}
			double threshold = maxRms.doubleValue() / nrMeasurements.maxSnRatio;
			scanner.set(new BucketScanner((int)(0.5 + nrMeasurements.skipWinwSamples), BucketScanner.SCALE_56BIT));
			
			return new NoiseLevelDiscardFilter(markers, scanner.get(), nrMeasurements, maxRms);
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
	
	public static class DiscardInfo {
		private final byte[] discardedSamples;
		private final double maxRmsValue;
		
		public DiscardInfo(byte[] discardedSamples, double maxRmsValue) {
			this.discardedSamples = discardedSamples;
			this.maxRmsValue = maxRmsValue;
		}
		
		public byte[] getDiscardedSamples() {
			return discardedSamples;
		}
		
		public double getMaxRmsValue() {
			return maxRmsValue;
		}
	}
}
