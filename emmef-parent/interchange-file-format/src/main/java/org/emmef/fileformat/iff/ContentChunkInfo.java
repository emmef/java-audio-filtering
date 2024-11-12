package org.emmef.fileformat.iff;


public interface ContentChunkInfo extends ChunkInfo {
	@Override
	ContentDefinition getDefinition();
	
	int getByteAt(int offset);
	int getWordAt(int offset);
	long getDWordAt(int offset);
	long getQWordAt(int offset);
	void setByteAt(int value, int offset);
	void setWordAt(int value, int offset);
	void setDWordAt(long value, int offset);
	void setWWordAt(long value, int offset);
}
