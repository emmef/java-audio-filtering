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
	
	public final ContentDefinition getDefinition() {
		return (ContentDefinition)getDefinitionUnspecialized();
	}
}
