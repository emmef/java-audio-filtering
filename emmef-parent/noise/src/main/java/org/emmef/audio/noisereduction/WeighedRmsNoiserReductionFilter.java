package org.emmef.audio.noisereduction;

import org.emmef.audio.noisedetection.NrMeasurementSettings;

public class WeighedRmsNoiserReductionFilter implements ChainableFilter {
	@Override
	public double filter(double input) {
		return 0;
	}

	@Override
	public void reset() {

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
			return null;
		}
	}
}
