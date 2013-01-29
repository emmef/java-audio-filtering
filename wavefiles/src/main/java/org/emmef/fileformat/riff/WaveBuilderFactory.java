package org.emmef.fileformat.riff;

import org.emmef.fileformat.interchange.ChunkNotRecognisedException;
import org.emmef.fileformat.interchange.ContentBuilderFactory;
import org.emmef.fileformat.interchange.ContentDefinition;
import org.emmef.fileformat.interchange.InterchangeChunk;
import org.emmef.fileformat.interchange.InterchangeChunk.ContentBuilder;

enum WaveBuilderFactory implements ContentBuilderFactory {
	INSTANCE;
	
	static final ContentDefinition FMT_DEFINITION = new ContentDefinition("fmt\u0000", 0, null, true);
	static final ContentDefinition FACT_DEFINITION = new ContentDefinition("fact", 0, null, true);
	static final ContentDefinition DATA_DEFINITION = new ContentDefinition("data", 0, null, false);
	
	@Override
	public ContentBuilder create(String id)
			throws ChunkNotRecognisedException {
		if (FMT_DEFINITION.getIdentifier().equals(id)) {
			return InterchangeChunk.contentBuilder(FMT_DEFINITION);
		}
		if (DATA_DEFINITION.getIdentifier().equals(id)) {
			return InterchangeChunk.contentBuilder(DATA_DEFINITION);
		}
		if (FACT_DEFINITION.getIdentifier().equals(id)) {
			return InterchangeChunk.contentBuilder(FACT_DEFINITION);
		}
		return InterchangeChunk.contentBuilder(new ContentDefinition(id, 0, null, true));
	}
}