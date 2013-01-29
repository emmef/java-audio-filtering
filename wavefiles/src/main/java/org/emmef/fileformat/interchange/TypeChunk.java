package org.emmef.fileformat.interchange;

public final class TypeChunk extends InterchangeChunk {
	private final String contentType;

	TypeChunk(InterchangeDefinition definition, long contentLength, ChunkRelation relation, InterchangeChunk relationInstance, String contentType) {
		super(definition, contentLength, relation, relationInstance);
		this.contentType = contentType;
	}
	
	public final TypeChunk getSibling() {
		if (getRelation() == ChunkRelation.SIBLING) {
			return (TypeChunk)getRelationInstanceInternal();
		}
		
		throw new IllegalStateException(this + " has no sibling");
	}
	
	public final ContentDefinition getDefinition() {
		return (ContentDefinition)getDefinitionUnspecialized();
	}

	public String getContentType() {
		return contentType;
	}
}
