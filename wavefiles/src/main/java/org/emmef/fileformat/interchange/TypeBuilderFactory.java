package org.emmef.fileformat.interchange;

import org.emmef.fileformat.interchange.InterchangeChunk.TypeBuilder;

public interface TypeBuilderFactory {
	TypeBuilder createBuilder();

	ContentBuilderFactory getContentParser(String contentType) throws ContentTypeNotRecognisedException;
}
