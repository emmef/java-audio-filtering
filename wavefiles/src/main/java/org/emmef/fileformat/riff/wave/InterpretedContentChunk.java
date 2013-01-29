package org.emmef.fileformat.riff.wave;

import org.emmef.fileformat.iff.ContentChunk;
import org.emmef.serialization.Deserialize;
import org.emmef.serialization.Serialize;
import org.emmef.utils.Preconditions;

public class InterpretedContentChunk {
	private final ContentChunk chunk;

	public InterpretedContentChunk(ContentChunk chunk) {
		Preconditions.checkNotNull(chunk, "chunk");
		if (!chunk.getContentDefinition().preReadContent()) {
			throw new IllegalArgumentException("Must have preread content chunk, not " + chunk);
		}
		this.chunk = chunk;
	}
	
	public long getContentLength() {
		return chunk.getContentLength();
	}
	
	public final String getIdentifier() {
		return chunk.getIdentifier();
	}
	
	protected final ContentChunk getChunk() {
		return chunk;
	}
	
	protected final int getByteAt(int offset) {
		return 0xff & Deserialize.read8(getBuffer(offset, 1), offset);
	}
	
	protected final int getWordAt(int offset) {
		return 0xffff & Deserialize.read16LittleEndian(getBuffer(offset, 2), offset);
	}
	
	protected final long getDWordAt(int offset) {
		return 0xffffffffL & Deserialize.read32LittleEndian(getBuffer(offset, 4), offset);
	}
	
	protected final long getQWordAt(int offset) {
		return Deserialize.read64LittleEndian(getBuffer(offset, 8), offset);
	}
	
	protected final void setByteAt(int value, int offset) {
		Serialize.write08(value, getBuffer(offset, 1), offset);
	}
	
	protected final void setWordAt(int value, int offset) {
		Serialize.write16LittleEndian(value, getBuffer(offset, 2), offset);
	}
	
	protected final void setDWordAt(long value, int offset) {
		Serialize.write32LittleEndian((int)value, getBuffer(offset, 4), offset);
	}
	
	protected final void setWWordAt(long value, int offset) {
		Serialize.write64LittleEndian(value, getBuffer(offset, 8), offset);
	}

	private byte[] getBuffer(int offset, int bytes) {
		byte[] buffer = chunk.getContent();
		Preconditions.checkOffsetAndCount(buffer.length, offset, bytes);
		return buffer;
	}
}
