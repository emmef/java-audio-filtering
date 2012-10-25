package org.emmef.fileformat.riff;

public class RiffChunk implements RiffRecord {
	private final RiffRecord parent;
	private final String identifier;
	private final long length;
	private final long relativeOffset;
	
	public RiffChunk(RiffRecord parent, RiffChunk source) {
		this.parent = RiffUtils.checkNotNull(parent, "parent");
		RiffUtils.checkNotNull(source, "source");
		this.identifier = source.identifier;
		this.length = source.length;
		this.relativeOffset = source.relativeOffset;
	}
	
	public RiffChunk(RiffChunk source) {
		RiffUtils.checkNotNull(source, "source");
		this.parent = source.parent;
		this.identifier = source.identifier;
		this.length = source.length;
		this.relativeOffset = source.relativeOffset;
	}
	
	public RiffChunk(RiffRecord parent, String chunkIdentifier, long relativeOffset, long length) {
		this.parent = RiffUtils.checkNotNull(parent, "parent");
		if (length < 0) {
			throw new IllegalArgumentException("length should be greater than or equal to 0");
		}
		if (relativeOffset < 0) {
			throw new IllegalArgumentException("relative offset should be greater than or equal to 0");
		}
		this.identifier = RiffUtils.checkValidRiffChunkIdentifier(chunkIdentifier);
		this.length = length;
		this.relativeOffset = relativeOffset;
	}

	@Override 
	public final String getIdentifier() {
		return identifier;
	}
	
	@Override
	public final int getHeaderLength() {
		return 8;
	}
	
	@Override
	public long getSkipToNext() {
		return length;
	}
	
	@Override
	public final long getContentLength() {
		return length;
	}

	@Override
	public final long getRelativeOffset() {
		return parent.getHeaderLength() + relativeOffset;
	}

	@Override
	public final long getAbsoluteOffset() {
		return parent.getAbsoluteOffset() + getRelativeOffset(); 
	}
	
	@Override
	public int compareTo(RiffRecord o) {
		long mine = getAbsoluteOffset();
		long yours = o.getAbsoluteOffset();
		if (mine > yours) {
			return 1;
		}
		if (mine < yours) {
			return -1;
		}
		
		return 0;
	}

	@Override
	public RiffRecord getParent() {
		return parent;
	}
}
