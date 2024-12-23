package org.emmef.audio.noisereduction;

import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.buckets.Detection;
import org.emmef.audio.noisedetection.NoiseLevelDiscardFilter;
import org.emmef.audio.noisedetection.NrMeasurementSettings;
import org.emmef.audio.noisedetection.NrMeasurementValues;
import org.emmef.logging.FormatLogger;

public class NoiseLevelMarkerFilter implements ChainableFilter {
	private static final FormatLogger log = FormatLogger.getLogger(NoiseLevelMarkerFilter.class);
	public static final byte MARK = 2;
	public static final byte UNMARK = (byte)(0xFF ^ MARK);
	
	private final Detection scanner;
	private final byte[] markers;
	private final double noiseLevel;
	private final double threshold;
	private int position;
	private int marks;
	private boolean marking = false;

	NoiseLevelMarkerFilter(Detection bucketScanner, byte[] markers, double noiseLevel) {
		scanner = bucketScanner;
		this.markers = markers;
		this.noiseLevel = noiseLevel;
		threshold = noiseLevel * 1.01;
		reset();
	}

	@Override
	public Double getMetaData() {
		log.debug("Marked %d (%d buckets) for irregular noise measuremens (%1.1f dB) (bucket %d samples)", marks, marks / scanner.getBucketSize(), 20.0*Math.log10(noiseLevel), scanner.getBucketSize());
		return noiseLevel;
	}

	@Override
	public double filter(double input) {
		if ((markers[position] & NoiseLevelDiscardFilter.MARK) != 0) {
			position++;
			return input;
		}
		final double average = scanner.addSample(input);
		if (scanner.isWholeBucketScanned()) {
			if (marking) {
				if (average < threshold) {
					markers[position] |= MARK;
					marks++;
				}
				else {
					marking = false;
				}
			}
			else if (average < threshold) {
				int i = position;
				int j = 0;
				final int bucketSize = scanner.getBucketSize();
				while (j < bucketSize) {
					while ((markers[i] & NoiseLevelDiscardFilter.MARK) != 0) {
						i--;
					}
					markers[i] |= MARK;
					marks++;
					j++;
				}
				marking = true;
			}
//			else {
//				log.trace("Average %1.3e threshold %1.3e", average, threshold);
//			}
		}
		position++;
		return input;
	}

	@Override
	public void reset() {
		position = 0;
		scanner.reset();
		for (int i = 0; i < markers.length; i++) {
			markers[i] &= UNMARK;
		}
		marking = false;
		marks = 0;
	}

	public static class Factory implements FilterFactory {
		private final ThreadLocal<Detection> scanner = new ThreadLocal<>();
		private final NrMeasurementValues nrMeasurements;
		
		public Factory(long sampleRate, NrMeasurementSettings nrMeasurements) {
			this.nrMeasurements = nrMeasurements.withSampleRate(sampleRate);
			scanner.set(new BucketScanner(sampleRate, Factory.this.nrMeasurements.noiseWin));
		}

		@Override
		public ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers) {
			if (filterMetaData == null) {
				throw new NullPointerException("filterMetaData");
			}
			double noiseLevel = (Double)filterMetaData;
			
			return new NoiseLevelMarkerFilter(scanner.get(), markers, noiseLevel);
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
