package org.emmef.audio.noisereduction;

import org.emmef.audio.buckets.BucketScanner;
import org.emmef.audio.noisedetection.NoiseLevelDiscardFilter;
import org.emmef.audio.noisedetection.NrMeasurementSettings;
import org.emmef.audio.noisedetection.NrMeasurementValues;
import org.emmef.logging.Logger;

public class NoiseLevelMarkerFilter implements ChainableFilter {
	private static final Logger log = Logger.getDefault();
	public static final byte MARK = 2;
	public static final byte UNMARK = (byte)(0xFF ^ MARK);
	
	private final BucketScanner scanner;
	private final byte[] markers;
	private final double noiseLevel;
	private final double threshold;
	private int position;
	private int marks;
	private boolean marking = false;

	NoiseLevelMarkerFilter(BucketScanner bucketScanner, byte[] markers, double noiseLevel) {
		this.scanner = bucketScanner;
		this.markers = markers;
		this.noiseLevel = noiseLevel;
		this.threshold = noiseLevel * noiseLevel * 1.01;
		reset();
	}

	public Double getMetaData() {
		log.debug("Marked %d (%d buckets) for irregular noise measuremens (%1.1f dB) (bucket %d samples)", marks, marks / scanner.getBucketSize(), 20.0*Math.log10(noiseLevel), scanner.getBucketSize());
		return noiseLevel;
	}

	public double filter(double input) {
		if ((markers[position] & NoiseLevelDiscardFilter.MARK) != 0) {
			position++;
			return input;
		}
		scanner.addUnscaledSample(input * input);
		if (scanner.isWholeBucketScanned()) {
			final double average = scanner.getAverage();
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
		private final ThreadLocal<BucketScanner> scanner;
		private final NrMeasurementValues nrMeasurements;
		
		public Factory(int samplerate, NrMeasurementSettings nrMeasurements) {
			this.nrMeasurements = nrMeasurements.withSampleRate(samplerate);
			scanner = new ThreadLocal<BucketScanner>() {@Override
			protected BucketScanner initialValue() {
				return new BucketScanner(Factory.this.nrMeasurements.noiseWinwSamples, BucketScanner.SCALE_48BIT);
			}};
		}

		public ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers) {
			if (filterMetaData == null) {
				throw new NullPointerException("filterMetaData");
			}
			double noiseLevel = (Double)filterMetaData;
			
			return new NoiseLevelMarkerFilter(scanner.get(), markers, noiseLevel);
		}

		public int getEndOffset() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getLatency() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getStartOffset() {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
