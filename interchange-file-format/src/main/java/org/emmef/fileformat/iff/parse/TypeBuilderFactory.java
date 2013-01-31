package org.emmef.fileformat.iff.parse;

import org.emmef.fileformat.iff.InterchangeChunk.TypeBuilder;

public interface TypeBuilderFactory {
	TypeBuilder createBuilder(boolean readOnly);

	ContentBuilderFactory getContentParser(String contentType) throws UnrecognizedContentTypeException;
}
