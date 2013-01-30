package org.emmef.fileformat.iff;

public interface ChunkInfo extends DefinitionInfo {
	long getContentLength();

	InterchangeDefinition getDefinition();
}
