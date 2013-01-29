package org.emmef.fileformat.interchange;

import org.emmef.samples.serialization.Endian;

public final class ContentDefinition extends InterchangeDefinition {
	private final long childRelativeOffset;
	private final Endian forcedEndian;
	private final boolean preReadContent;

	public ContentDefinition(String identifier, long childRelativeOffset, Endian forcedEndian, boolean preReadContent) {
		super(identifier);
		this.childRelativeOffset = childRelativeOffset;
		this.forcedEndian = forcedEndian;
		this.preReadContent = preReadContent;
	}
	
	@Override
	public final long childRelativeOffset() {
		return childRelativeOffset;
	}
	
	public boolean preReadContent() {
		return preReadContent;
	}
	
	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder(40);
		
		toString.append("Chunk(id=").append(getIdentifier()).append("; child-offset=/").append(childRelativeOffset);
		if (forcedEndian != null) {
			toString.append("; forced-endian=").append(forcedEndian);
		}
		toString.append(')');
		
		return toString.toString();
	}
	
}
