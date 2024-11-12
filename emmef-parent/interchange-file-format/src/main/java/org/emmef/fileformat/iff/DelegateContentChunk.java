package org.emmef.fileformat.iff;

import org.emmef.samples.serialization.Endian;


public class DelegateContentChunk implements ContentChunkInfo {
	
	private final ContentChunk chunk;

	public DelegateContentChunk(ContentChunk chunk) {
		this.chunk = chunk;
	}
	
	public ContentChunk getChunk() {
		return chunk;
	}

	@Override
	public final ContentDefinition getDefinition() {
		return chunk.getDefinition();
	}

	@Override
	public final int getByteAt(int offset) {
		return chunk.getByteAt(offset);
	}

	@Override
	public final int getWordAt(int offset) {
		return chunk.getWordAt(offset);
	}

	@Override
	public final long getDWordAt(int offset) {
		return chunk.getDWordAt(offset);
	}

	@Override
	public final long getQWordAt(int offset) {
		return chunk.getQWordAt(offset);
	}

	@Override
	public final void setByteAt(int value, int offset) {
		chunk.setByteAt(value, offset);
	}

	@Override
	public final void setWordAt(int value, int offset) {
		chunk.setWordAt(value, offset);
	}

	@Override
	public final String getIdentifier() {
		return chunk.getIdentifier();
	}

	@Override
	public final long getContentLength() {
		return chunk.getContentLength();
	}

	@Override
	public final void setDWordAt(long value, int offset) {
		chunk.setDWordAt(value, offset);
	}

	@Override
	public final long childRelativeOffset() {
		return chunk.childRelativeOffset();
	}

	@Override
	public final void setWWordAt(long value, int offset) {
		chunk.setWWordAt(value, offset);
	}

	@Override
	public Endian getEndian() {
		return chunk.getEndian();
	}
}
