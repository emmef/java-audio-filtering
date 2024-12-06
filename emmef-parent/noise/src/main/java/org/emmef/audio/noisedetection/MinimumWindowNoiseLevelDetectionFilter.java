package org.emmef.audio.noisedetection;

import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.noisedetection.NoiseLevelDiscardFilter.DiscardInfo;
import org.emmef.logging.FormatLogger;

public class MinimumWindowNoiseLevelDetectionFilter implements NoiseLevelDetectionFilter {
	private static final FormatLogger logger = FormatLogger.getLogger(MinimumWindowNoiseLevelDetectionFilter.class);
	
	private final BucketScanner scanner;
	private final byte[] ignored;
	private int position;
	private final double maxRmsLevel;
	private final NrMeasurementValues nrMeasurementSettings;

	public MinimumWindowNoiseLevelDetectionFilter(BucketScanner scanner, DiscardInfo info, NrMeasurementValues nrMeasurementSettings) {
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
			scanner.addSample(source);
		}
		position++;
		return source;
	}
	
	@Override
	public double getNoiseLevel() {
		final double minimum;
		final double absMin = maxRmsLevel / nrMeasurementSettings.maxSnRatio;
		final double maxMin = maxRmsLevel / nrMeasurementSettings.minSnRatio;
		if (scanner.isWholeBucketScanned()) {
			minimum = Math.min(maxMin, Math.max(absMin, scanner.getMinimum()));
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

}
