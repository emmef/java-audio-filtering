package org.emmef.fileformat.iff;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.emmef.samples.serialization.Deserialize;
import org.emmef.samples.serialization.Endian;
import org.emmef.samples.serialization.Serialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;


public abstract class InterchangeChunk implements ChunkInfo {
	private static final Logger log = LoggerFactory.getLogger(InterchangeChunk.class);
	private final InterchangeDefinition definition;
	private long contentLength;
	private final ChunkRelation relation;
	private final InterchangeChunk relationInstance;
	private final boolean readOnly;
	
	public static final TypeBuilder typeBuilder(TypeDefinition definition, boolean readOnly) {
		return new TypeBuilder(definition, readOnly);
	}
	
	public static final ContentBuilder contentBuilder(ContentDefinition definition, boolean readOnly) {
		return new ContentBuilder(definition, readOnly);
	}
	
	public static final TypeBuilder typeBuilder(TypeDefinition definition, TypeChunk siblingTypeChunk, boolean readOnly) {
		TypeBuilder typeBuilder = new TypeBuilder(definition, readOnly);
		return typeBuilder.sibling(siblingTypeChunk);
	}
	
	public static final ContentBuilder contentBuilder(ContentDefinition definition, TypeChunk parent, boolean readOnly) {
		ContentBuilder contentBuilder = new ContentBuilder(definition, readOnly);
		return contentBuilder.parent(parent);
	}
	
	public static final ContentBuilder contentBuilder(ContentDefinition definition, ContentChunk sibling, boolean readOnly) {
		ContentBuilder contentBuilder = new ContentBuilder(definition, readOnly);
		return contentBuilder.sibling(sibling);
	}
	
	InterchangeChunk(InterchangeDefinition definition, long contentLength, ChunkRelation relation, InterchangeChunk relationInstance, boolean readOnly) {
		this.definition = definition;
		this.contentLength = contentLength;
		this.relation = relation;
		this.relationInstance = relationInstance;
		this.readOnly = readOnly;
		if (!isReadOnly()) {
			addDeltaToParent(contentLength + CONTENT_RELATIVE_OFFSET);
		}
	}
	
	@Override
	public final String getIdentifier() {
		return definition.getIdentifier();
	}
	
	@Override
	public final long getContentLength() {
		return contentLength;
	}
	
	@Override
	public final long childRelativeOffset() {
		return getDefinition().childRelativeOffset();
	}

