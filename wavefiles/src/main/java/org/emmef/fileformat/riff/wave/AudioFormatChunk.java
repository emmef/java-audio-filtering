package org.emmef.fileformat.riff.wave;

import org.emmef.fileformat.riff.RiffChunk;
import org.emmef.fileformat.riff.RiffDataChunk;

public class AudioFormatChunk extends RiffDataChunk {
	public static final String WAVE_AUDIO_FORMAT_IDENTIFIER = "fmt ";
	public static final int L = WAVE_AUDIO_FORMAT_IDENTIFIER.length();

	AudioFormatChunk(RiffChunk source, byte[] buffer) {
		super(source, buffer);
		if (!WAVE_AUDIO_FORMAT_IDENTIFIER.equals(source.getIdentifier())) {
			throw new IllegalArgumentException("Chunk name must be \"" + WAVE_AUDIO_FORMAT_IDENTIFIER + "\", not \"" + source.getIdentifier() + "\"");
		}
	}
	
	public StorageType getStorageType() {
		return StorageType.valueOf(getWordAt(0));
	}
	
	public int getChannels() {
		return getWordAt(2);
	}
	
	public long getSampleRate() {
		return getDWordAt(4);
	}
	
	public long getByteRate() {
		return getDWordAt(8);
	}
	
	public int getBlockAlignment() {
		return getWordAt(12);
	}
	
	public int getBitsPerSample() {
		return getWordAt(14);
	}
	
	public boolean hasExtraFormatInformation() {
		return getBuffer().length > 16;
	}
}
