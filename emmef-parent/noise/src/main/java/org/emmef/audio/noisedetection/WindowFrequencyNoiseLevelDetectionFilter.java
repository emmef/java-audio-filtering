package org.emmef.audio.noisedetection;

import org.emmef.audio.buckets.BucketScanner;
import org.emmef.logging.FormatLogger;

public class WindowFrequencyNoiseLevelDetectionFilter implements NoiseLevelDetectionFilter {
	private static final FormatLogger logger = FormatLogger.getLogger(WindowFrequencyNoiseLevelDetectionFilter.class);

	public static final int ACCURACY_IN_STEPS_PER_DB = 10;
	private final int loudnessLevels;

	private static double dbFromValue(double x) {
		return 20.0 * Math.log10(x);
	}

	private static int stepFromValue(double x) {
		return (int) Math.round(20.0 * ACCURACY_IN_STEPS_PER_DB * Math.log10(x));
	}

	private static double valueFromDb(double x) {
		return 20.0 * Math.pow(10, 0.05 * x);
	}

	private final BucketScanner scanner;
	private final long[] frequency;
	private final double maxRmsLevel;
	private final NrMeasurementValues nrMeasurementSettings;
	private final double minRmsLevel;

	public WindowFrequencyNoiseLevelDetectionFilter(BucketScanner scanner, double maxRmsValue, NrMeasurementValues nrMeasurementSettings) {
		this.scanner = scanner;
		this.nrMeasurementSettings = nrMeasurementSettings;
		this.maxRmsLevel = maxRmsValue;
		minRmsLevel = maxRmsLevel / nrMeasurementSettings.maxSnRatio;
		loudnessLevels = (int) Math.round(ACCURACY_IN_STEPS_PER_DB * dbFromValue(nrMeasurementSettings.maxSnRatio) + 0.5);
		frequency = new long[loudnessLevels];
		scanner.reset();
	}

	@Override
	public void reset() {
		scanner.reset();
		for (int i = 0; i < loudnessLevels; i++) {
			frequency[i] = 0;
		}
	}

	@Override
	public double filter(double source) {
		scanner.addUnscaledSample(source);
		if (scanner.isWholeBucketScanned()) {
			double relativeRms = Math.max(scanner.getRootMeanSquared(), minRmsLevel) / minRmsLevel;
			int index = Math.min(stepFromValue(relativeRms), loudnessLevels - 1);
			frequency[index]++;
		}

		return source;
	}

	@Override
	public double getNoiseLevel() {
		final double minimum;
		if (scanner.isWholeBucketScanned()) {
			final double measuredLevel = minRmsLevel * valueFromDb((double) getLowestLevelMaximumFrequency() / (double) ACCURACY_IN_STEPS_PER_DB);
			final double maxMin = maxRmsLevel / nrMeasurementSettings.minSnRatio;
			minimum = Math.min(maxMin, measuredLevel);
		} else {
			minimum = minRmsLevel;
		}

		logger.trace("%s: noiseLevel=%1.1fdB; S/N=%1.1fdB; window=%1.3fs", this, 20 * Math.log10(minimum), 20 * Math.log10(maxRmsLevel / minimum), 1.0 * scanner.getBucketSize() / nrMeasurementSettings.sampleRate);

		return minimum;
	}

	private int getLowestLevelMaximumFrequency() {
		long maxFrequency = frequency[0];
		for (int slice = 1; slice < loudnessLevels; slice++) {
			long f = frequency[slice];
			if (f > maxFrequency) {
				return slice;
			} else if (slice == 1) {
				return 0;
			}
		}
		return loudnessLevels - 1;
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
