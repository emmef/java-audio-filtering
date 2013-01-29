package org.emmef.fileformat.riff;

import org.emmef.fileformat.interchange.ContentBuilderFactory;
import org.emmef.fileformat.interchange.ContentTypeNotRecognisedException;
import org.emmef.fileformat.interchange.InterchangeChunk;
import org.emmef.fileformat.interchange.TypeBuilderFactory;
import org.emmef.fileformat.interchange.InterchangeChunk.TypeBuilder;

enum RiffBuilderFactory implements TypeBuilderFactory {
	INSTANCE;
	
	@Override
	public ContentBuilderFactory getContentParser(String contentType) throws ContentTypeNotRecognisedException {
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