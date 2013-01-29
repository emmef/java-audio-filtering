package org.emmef.fileformat.interchange;

public interface TypeResolver {
	TypeBuilderFactory get(String identifier);
}
