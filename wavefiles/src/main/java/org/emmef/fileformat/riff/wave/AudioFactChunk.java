package org.emmef.fileformat.riff.wave;

import org.emmef.fileformat.interchange.ContentChunk;


public class AudioFactChunk extends InterpretedContentChunk {
	public static final String CHUNK_ID = "fact";
	public static final int OFFSET_SAMPLES_PER_CHANNEL = 0;
	public static final int SIZE_DEFAULT_CHUNK = 4;
	
	public AudioFactChunk(ContentChunk chunk) {
		super(chunk);
	}
	
	
	public boolean containsInformation() {
		return getContentLength() >= SIZE_DEFAULT_CHUNK;
	}
	
	public long getSamplePerChannel() {
		if (containsInformation()) {
			return getDWordAt(OFFSET_SAMPLES_PER_CHANNEL);
		}
		throw new IllegalStateException("This \"" + getChunk().getContentDefinition().getIdentifier() + "\" contains no data");
	}
}
