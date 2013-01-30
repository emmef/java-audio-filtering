package org.emmef.fileformat.iff;

public final class TypeChunk extends InterchangeChunk implements TypeChunkInfo {
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
	
	@Override
	public final TypeDefinition getDefinition() {
		return (TypeDefinition)super.getDefinition();
	}

	@Override
	public String getContentType() {
		return contentType;
	}
	
	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append(getDefinition().getIdentifier()).append("[");
		if (getDefinition().getEndian() != null) {
			text.append("endianness=").append(getDefinition().getEndian()).append("; ");
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