	public final ChunkRelation getRelation() {
		return relation;
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public final long getOffset() {
		if (relation == ChunkRelation.SIBLING) {
			return
					DefinitionInfo.CONTENT_RELATIVE_OFFSET +
					relationInstance.getContentLength() +
					relationInstance.getOffset();
		}
		if (relation == ChunkRelation.PARENT) {
			return
					DefinitionInfo.CONTENT_RELATIVE_OFFSET +
					relationInstance.getDefinition().childRelativeOffset() +
					relationInstance.getOffset();
		}
		return 0;
	}
	
	@Override
	public Endian getEndian() {
		Endian endian = definition.getEndian();
		if (endian != null) {
			return endian;
		}
		if (relation != null) {
			return relationInstance.getEndian();
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
	
	@Override
	public InterchangeDefinition getDefinition() {
		return definition;
	}
	
	public void write(OutputStream stream) throws IOException {
		writeId(stream, getIdentifier());
		if (getEndian() == Endian.BIG) {
			Serialize.write32BigEndian((int)getContentLength(), stream);
		}
		else {
			Serialize.write32LittleEndian((int)getContentLength(), stream);
		}
	}
	
	public void setContentLength(long newLength) {
		if (readOnly) {
			throw new IllegalStateException(this + ": read-only: cannot change content length");
		}
		if (newLength == contentLength) {
			return;
		}
		definition.validContentLength(newLength);
		
		addDeltaToParent(newLength - contentLength);
		
		contentLength = newLength;
	}

	private void addDeltaToParent(long delta) {
		InterchangeChunk parent = getParentChunk();
		if (parent != null) {
			long relationLength = parent.getContentLength();
			long newRelationLength = relationLength + delta;
			log.debug("{}: Add {} to parent {}", getIdentifier(), delta, parent);
			parent.setContentLength(newRelationLength);
		}
	}

	private InterchangeChunk getParentChunk() {
		InterchangeChunk related = relationInstance;
		ChunkRelation chunkRelation = relation;
		while (related != null && chunkRelation != ChunkRelation.PARENT) {
			chunkRelation = related.relation;
			related = related.relationInstance;
		}
		return related;
	}

	static void writeId(OutputStream stream, String identifier) throws IOException {
		for (int i = 0; i < 4; i++) {
			stream.write(identifier.charAt(i));
		}
	}
	
	public static abstract class AbstractBuilder<T extends InterchangeChunk, V extends InterchangeDefinition> {
		private final V definition;
		private ChunkRelation relation;
		private InterchangeChunk instance;
		private long contentLength = -1;
		private final boolean readOnly;

		AbstractBuilder(V definition, boolean readOnly) {
			this.readOnly = readOnly;
			this.definition = Preconditions.checkNotNull(definition, "Chunk definition");
		}
		
		public abstract T build();
		
		final void setContentLength(long length) {
			if (contentLength != -1) {
				throw new IllegalStateException("Offset" + " already set for [" + definition + "]");
			}
			
			assignContentLength(definition.validContentLength(length));
		}
		
		public final V getDefinition() {
			return definition;
		}
		
		public final long getContentLength() {
			return contentLength;
		}
		
		void readContentLengthInternal(InputStream stream) throws IOException {
			if (contentLength != -1) {
				throw new IllegalStateException("Offset" + " already set for [" + definition + "]");
			}
			long readLength;
			switch (getEndian()) {
			case BIG:
				readLength = 0xffffffffL & Deserialize.read32BigEndian(stream);
				break;
			case LITTLE:
				readLength = 0xffffffffL & Deserialize.read32LittleEndian(stream);
				break;
			default:
				throw new IllegalStateException("Invalid endianness " + getEndian());
			}
			
			assignContentLength(readLength);
		}

		private void assignContentLength(long newLength) {
			if (definition instanceof TypeDefinition && newLength < 4) {
				throw new IllegalStateException(definition + ": cannot set length smaller than 4, as type chunks always contain a content-type specifier");
			}
			contentLength = newLength;
		}
		
		protected ChunkRelation getRelation() {
			return relation;
		}
		
		protected InterchangeChunk getInstance() {
			return instance;
		}
		
		protected Endian getEndian() {
			Endian endian = definition.getEndian();
			
			if (endian == null && relation != null) {
				endian = instance.getEndian();
			}
			
			if (endian == null) {
				throw new IllegalStateException(getDefinition() + ": endianness not defined: cannot read chunk length");
			}
			
			return endian;
		}
		
		protected void setRelation(ChunkRelation relation, InterchangeChunk relationInstance) {
			Preconditions.checkNotNull(relationInstance, "Relation instance");
			if (this.relation != null) {
				throw new IllegalStateException(getDefinition() + "Relation already set: " + relation + " " + relationInstance);
			}
			if (!getReadOnly() && relation == ChunkRelation.PARENT && relationInstance.isReadOnly()) {
				throw new IllegalArgumentException(getDefinition() + ": cannot set read-only relation " + relationInstance);
			}
			this.relation = relation;
			instance = relationInstance;
		}
		
		boolean getReadOnly() {
			return readOnly;
		}
	}
	
	public static final class TypeBuilder extends AbstractBuilder<TypeChunk, TypeDefinition> {
		private String contentType;

		TypeBuilder(TypeDefinition definition, boolean readOnly) {
			super(definition, readOnly);
		}
		
		@Override
		public TypeChunk build() {
			if (contentType == null) {
				throw new IllegalStateException(getDefinition() + ": content type not set");
			}
			if (getContentLength() <= 0 && !getReadOnly()) {
				/* Set the length of the content-type identifier as current content-length */
				return new TypeChunk(getDefinition(), 4 , getRelation(), getInstance(), contentType, getReadOnly());
			}
			return new TypeChunk(getDefinition(), getContentLength(), getRelation(), getInstance(), contentType, getReadOnly());
		}
		
		public TypeBuilder sibling(TypeChunk sibling) {
			setRelation(ChunkRelation.SIBLING, sibling);
			
			return this;
		}
		
		public TypeBuilder readContentLengthAndType(InputStream stream) throws IOException, InvalidContentTypeIdentfierException, InvalidChunkIdentifierException {
			checkContentNotSet();
			readContentLength(stream);
			contentType = InterchangeHelper.createContentTypeIdentifier(stream);
			
			return this;
		}
		
		public TypeBuilder setContentType(String contentType) throws InvalidContentTypeIdentfierException {
			checkContentNotSet();
			this.contentType = InterchangeHelper.verifiedContentTypeIdentifier(contentType);
			
			return this;
		}
		
		public String getContentType() {
			return contentType;
		}
		
		TypeBuilder contentLength(long length) {
			super.setContentLength(length);
			
			return this;
		}
		
		public TypeBuilder readContentLength(InputStream stream) throws IOException {
			readContentLengthInternal(stream);
			
			return this;
		}

		private void checkContentNotSet() {
			if (contentType != null) {
				throw new IllegalStateException(getDefinition() + ": content-type already set: " + contentType);
			}
		}
	}
	
	public static final class ContentBuilder extends AbstractBuilder<ContentChunk, ContentDefinition> {

		private static final byte[] EMPTY_CONTENT = new byte[0];
		private byte[] content;

		ContentBuilder(ContentDefinition definition, boolean readOnly) {
			super(definition, readOnly);
		}

		@Override
		public ContentChunk build() {
			if (getDefinition().preReadContent()) {
				allocateContent();
			}
			return new ContentChunk(getDefinition(), getContentLength(), getRelation(), getInstance(), content, getReadOnly());
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
			checkNoContent();
			allocateContent();
		}

		private void allocateContent() {
			if (content != null) {
				return;
			}
			if (getContentLength() > MAX_PREREAD_LENGTH) {
				throw new IllegalStateException(getDefinition() + ": cannot read " + getContentLength() + " bytes of content: maximum is " + MAX_PREREAD_LENGTH + " bytes");
			}
			content = new byte[(int)getContentLength()];
		}

		public ContentBuilder setContent(byte[] chunkData, boolean clone) {
			contentLength(chunkData != null ? chunkData.length : 0);
			content = chunkData != null ? clone ? chunkData.clone() : chunkData : EMPTY_CONTENT;
			
			return this;
		}
		
		public ContentBuilder contentLength(long length) {
			super.setContentLength(length);
			
			return this;
		}
		
		public ContentBuilder readContentLength(InputStream stream) throws IOException {
			readContentLengthInternal(stream);
			
			return this;
		}
		
		private void checkNoContent() {
			if (content != null) {
				throw new IllegalStateException(getDefinition() + ": content already ead");
			}
		}
	}
}
