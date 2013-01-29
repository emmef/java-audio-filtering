package org.emmef.fileformat.iff.parse;


public interface TypeResolver {
	TypeBuilderFactory get(String identifier);
}
