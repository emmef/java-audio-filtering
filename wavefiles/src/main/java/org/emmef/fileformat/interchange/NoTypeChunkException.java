package org.emmef.fileformat.interchange;

public class NoTypeChunkException extends ChunkParseException {

	public NoTypeChunkException(String id) {
		super(id);
	}

}
