package org.emmef.fileformat.iff;

import org.emmef.serialization.Endian;

public abstract class InterchangeDefinition {
	/**
	 * This is the offset of content inside a chunk, which is always the
	 * identifier (4 ASCII-bytes) plus the chunk-length (4 byte word).
	 */
	public static final long CONTENT_RELATIVE_OFFSET = 8;
	
	private final String identifier;
	
	public InterchangeDefinition(String identifier) throws InvalidChunkIdentifierException {
		this.identifier = InterchangeHelper.verifiedChunkIdentifier(identifier);
	}
	
	/**
	 * Returns the four ASCII-character chunk identifier as a String
	 * 
	 * @return a non-{@code null} {@link String}
	 */
	public final String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Returns the relative offset to the content offset of child chunks
	 * <p>
	 * Child chunks so not always start at the beginning of the chunk content.
	 * For instance, a RIFF chunk contains a content-identifier before the first
	 * child chunk starts. In case of a WAVE audio file this is {@code "WAVE"}.

	 * @return a positive number
	 */
	public long childRelativeOffset() {
		return 0L;
	}
	
	/**
	 * Returns the endian-ness of the content.
	 * <p>
	 * The value {@code null} indicates that the parent or sibling chunk endian
	 * type should be inherited.
	 * 
	 * @return a non-{@code null} {@link Endian}.
	 */
	public Endian getEndian() {
		return null;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
