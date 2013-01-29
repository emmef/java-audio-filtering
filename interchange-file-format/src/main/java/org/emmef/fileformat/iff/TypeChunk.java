package org.emmef.fileformat.iff;

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
	
	public final TypeDefinition getTypeDefinition() {
		return (TypeDefinition)getDefinition();
	}

	public String getContentType() {
		return contentType;
	}
	
	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append(getTypeDefinition().getIdentifier()).append("[");
		if (getTypeDefinition().getEndian() != null) {
			text.append("endianness=").append(getTypeDefinition().getEndian()).append("; ");
		}
		text.append("content-type=").append(getContentType());
		text.append("; content-length=").append(getContentLength());
		if (getOffset() > 0) {
			text.append("; offset=").append(getOffset());
		}
		text.append("]");
		if (getRelation() != null) {
			text.append(" ").append(getRelation()).append(" ").append(getRelationInstanceInternal());
		}
		
		return text.toString();
	}
}
