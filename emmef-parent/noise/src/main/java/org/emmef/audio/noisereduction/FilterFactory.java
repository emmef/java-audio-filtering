package org.emmef.audio.noisereduction;

public interface FilterFactory {
	ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers);

	default int getLatency() {
		return 0;
	}
	default int getStartOffset() {
		return 0;
	}
	default int getEndOffset() {
		return 0;
	}
}
