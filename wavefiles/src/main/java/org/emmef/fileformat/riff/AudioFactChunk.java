package org.emmef.fileformat.riff;

public class AudioFactChunk extends RiffDataChunk {
	public static final String CHUNK_ID = "fact";
	public static final int OFFSET_SAMPLES_PER_CHANNEL = 0;
	public static final int SIZE_DEFAULT_CHUNK = 4;
	
	public AudioFactChunk(RiffChunk source, byte[] data) {
		super(source, data);
	}
	
	public boolean containsInformation() {
		return getBuffer().length >= SIZE_DEFAULT_CHUNK;
	}
	
	public long getSamplePerChannel() {
		if (containsInformation()) {
			return getDWordAt(OFFSET_SAMPLES_PER_CHANNEL);
		}
		throw new IllegalStateException("This \"" + getIdentifier() + "\" contains no data");
	}
}
