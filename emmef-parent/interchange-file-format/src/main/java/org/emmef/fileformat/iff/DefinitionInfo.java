package org.emmef.fileformat.iff;

import org.emmef.samples.serialization.Endian;

public interface DefinitionInfo {
	final long MAX_CONTENT_LENGTH = 0xffffffffL;
	/**
	 * This is the offset of content inside a chunk, which is always the
	 * identifier (4 ASCII-bytes) plus the chunk-length (4 byte word).
	 */
	final long CONTENT_RELATIVE_OFFSET = 8;

	public abstract Endian getEndian();

	public abstract long childRelativeOffset();

	public abstract String getIdentifier();

}
