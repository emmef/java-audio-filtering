package org.emmef.fileformat.riff;

import org.emmef.fileformat.interchange.InterchangeHelper;
import org.emmef.fileformat.interchange.TypeDefinition;
import org.emmef.samples.serialization.Endian;

public final class RiffDefinition extends TypeDefinition {
	public static final String RIFF_IDENTIFIER = InterchangeHelper.verifiedChunkIdentifier("RIFF");

	public RiffDefinition() {
		super(RIFF_IDENTIFIER, Endian.LITTLE);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The RIFF format used little-endian representaton of multi-bye values.
	 */
	@Override
	public Endian getEndian() {
		return Endian.LITTLE;
	}
}
