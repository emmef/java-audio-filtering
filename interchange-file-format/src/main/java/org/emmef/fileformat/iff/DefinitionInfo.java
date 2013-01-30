package org.emmef.fileformat.iff;

import org.emmef.samples.serialization.Endian;

public interface DefinitionInfo {

	public abstract Endian getEndian();

	public abstract long childRelativeOffset();

	public abstract String getIdentifier();

}
