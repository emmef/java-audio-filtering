package org.emmef.fileformat.iff;

import org.emmef.serialization.Endian;
import org.emmef.utils.Preconditions;

/**
 * Defines the type of interchange format.
 * <p>
 * Examples of Interchange File Formats are RIFF (Redmond Interchange File
 * Format) and AIFF (Apple Interchange File Format) and
 * <p>
 * Type chunks always have an identifier of the actual content type,
 * so child chunks always start after that. The length of an identifier is
 * 4, so {@link #childRelativeOffset()} always returns 4.
 */
public class TypeDefinition extends InterchangeDefinition {
	private final Endian endian;

	public TypeDefinition(String identifier, Endian endian) throws InvalidChunkIdentifierException {
		super(identifier);
		Preconditions.checkNotNull(endian, "Endianness");
		this.endian = endian;
	}
	
	/**
	 * {@inheritDoc}
	 * Identifying chunks always contains a content identifier
	 * at the start, before any child chunks.
	 */
	@Override
	public long childRelativeOffset() {
		return 4;
	}
	
	@Override
	public Endian getEndian() {
		return endian;
	}

	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder(40);
		
		toString.append(getIdentifier()).append("(");
		
		if (endian != null) {
			toString.append("endian=").append(endian);
		}
		toString.append(')');
		
		return toString.toString();
	}
}
