package org.emmef.fileformat.riff;

import org.emmef.fileformat.interchange.TypeBuilderFactory;
import org.emmef.fileformat.interchange.TypeResolver;

public enum RiffTypeFactory implements TypeResolver {
	INSTACE;

	static final RiffDefinition RIFF_DEFINITION = new RiffDefinition();

	@Override
	public TypeBuilderFactory get(String identifier) {
		if (!"RIFF".equals("identifier")) {
			return null;
		}
		
		return RiffBuilderFactory.INSTANCE;
	}
}
