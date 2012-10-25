package org.emmef.fileformat.riff;

public final class RiffRootRecord implements RiffRecord {
	public static final int RIFF_ROOT_HEADER_LENGTH = 12;
	private final String identifier;
	private final long length;
	
	/**
	 * Redmond Interchange File Format identifier
	 */
	public static final String RIFF_IDENTIFIER = "RIFF";

	RiffRootRecord(String identifier, long length) {
		if (length < RIFF_ROOT_HEADER_LENGTH) {
			throw new IllegalArgumentException("Length of riff file must be at least " + RIFF_ROOT_HEADER_LENGTH + " bytes");
		}
		this.identifier = identifier;
		this.length = length;
	}
	
	public static RiffRootRecord create(String identifier, long length) {
		return new RiffRootRecord(RiffUtils.checkValidRiffChunkIdentifier(identifier), length);
	}
	
	@Override
	public RiffRecord getParent() {
		return null;
	}

	@Override
	public int compareTo(RiffRecord o) {
		long absoluteOffset = o.getAbsoluteOffset();
		
		return absoluteOffset > 0 ? -1 : 0; 
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public int getHeaderLength() {
		return RIFF_ROOT_HEADER_LENGTH;
	}
	
	@Override
	public long getSkipToNext() {
		return length - RIFF_ROOT_HEADER_LENGTH;
	}

	@Override
	public long getContentLength() {
		return length;
	}

	@Override
	public long getRelativeOffset() {
		return 0;
	}

	@Override
	public long getAbsoluteOffset() {
		return 0;
	}
}
