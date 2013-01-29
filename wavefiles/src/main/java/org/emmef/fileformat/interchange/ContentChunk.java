package org.emmef.fileformat.interchange;

public final class ContentChunk extends InterchangeChunk {

	private final byte[] content;

	ContentChunk(InterchangeDefinition definition, long contentLength, ChunkRelation relation, InterchangeChunk relationInstance, byte[] content) {
		super(definition, contentLength, relation, relationInstance);
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
	
	public byte[] getContent() {
		return content;
	}
	
	public final ContentDefinition getContentDefinition() {
		return (ContentDefinition)getDefinition();
	}
	
	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append(getContentDefinition().getIdentifier()).append("[");
		if (getContentDefinition().getEndian() != null) {
			text.append("endianness=").append(getContentDefinition().getEndian()).append("; ");
		}
		text.append("content-length=").append(getContentLength());
		if (getOffset() > 0) {
			text.append("; offset=").append(getOffset());
		}
		if (getContentDefinition().preReadContent()) {
			text.append("; pre-read");
		}
		text.append("]");
		if (getRelation() != null) {
			text.append(" ").append(getRelation()).append(" ").append(getRelationInstanceInternal());
		}
		
		return text.toString();
	}
}
