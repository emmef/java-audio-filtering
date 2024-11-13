package org.emmef.audio.noisedetection;

import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.noisereduction.ChainableFilter;
import org.emmef.audio.noisereduction.FilterFactory;

public class NoiseLevelDetectionFilterFactory implements FilterFactory {
	private ThreadLocal<BucketScanner> scanner;
	private final NrMeasurementValues nrMeasurementSettings;

	public NoiseLevelDetectionFilterFactory(long sampleRate, NrMeasurementSettings nrMeasurementSettings) {
		this.nrMeasurementSettings = nrMeasurementSettings.withSampleRate(sampleRate);
		scanner = new ThreadLocal<BucketScanner>() {
			@Override
			protected BucketScanner initialValue() {
				return new BucketScanner(NoiseLevelDetectionFilterFactory.this.nrMeasurementSettings.noiseWinwSamples, BucketScanner.SCALE_48BIT);
			}
		};
	}

	@Override
	public ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers) {
		if (filterMetaData == null) {
			throw new NullPointerException("filterMetaData");
		}
		if (nrMeasurementSettings.frequencyScanning) {
			return new WindowFrequencyNoiseLevelDetectionFilter(scanner.get(), (Double) filterMetaData, nrMeasurementSettings);
		}
		else {
			NoiseLevelDiscardFilter.DiscardInfo info = new NoiseLevelDiscardFilter.DiscardInfo(markers, (Double) filterMetaData);
			return new MinimumWindowNoiseLevelDetectionFilter(scanner.get(), info, nrMeasurementSettings);
		}
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
