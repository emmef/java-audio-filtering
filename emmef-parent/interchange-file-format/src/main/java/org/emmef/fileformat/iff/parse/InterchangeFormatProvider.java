package org.emmef.fileformat.iff.parse;

public interface InterchangeFormatProvider {
	String getTypeIdentifier();
	TypeBuilderFactory get();
	int getPriority();
}
