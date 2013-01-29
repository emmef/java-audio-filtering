package org.emmef.fileformat.riff;

import org.emmef.fileformat.iff.parse.TypeBuilderFactory;
import org.emmef.fileformat.iff.parse.TypeResolver;

public enum RiffTypeFactory implements TypeResolver {
	INSTACE;

	@Override
	public TypeBuilderFactory get(String identifier) {
		if (!"RIFF".equals(identifier)) {
			return null;
		}
		
		return RiffBuilderFactory.INSTANCE;
	}
}
