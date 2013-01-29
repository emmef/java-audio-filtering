package org.emmef.fileformat.iff.parse;

import org.emmef.fileformat.iff.InterchangeChunk.ContentBuilder;

public interface ContentBuilderFactory {

	ContentBuilder create(String createIdentifier) throws UnrecognizedContentChunkException;

}
