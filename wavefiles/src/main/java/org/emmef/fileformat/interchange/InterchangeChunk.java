package org.emmef.fileformat.interchange;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.emmef.samples.serialization.Deserialize;
import org.emmef.samples.serialization.Endian;
import org.emmef.utils.Preconditions;


public abstract class InterchangeChunk {
	public static final long MAX_CONTENT_LENGTH = 0xffffffffL;
	public static final long MAX_READ_LENGTH = 0xffffL;
	
	private final InterchangeDefinition definition;
	private final long contentLength;
	private final ChunkRelation relation;
	private final InterchangeChunk relationInstance;
	
	public static final TypeBuilder typeBuilder(TypeDefinition definition) {
		return new TypeBuilder(definition);
	}
	
	public static final ContentBuilder contentBuilder(ContentDefinition definition) {
		return new ContentBuilder(definition);
	}
	
	public static final TypeBuilder typeBuilder(TypeDefinition definition, TypeChunk siblingTypeChunk) {
		TypeBuilder typeBuilder = new TypeBuilder(definition);
		return typeBuilder.sibling(siblingTypeChunk);
	}
	
	public static final ContentBuilder contentBuilder(ContentDefinition definition, TypeChunk parent) {
		ContentBuilder contentBuilder = new ContentBuilder(definition);
		return contentBuilder.parent(parent);
	}
	
	public static final ContentBuilder contentBuilder(ContentDefinition definition, ContentChunk sibling) {
		ContentBuilder contentBuilder = new ContentBuilder(definition);
		return contentBuilder.sibling(sibling);
	}
	
	InterchangeChunk(InterchangeDefinition definition, long contentLength, ChunkRelation relation, InterchangeChunk relationInstance) {
		this.definition = definition;
		this.contentLength = contentLength;
		this.relation = relation;
		this.relationInstance = relationInstance;
	}
	
	public final long getContentLength() {
		return contentLength;
	}
	
	public final ChunkRelation getRelation() {
		return relation;
	}
	
	public final long getOffset() {
		if (relation == ChunkRelation.SIBLING) {
			return
					InterchangeDefinition.CONTENT_RELATIVE_OFFSET +
					relationInstance.getContentLength() +
					relationInstance.getOffset();
		}
		if (relation == ChunkRelation.PARENT) {
			return
					InterchangeDefinition.CONTENT_RELATIVE_OFFSET +
					relationInstance.getDefinitionUnspecialized().childRelativeOffset() +
					relationInstance.getOffset();
		}
		return 0;
	}
	
	public Endian getEndian() {
		Endian endian = definition.getEndian();
		if (endian != null) {
			return endian;
		}
		if (relation == ChunkRelation.PARENT) {
			return relationInstance.getDefinitionUnspecialized().getEndian();
		}
		
		return null;
	}

	
	@Override
	public String toString() {
		if (definition instanceof TypeDefinition) {
			return "Type [" + definition.getIdentifier() + "";
		}
		else {
			return super.toString();
		}
	}
	
	protected final InterchangeChunk getRelationInstanceInternal() {
		return relationInstance;
	}
	
	protected final InterchangeDefinition getDefinitionUnspecialized() {
		return definition;
	}
	

	public static abstract class AbstractBuilder<T extends InterchangeChunk, V extends InterchangeDefinition> {
		private final V definition;
		private ChunkRelation relation;
		private InterchangeChunk instance;
		private long contentLength = -1;

		AbstractBuilder(V definition) {
			this.definition = Preconditions.checkNotNull(definition, "Chunk definition");
		}
		
		public abstract T build();
		
		public final AbstractBuilder<T, V> contentLength(long length) {
			if (contentLength != -1) {
				throw new IllegalStateException("Offset" + " already set for [" + definition + "]");
			}
			if (length < 0 || length > MAX_CONTENT_LENGTH) {
				throw new IllegalArgumentException("Offset" + "must lie within [0.." + MAX_CONTENT_LENGTH + "]");
			}
			contentLength = length;
			
			return this;
		}
		
		public final V getDefinition() {
			return definition;
		}
		
		public final long getContentLength() {
			return contentLength;
		}
		
