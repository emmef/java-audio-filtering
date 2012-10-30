package org.emmef.fileformat.riff.wave;

import org.emmef.fileformat.riff.RiffChunk;
import org.emmef.fileformat.riff.RiffDataChunk;

public class AudioFormatChunk extends RiffDataChunk {
	public static final int OFFSET_FORMAT_TAG = 0;
	public static final int OFFSET_CHANNEL_COUNT = 2;
	public static final int OFFSET_SAMPLE_RATE = 4;
	public static final int OFFSET_BYTES_PER_SECOND = 8;
	public static final int OFFSET_BYTES_PER_FRAME = 12;
	public static final int OFFSET_BITS_PER_SAMPLE = 14;
	public static final int OFFSET_EXTENDED_FORMAT_LENGTH = 16;
	public static final int OFFSET_EXTENDED_FORMAT_VALID_BITS = 18;
	public static final int OFFSET_EXTENDED_FORMAT_CHANNEL_MASK = 20;
	public static final int OFFSET_EXTENDED_FORMAT_SUB_FORMAT = 24;
	public static final int SIZE_DEFAULT_CHUNK = OFFSET_EXTENDED_FORMAT_LENGTH;
	
	public static final String WAVE_AUDIO_FORMAT_IDENTIFIER = "fmt ";
	public static final int L = WAVE_AUDIO_FORMAT_IDENTIFIER.length();

	AudioFormatChunk(RiffChunk source, byte[] buffer) {
		super(source, buffer);
		if (!WAVE_AUDIO_FORMAT_IDENTIFIER.equals(source.getIdentifier())) {
			throw new IllegalArgumentException("Chunk name must be \"" + WAVE_AUDIO_FORMAT_IDENTIFIER + "\", not \"" + source.getIdentifier() + "\"");
		}
	}
	
	public FormatType getStorageType() {
		return FormatType.valueOf(getWordAt(OFFSET_FORMAT_TAG));
	}
	
	/**
	 * Return the number of channels in each frame.
	 * @return a positive integer
	 */
	public int getChannels() {
		return getWordAt(OFFSET_CHANNEL_COUNT);
	}

	/**
	 * Return the number of frames per second.
	 * @return a positive integer
	 */
	public long getSampleRate() {
		return getDWordAt(OFFSET_SAMPLE_RATE);
	}

	/**
	 * Return the (average) number of bytes of audio data per second.
	 * <p>
	 * This is the product of the number of bytes per sample, the number of
	 * channels and the sample rate.
	 * 
	 * @return a positive integer
	 */
	public long getByteRate() {
		return getDWordAt(OFFSET_BYTES_PER_SECOND);
	}
	
	/**
	 * Returns the number of bytes per frame.
	 * <p>
	 * This is the product of the number of bytes per sample and the number of
	 * channels in the audio data.
	 * 
	 * @return a positive integer
	 */
	public int getBytesPerFrame() {
		return getWordAt(OFFSET_BYTES_PER_FRAME);
	}
	
	public int getContainerBytesPerSample() {
		int channels = getChannels();
		if (channels <= 0) {
			throw new IllegalStateException("Illegal number of channels: " + channels);
		}
		int bytesPerFrame = getBytesPerFrame();
		if (bytesPerFrame <= 0) {
			throw new IllegalStateException("Illegal number of channels: " + channels);
		}
		return bytesPerFrame; 
	}

	/**
	 * Get the number of representative bits per sample.
	 * <p>
	 * This round up to eight times the number of bytes per sample in the container. A single byte sample
	 * can have any value 1..8. 
	 * @return a positive integer
	 */
	public int getBitsPerSample() {
		return getWordAt(OFFSET_BITS_PER_SAMPLE);
	}
	
	public boolean hasExtendedFormatSize() {
		return getExtendedFormatSize() >= 0;
	}
	
	public boolean hasExtendedFormat() {
		return getExtendedFormatSize() > 0;
	}
	
	public int getValidBitsPerSample() {
		if (getExtendedFormatSize() > 0) {
			return getWordAt(OFFSET_EXTENDED_FORMAT_VALID_BITS);
		}
		return getBitsPerSample();
	}
	
	public int getExtendedFormatSize() {
		return getBuffer().length >= 18 ? getWordAt(OFFSET_EXTENDED_FORMAT_LENGTH) : -1;
	}
	
	public int getExtendedValidBitsPerSample() {
		checkExtendedFormatAvailable();
		return getWordAt(OFFSET_EXTENDED_FORMAT_VALID_BITS);
	}
	
	public long getExtendedChannelMask() {
		checkExtendedFormatAvailable();
		return getWordAt(OFFSET_EXTENDED_FORMAT_CHANNEL_MASK);
	}
	
	public int getExtendedSubFormat() {
		checkExtendedFormatAvailable();
		return getWordAt(OFFSET_EXTENDED_FORMAT_SUB_FORMAT);
	}

	private void checkExtendedFormatAvailable() {
		if (getExtendedFormatSize() <=  0) {
			throw new IllegalStateException("Have no extended information");
		}
	}
}
