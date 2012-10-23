package org.emmef.audio.noisereduction;

public interface FilterFactory {
	ChainableFilter createFilter(Object filterMetaData, double minFreq, double maxFreq, byte[] markers);
	int getLatency();
	int getStartOffset();
	int getEndOffset();
}
