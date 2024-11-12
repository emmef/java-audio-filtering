package org.emmef.fileformat.iff;

import org.emmef.samples.serialization.Endian;

public abstract class InterchangeDefinition implements DefinitionInfo {
	
	private final String identifier;
	
	public InterchangeDefinition(String identifier) throws InvalidChunkIdentifierException {
		this.identifier = InterchangeHelper.verifiedChunkIdentifier(identifier);
	}
	
	/**
	 * Returns the four ASCII-character chunk identifier as a String
	 * 
	 * @return a non-{@code null} {@link String}
	 */
	@Override
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
	@Override
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
	@Override
	public Endian getEndian() {
		return null;
	}
	
	protected final boolean isValidContentLength(long length) {
		return length >= childRelativeOffset() && length < MAX_CONTENT_LENGTH;
	}
	
	protected long validContentLength(long length) {
		if (isValidContentLength(length)) {
			return length;
		}
		throw new IllegalArgumentException(this + ": content length must be between [" + childRelativeOffset() + ".." + MAX_CONTENT_LENGTH + "]");
	}
}
