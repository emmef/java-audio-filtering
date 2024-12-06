package org.emmef.audio.noisereduction;

import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.noisedetection.NrMeasurementSettings;
import org.emmef.audio.noisedetection.NrMeasurementValues;
import org.emmef.logging.FormatLogger;

public class IrregularNoiseDetectionFilter implements ChainableFilter {
	private static final FormatLogger logger = FormatLogger.getLogger(IrregularNoiseDetectionFilter.class);
	private final BucketScanner scanner;
	private final double noiseLevel;
	private final byte[] markers;
	private int position;

	IrregularNoiseDetectionFilter(BucketScanner scanner, double noiseLevel, byte[] markers) {
		this.scanner = scanner;
		this.noiseLevel = noiseLevel;
		this.markers = markers;
		reset();
	}

	@Override
	public double filter(double input) {
		if ((markers[position] & NoiseLevelMarkerFilter.MARK) != 0) {
			scanner.addSample(input);
		}
		position++;
		return input;
	}

	@Override
	public void reset() {
		position = 0;
		scanner.reset();
	}
	
	public double getNoiseLevel() {
		final double newNoiseLevel;
		final double correction;
		if (scanner.isWholeBucketScanned()) {
			newNoiseLevel = Math.max(scanner.getMaximum(), noiseLevel);
			correction = newNoiseLevel / noiseLevel;
		}
		else {
			newNoiseLevel = noiseLevel;
			correction = 1.0;
		}
		if (correction > 1.0) {
			logger.info("Irregular noise level correction: %+1.1f dB (bucket %d samples)", 20*Math.log10(correction), scanner.getBucketSize());
		}
		
		return newNoiseLevel;
	}

	
	public static class Factory implements FilterFactory {
		private final NrMeasurementValues nrMeasurements;
		private final ThreadLocal<BucketScanner> scanner = new ThreadLocal<BucketScanner>();
		private final RatedTimings ratedTimings;

		public Factory(NrMeasurementSettings nrMeasurements, RatedTimings ratedTimings) {
			this.ratedTimings = ratedTimings;
			this.nrMeasurements = nrMeasurements.withSampleRate((int)ratedTimings.sampleRate);
		}

		@Override
		public ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers) {
			if (filterMetaData == null) {
				throw new NullPointerException("filterMetaData");
			}
			final BucketScanner newScanner = new BucketScanner(ratedTimings.sampleRate, ratedTimings.getEffectiveMeasurementTime(nrMeasurements, minFreq));
			scanner.set(newScanner);
			return new IrregularNoiseDetectionFilter(newScanner, ((Double)filterMetaData).doubleValue(), markers);
		}

		@Override
		public int getEndOffset() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getLatency() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getStartOffset() {
			// TODO Auto-generated method stub
			return 0;
		}

	}
}
