package org.emmef.fileformat.iff;

import org.emmef.samples.serialization.Deserialize;
import org.emmef.samples.serialization.Serialize;
import org.emmef.utils.Checks;

public final class ContentChunk extends InterchangeChunk implements ContentChunkInfo {

	private final byte[] content;

	ContentChunk(InterchangeDefinition definition, long contentLength, ChunkRelation relation, InterchangeChunk relationInstance, byte[] content, boolean readOnly) {
		super(definition, contentLength, relation, relationInstance, readOnly);
		this.content = content;
	}
	
	public final InterchangeChunk getParent() {
		if (getRelation() == ChunkRelation.PARENT) {
			return getRelationInstanceInternal();
		}
		
		throw new IllegalStateException(this + " has no parent");
	}
	
	public final ContentChunk getSibling() {
		if (getRelation() == ChunkRelation.SIBLING) {
			return (ContentChunk)getRelationInstanceInternal();
		}
		
		throw new IllegalStateException(this + "has no sibling");
	}
	
	@Override
	public final ContentDefinition getDefinition() {
		return (ContentDefinition)super.getDefinition();
	}
	
	@Override
	public final int getByteAt(int offset) {
		return 0xff & Deserialize.read8(getBuffer(offset, 1, false), offset);
	}
	
	@Override
	public final int getWordAt(int offset) {
		return 0xffff & Deserialize.read16LittleEndian(getBuffer(offset, 2, false), offset);
	}
	
	@Override
	public final long getDWordAt(int offset) {
		return 0xffffffffL & Deserialize.read32LittleEndian(getBuffer(offset, 4, false), offset);
	}
	
	@Override
	public final long getQWordAt(int offset) {
		return Deserialize.read64LittleEndian(getBuffer(offset, 8, false), offset);
	}
	
	@Override
	public final void setByteAt(int value, int offset) {
		Serialize.write08(value, getBuffer(offset, 1, true), offset);
	}
	
	@Override
	public final void setWordAt(int value, int offset) {
		Serialize.write16LittleEndian(value, getBuffer(offset, 2, true), offset);
	}
	
	@Override
	public final void setDWordAt(long value, int offset) {
		Serialize.write32LittleEndian((int)value, getBuffer(offset, 4, true), offset);
	}
	
	@Override
	public final void setWWordAt(long value, int offset) {
		Serialize.write64LittleEndian(value, getBuffer(offset, 8, true), offset);
	}
	
	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append(getDefinition().getIdentifier()).append("[");
		if (getDefinition().getEndian() != null) {
			text.append("endianness=").append(getDefinition().getEndian()).append("; ");
		}
		text.append("content-length=").append(getContentLength());
		if (getOffset() > 0) {
			text.append("; offset=").append(getOffset());
		}
		if (getDefinition().preReadContent()) {
			text.append("; pre-read");
		}
		text.append("]");
		if (getRelation() != null) {
			text.append(" ").append(getRelation()).append(" ").append(getRelationInstanceInternal());
		}
		
		return text.toString();
	}

	private byte[] getBuffer(int offset, int bytes, boolean forWriting) {
		Checks.checkOffsetAndCount(content.length, offset, bytes);
		if (forWriting && isReadOnly()) {
			throw new IllegalStateException(this + ": read-only: cannot write data");
		}
		return content;
	}
}
