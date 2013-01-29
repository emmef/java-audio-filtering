package org.emmef.fileformat.iff.parse;

import org.emmef.fileformat.iff.InterchangeChunk.ContentBuilder;
import org.emmef.fileformat.iff.InvalidChunkIdentifierException;

public interface ContentBuilderFactory {

	ContentBuilder create(String createIdentifier) throws UnrecognizedContentChunkException, InvalidChunkIdentifierException;

}
