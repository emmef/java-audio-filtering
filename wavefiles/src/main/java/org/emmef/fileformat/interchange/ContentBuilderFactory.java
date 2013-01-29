package org.emmef.fileformat.interchange;

import org.emmef.fileformat.interchange.InterchangeChunk.ContentBuilder;

public interface ContentBuilderFactory {

	ContentBuilder create(String createIdentifier) throws ChunkNotRecognisedException;

}
