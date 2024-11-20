package org.emmef.audio.noisedetection;

import org.emmef.audio.buckets.Integration;
import org.emmef.audio.buckets.RmsDetection;
import org.emmef.audio.noisereduction.ChainableFilter;
import org.emmef.audio.noisereduction.FilterFactory;
import org.emmef.logging.FormatLogger;

import java.util.*;

public final class WeighedRmsLoudnessMeasurementFilter implements ChainableFilter {
	private static final FormatLogger logger = FormatLogger.getLogger(WeighedRmsLoudnessMeasurementFilter.class);

	public static final double ATTACK_SECONDS = 0.001;
	public static final double RELEASE_SECONDS = 0.020;


	private static final int STEPS_PER_DB = 10;
	private static final double MIN_DB = -120.0;
	private static final double MIN_DETECTION = Math.pow(10, MIN_DB / 20);

	private static final double LOG_TO_INDEX = 20.0 * STEPS_PER_DB;

	private final long sampleRate;
	private final NrMeasurementSettings nrMeasurements;
	private final double minFreq;
	private final double maxFreq;
	private final SortedMap<Integer, Long> loudnessFrequency = new TreeMap<>();
	private final RmsDetection rmsDetection;
	private int position = 0;
	private int start;
	private int end;

	public WeighedRmsLoudnessMeasurementFilter(long sampleRate, NrMeasurementSettings nrMeasurements, double minFreq, double maxFreq, byte[] markers) {
		this.sampleRate = sampleRate;
		this.nrMeasurements = nrMeasurements;
		this.minFreq = minFreq;
		this.maxFreq = maxFreq;
		this.rmsDetection = new RmsDetection(sampleRate, ATTACK_SECONDS, RELEASE_SECONDS, 0.0, null);
		this.start = (int) (sampleRate * nrMeasurements.skipStartSecs) + rmsDetection.windowSamples();
		this.end = markers.length - (int) (sampleRate * nrMeasurements.skipEndSecs);
	}

	@Override
	public double filter(double input) {
		if (position > start && position < end) {
			double detection = Math.max(MIN_DETECTION, rmsDetection.addSample(input));
			int bucketIndex = indexFromDetection(detection);
			Long frequency = loudnessFrequency.get(bucketIndex);
			if (frequency != null) {
				loudnessFrequency.put(bucketIndex, frequency + 1);
			}
			else {
				loudnessFrequency.put(bucketIndex, 1L);
			}
		}
		position++;
		return input;
	}

	@Override
	public void reset() {
		position = 0;
		rmsDetection.reset();
		loudnessFrequency.clear();
	}

	@Override
	public Double getMetaData() {
		if (loudnessFrequency.size() > 0) {
			int noiseIndex = getNoiseIndex();
			double noiseDb = dbFromIndex(noiseIndex);
			double noiseRms = rmsFromIndex(noiseIndex);
			int maxIndex = loudnessFrequency.lastKey();
			double maxDb = dbFromIndex(maxIndex);
			double snr = dbFromIndex(maxIndex - noiseIndex);

			logger.info("%s: %1.0fHz~%1.0fHz; noiseLevel=%1.1fdB; maxLevel=%1.1fdB; S/N=%1.1fdB; window=%1.3fs", this.getClass().getSimpleName(),
					minFreq, maxFreq, noiseDb, maxDb, snr, 1.0 * rmsDetection.windowSamples() / sampleRate);

			return noiseRms;
		}
		logger.info("%s: %1.0fHz~%1.0fHz; ESTIMATED noiseLevel=%1.1fdB; window=%1.3fs", this.getClass().getSimpleName(),
				minFreq, maxFreq, nrMeasurements.maxSnRatioDb, 1.0 * rmsDetection.windowSamples() / sampleRate);
		return Math.pow(10, -0.02 * nrMeasurements.maxSnRatioDb);
	}

	int getNoiseIndex() {
		int maxRmsIndex = loudnessFrequency.lastKey();

		// Creates a 10-step-per-db array of occurrence frequencies
		int rangeStart  = loudnessFrequency.firstKey();
		int rangeEnd = loudnessFrequency.lastKey() ;
		int rangeSize = 1 + rangeEnd - rangeStart;
		double range[] = new double[rangeSize];
		for (var entry : loudnessFrequency.entrySet()) {
			range[entry.getKey() - rangeStart] = entry.getValue();
		}

		// Applies a zero-phase low-pass filter on the array of occurrence frequencies
		Integration.Factors integration = new Integration.Factors(10);
		double integrated;
		integrated = 0;
		for (int i = 0; i < range.length; i++) {
			integrated = integration.integrated(integrated, range[i]);
			range[i] = integrated;
		}
		integrated = 0;
		for (int i = range.length - 1; i >= 0; i--) {
			integrated = integration.integrated(integrated, range[i]);
			range[i] = integrated;
		}

		// Detects the first maximum between the minimum and maximum allowed noise levels
		int snrIndexMin = maxRmsIndex - (int) (nrMeasurements.maxSnRatioDb * STEPS_PER_DB) - rangeStart;
		int snrIndexMax = maxRmsIndex - (int) (nrMeasurements.minSnRatioDb * STEPS_PER_DB) - rangeStart;

		int i = snrIndexMin;
		double previous = range[i++];
		int end = Math.min(range.length, snrIndexMax + 1);
		while (i < end) {
			double f = range[i];
			if (f < previous) {
				break;
			}
			previous = f;
			i++;
		}
		// Returns the index of the maximum
		return i + rangeStart - 1;
	}

	private static int indexFromDetection(double detection) {
		return (int) Math.round(Math.log10(detection) * LOG_TO_INDEX);
	}

	private static double rmsFromIndex(int index) {
		return Math.pow(10, 0.005 * index);
	}

	private static double dbFromIndex(int index) {
		return 0.1 * index;
	}

	public static class Factory implements FilterFactory {
		private final long sampleRate;
		private final NrMeasurementSettings nrMeasurements;

		public Factory(long sampleRate, NrMeasurementSettings nrMeasurements) {
			this.sampleRate = sampleRate;
			this.nrMeasurements = nrMeasurements;
		}

		@Override
		public ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers) {
			return new WeighedRmsLoudnessMeasurementFilter(sampleRate, nrMeasurements, minFreq, maxFreq, markers);
		}
	}
}
