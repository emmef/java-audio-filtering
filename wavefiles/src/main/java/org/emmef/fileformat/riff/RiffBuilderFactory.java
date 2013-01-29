package org.emmef.fileformat.riff;

import org.emmef.fileformat.iff.InterchangeChunk;
import org.emmef.fileformat.iff.InterchangeChunk.TypeBuilder;
import org.emmef.fileformat.iff.parse.ContentBuilderFactory;
import org.emmef.fileformat.iff.parse.TypeBuilderFactory;
import org.emmef.fileformat.iff.parse.UnrecognizedContentTypeException;

enum RiffBuilderFactory implements TypeBuilderFactory {
	INSTANCE;
	
	@Override
	public ContentBuilderFactory getContentParser(String contentType) throws UnrecognizedContentTypeException {
		// TODO provide available return values with a provider structure.
		// Make providers that have a type (RIFF, AIFF, etc) and a Content-type
		// (WAVE, AVI) that can be used to create proper mappings to factories.
		if (!"WAVE".equals(contentType)) {
			throw new UnrecognizedContentTypeException(contentType);
		}
		return WaveBuilderFactory.INSTANCE;
	}

	@Override
	public TypeBuilder createBuilder() {
		return InterchangeChunk.typeBuilder(RiffDefinition.INSTANCE);
	}
}