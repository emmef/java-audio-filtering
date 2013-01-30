package org.emmef.fileformat.iff;

public interface TypeChunkInfo extends ChunkInfo {
	String getContentType();
	@Override
	TypeDefinition getDefinition();

}