		public final AbstractBuilder<T, V> readContentLength(InputStream stream) throws IOException {
			if (contentLength != -1) {
				throw new IllegalStateException("Offset" + " already set for [" + definition + "]");
			}
			switch (getEndian()) {
			case BIG:
				contentLength = 0xffffffffL & Deserialize.read32BigEndian(stream);
				break;
			case LITTLE:
				contentLength = 0xffffffffL & Deserialize.read32LittleEndian(stream);
				break;
			}
			
			return this;
		}
		
		protected ChunkRelation getRelation() {
			return relation;
		}
		
		protected InterchangeChunk getInstance() {
			return instance;
		}
		
		protected Endian getEndian() {
			Endian endian = definition.getEndian();
			if (endian == null && relation == ChunkRelation.PARENT) {
				endian = instance.getDefinitionUnspecialized().getEndian();
			}
			if (endian == null) {
				throw new IllegalStateException("Endianness not defined: cannot read chunk length");
			}
			
			return null;
		}
		
		protected void setRelation(ChunkRelation relation, InterchangeChunk relationInstance) {
			Preconditions.checkNotNull(instance, "Relation instance");
			if (relation != null) {
				throw new IllegalStateException("Relation already set: " + relation + " " + relationInstance);
			}
			this.relation = relation;
			instance = relationInstance;
		}
	}
	
	public static final class TypeBuilder extends AbstractBuilder<TypeChunk, TypeDefinition> {
		private String contentType;

		TypeBuilder(TypeDefinition definition) {
			super(definition);
		}
		
		@Override
		public TypeChunk build() {
			if (contentType == null) {
				throw new IllegalStateException(getDefinition() + ": content type not set");
			}
			return new TypeChunk(getDefinition(), getContentLength(), getRelation(), getInstance(), contentType);
		}
		
		public TypeBuilder sibling(TypeChunk sibling) {
			setRelation(ChunkRelation.SIBLING, sibling);
			
			return this;
		}
		
		public TypeBuilder readContentLengthAndType(InputStream stream) throws IOException {
			checkContentNotSet();
			readContentLength(stream);
			contentType = InterchangeHelper.createIdentifier(stream);
			
			return this;
		}
		
		public TypeBuilder setContentType(String contentType) {
			checkContentNotSet();
			contentType = InterchangeHelper.verifiedChunkIdentifier(contentType);
			
			return this;
		}
		
		public String getContentType() {
			return contentType;
		}

		private void checkContentNotSet() {
			if (contentType != null) {
				throw new IllegalStateException(getDefinition() + ": content-type already set: " + contentType);
			}
		}
	}
	
	public static final class ContentBuilder extends AbstractBuilder<ContentChunk, ContentDefinition> {

		private byte[] content;

		ContentBuilder(ContentDefinition definition) {
			super(definition);
		}

		@Override
		public ContentChunk build() {
			if (getDefinition().preReadContent()) {
				allocateContent();
			}
			return new ContentChunk(getDefinition(), getContentLength(), getRelation(), getInstance(), content);
		}
		
		public ContentBuilder sibling(ContentChunk sibling) {
			setRelation(ChunkRelation.SIBLING, sibling);
			
			return this;
		}
		
		public ContentBuilder parent(InterchangeChunk parent) {
			setRelation(ChunkRelation.PARENT, parent);
			
			return this;
		}
		
		public ContentBuilder readContent(InputStream stream) throws IOException {
			if (!getDefinition().preReadContent()) {
				return this;
			}
			createContentBuffer();
			
			if (stream.read(content) < content.length) {
				throw new EOFException(getDefinition() + ": couldn't read " + content.length + " bytes of content");
			}
			
			return this;
		}

		private void createContentBuffer() {
			if (content != null) {
				throw new IllegalStateException(getDefinition() + ": content already ead");
			}
			allocateContent();
		}

		private void allocateContent() {
			if (content != null) {
				return;
			}
			if (getContentLength() > MAX_READ_LENGTH) {
				throw new IllegalStateException(getDefinition() + ": cannot read " + getContentLength() + " bytes of content: maximum is " + MAX_READ_LENGTH + " bytes");
			}
			content = new byte[(int)getContentLength()];
		}
	}
}