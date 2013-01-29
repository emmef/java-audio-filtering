package org.emmef.fileformat.riff;

import org.emmef.fileformat.iff.ContentDefinition;
import org.emmef.fileformat.iff.InterchangeChunk;
import org.emmef.fileformat.iff.InterchangeChunk.ContentBuilder;
import org.emmef.fileformat.iff.InvalidChunkIdentifierException;
import org.emmef.fileformat.iff.parse.ContentBuilderFactory;

public enum WaveBuilderFactory implements ContentBuilderFactory {
	INSTANCE;
	
	public static final ContentDefinition FMT_DEFINITION;
	public static final ContentDefinition FACT_DEFINITION;
	public static final ContentDefinition DATA_DEFINITION;
	
	@Override
	public ContentBuilder create(String id) throws InvalidChunkIdentifierException {
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
	
	static {
		try {
			FMT_DEFINITION = new ContentDefinition("fmt ", 0, null, true);
			FACT_DEFINITION = new ContentDefinition("fact", 0, null, true);
			DATA_DEFINITION = new ContentDefinition("data", 0, null, false);
		}
		catch (InvalidChunkIdentifierException e) {
			throw new IllegalStateException(e);
		}
	}
}