package org.emmef.fileformat.riff;

import org.emmef.fileformat.interchange.ContentBuilderFactory;
import org.emmef.fileformat.interchange.ContentTypeNotRecognisedException;
import org.emmef.fileformat.interchange.InterchangeChunk;
import org.emmef.fileformat.interchange.InterchangeChunk.TypeBuilder;
import org.emmef.fileformat.interchange.TypeBuilderFactory;

enum RiffBuilderFactory implements TypeBuilderFactory {
	INSTANCE;
	
	@Override
	public ContentBuilderFactory getContentParser(String contentType) throws ContentTypeNotRecognisedException {
		// TODO provide available return values with a provider structure.
		// Make providers that have a type (RIFF, AIFF, etc) and a Content-type
		// (WAVE, AVI) that can be used to create proper mappings to factories.
		if (!"WAVE".equals(contentType)) {
			throw new ContentTypeNotRecognisedException(contentType);
		}
		return WaveBuilderFactory.INSTANCE;
	}

	@Override
	public TypeBuilder createBuilder() {
		return InterchangeChunk.typeBuilder(RiffTypeFactory.RIFF_DEFINITION);
	}
}