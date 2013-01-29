package org.emmef.fileformat.riff;

import org.emmef.fileformat.iff.InvalidChunkIdentifierException;
import org.emmef.fileformat.iff.TypeDefinition;
import org.emmef.samples.serialization.Endian;

public final class RiffDefinition extends TypeDefinition {
	public static final String RIFF_IDENTIFIER = "RIFF";
	
	public static final RiffDefinition INSTANCE;
	
	private RiffDefinition() throws InvalidChunkIdentifierException {
		super(RIFF_IDENTIFIER, Endian.LITTLE);
	}
	
	static {
		try {
			INSTANCE = new RiffDefinition();
		}
		catch (InvalidChunkIdentifierException e) {
			throw new IllegalStateException(e);
		}
	}
}
