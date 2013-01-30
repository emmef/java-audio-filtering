package org.emmef.audio.noisedetection;

import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.noisedetection.NoiseLevelDiscardFilter.DiscardInfo;
import org.emmef.audio.noisereduction.ChainableFilter;
import org.emmef.audio.noisereduction.FilterFactory;
import org.emmef.logging.FormatLogger;
import org.emmef.logging.FormatLoggerFactory;

public class NoiseLevelDetectionFilter implements ChainableFilter {
	private static final FormatLogger logger = FormatLoggerFactory.getLogger(NoiseLevelDetectionFilter.class);
	
	private final BucketScanner scanner;
	private final byte[] ignored;
	private int position;
	private final double maxRmsLevel;
	private final NrMeasurementValues nrMeasurementSettings;

	public NoiseLevelDetectionFilter(BucketScanner scanner, DiscardInfo info, NrMeasurementValues nrMeasurementSettings) {
		this.scanner = scanner;
		this.nrMeasurementSettings = nrMeasurementSettings;
		ignored = info.getDiscardedSamples();
		maxRmsLevel = info.getMaxRmsValue();
		scanner.reset();
	}
	
	@Override
	public void reset() {
		scanner.reset();
		position = 0;
	}

	@Override
	public double filter(double source) {
		if ((ignored[position] & NoiseLevelDiscardFilter.MARK) == 0) {
			scanner.addUnscaledSample(source * source);
		}
		position++;
		return source;
	}
	
	public double getNoiseLevel() {
		final double minimum;
		final double absMin = maxRmsLevel / nrMeasurementSettings.maxSnRatio;
		final double maxMin = maxRmsLevel / nrMeasurementSettings.minSnRatio;
		if (scanner.isWholeBucketScanned()) {
			minimum = Math.min(maxMin, Math.max(absMin, Math.sqrt(scanner.getMinimum())));
		}
		else {
			minimum = Math.sqrt(absMin * maxMin);
		}
		
		logger.trace("%s: noiseLevel=%1.1fdB; S/N=%1.1fdB; window=%1.3fs", this, 20*Math.log10(minimum), 20*Math.log10(maxRmsLevel/minimum), 1.0 * scanner.getBucketSize() / nrMeasurementSettings.sampleRate);
		
		return minimum;
	}
	
	@Override
	public Double getMetaData() {
		return getNoiseLevel();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	public static class Factory implements FilterFactory {
		private ThreadLocal<BucketScanner> scanner;
		private final NrMeasurementValues nrMeasurementSettings;
		
		public Factory(long sampleRate, NrMeasurementSettings nrMeasurementSettings) {
			this.nrMeasurementSettings = nrMeasurementSettings.withSampleRate(sampleRate);
			scanner = new ThreadLocal<BucketScanner>() {
				@Override
				protected BucketScanner initialValue() {
					return new BucketScanner(Factory.this.nrMeasurementSettings.noiseWinwSamples, BucketScanner.SCALE_48BIT);
				}
			};
		}

		@Override
		public ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers) {
			if (filterMetaData == null) {
				throw new NullPointerException("filterMetaData");
			}
			DiscardInfo info = new DiscardInfo(markers, (Double)filterMetaData);
			return new NoiseLevelDetectionFilter(scanner.get(), info, nrMeasurementSettings);
		}

		public Object[] filterCallBack(ChainableFilter[] filters) {
			Object[] metaData = new Object[filters.length];
			for (int i = 0; i < filters.length; i++) {
				ChainableFilter filter = filters[i];
				if (filter instanceof NoiseLevelDetectionFilter) {
					metaData[i] = ((NoiseLevelDetectionFilter)filter).getNoiseLevel();
				}
			}

			return metaData;
		}

		@Override
		public int getLatency() {
			return 0;
		}
		
		@Override
		public int getStartOffset() {
			return 0;
		}
		
		@Override
		public int getEndOffset() {
			return 0;
		}
		
	}
}
