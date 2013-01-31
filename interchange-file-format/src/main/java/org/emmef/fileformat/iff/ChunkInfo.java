package org.emmef.fileformat.iff;

public interface ChunkInfo extends DefinitionInfo {
	final long MAX_PREREAD_LENGTH = 0xffffL;
	
	long getContentLength();

	InterchangeDefinition getDefinition();
}
