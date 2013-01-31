package org.emmef.fileformat.riff.wave;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.emmef.audio.format.AudioFormat;
import org.emmef.fileformat.iff.ContentChunk;
import org.emmef.fileformat.iff.DelegateContentChunk;
import org.emmef.fileformat.iff.InterchangeChunk;
import org.emmef.fileformat.iff.InterchangeChunk.ContentBuilder;
import org.emmef.fileformat.iff.TypeChunk;
import org.emmef.fileformat.riff.WaveBuilderFactory;
import org.emmef.samples.serialization.Serialize;

import com.google.common.base.Preconditions;

public class AudioFormatChunk extends DelegateContentChunk {
	public static final List<Byte> TYPE_GUID_PADDING = Collections.unmodifiableList(Arrays.asList(
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x80,
			(byte)0x00, (byte)0x00, (byte)0xAA, (byte)0x00, (byte)0x38, (byte)0x9B, (byte)0x71));
	
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
	public static final int OFFSET_EXTENDED_FORMAT_GUID_PAD = 26;
	
	public static final int SIZE_MAXIMUM = OFFSET_EXTENDED_FORMAT_GUID_PAD + TYPE_GUID_PADDING.size();
	public static final int SIZE_EXTENDED_NOT_PRESENT = OFFSET_EXTENDED_FORMAT_LENGTH;
	public static final int SIZE_EXTENDED_EMPTY = OFFSET_EXTENDED_FORMAT_VALID_BITS;
	public static final int SIZE_OF_EXTENDED_CHUNK = SIZE_MAXIMUM - SIZE_EXTENDED_EMPTY;
	
	public static final String WAVE_AUDIO_FORMAT_IDENTIFIER = "fmt ";
	public static final int L = WAVE_AUDIO_FORMAT_IDENTIFIER.length();

	AudioFormatChunk(ContentChunk chunk) {
		super(chunk);
	}
	
	/**
	 * Create a new format chunk, based on the provided
	 * audio format.
	 * 
	 * @param relation
	 * @param format
	 */
	public static AudioFormatChunk fromFormat(InterchangeChunk relation, AudioFormat format) {
		Preconditions.checkNotNull(relation, "Parent or sibling chunk");
		byte[] chunkData = createChunkData(Preconditions.checkNotNull(format, "format"));
		ContentBuilder builder = InterchangeChunk.contentBuilder(WaveBuilderFactory.FMT_DEFINITION, false);
		
		builder.setContent(chunkData, false);
		if (relation instanceof TypeChunk) {
			builder.parent(relation);
		}
		else {
			builder.sibling((ContentChunk)relation);
		}
		
		return new AudioFormatChunk(builder.build());
	}

	public FormatType getFormatType() {
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
		return bytesPerFrame / channels;
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
		return getContentLength() >= 18 ? getWordAt(OFFSET_EXTENDED_FORMAT_LENGTH) : -1;
	}
	
	public int getExtendedValidBitsPerSample() {
		checkExtendedFormatAvailable();
		return getWordAt(OFFSET_EXTENDED_FORMAT_VALID_BITS);
	}
	
	public boolean hasExtendedChannelMask() {
		return getContentLength() >= OFFSET_EXTENDED_FORMAT_SUB_FORMAT;
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
	
	private static byte[] createChunkData(AudioFormat format) {
		byte[] buffer = new byte[SIZE_MAXIMUM];
		
		int bitsPerSample = writeBasicFormatAndGetValidBits(format, buffer);
		long locationMask = format.getLocationMask();
		
		if (!format.hasSpareBits() && locationMask == 0) {
			Serialize.write16LittleEndian(getFormatNumber(format), buffer, OFFSET_FORMAT_TAG);
			Serialize.write16LittleEndian(0, buffer, OFFSET_EXTENDED_FORMAT_LENGTH);
			byte[] result = new byte[SIZE_EXTENDED_EMPTY];
			System.arraycopy(buffer, 0, result, 0, SIZE_EXTENDED_EMPTY);
			return result;
		}
		
		Serialize.write16LittleEndian(FormatType.WAVE_FORMAT_EXTENSIBLE.getFormatValue(), buffer, OFFSET_FORMAT_TAG);
		
		Serialize.write16LittleEndian(SIZE_OF_EXTENDED_CHUNK, buffer, OFFSET_EXTENDED_FORMAT_LENGTH);
		Serialize.write16LittleEndian(bitsPerSample, buffer, OFFSET_EXTENDED_FORMAT_VALID_BITS);
		Serialize.write32LittleEndian((int)locationMask, buffer, OFFSET_EXTENDED_FORMAT_CHANNEL_MASK);
		
		Serialize.write16LittleEndian(getFormatNumber(format), buffer, OFFSET_EXTENDED_FORMAT_SUB_FORMAT);
		for (int i = 0; i < TYPE_GUID_PADDING.size(); i++) {
			buffer[OFFSET_EXTENDED_FORMAT_GUID_PAD + i] = TYPE_GUID_PADDING.get(i) ;
		}
		
		return buffer;
	}

	private static int writeBasicFormatAndGetValidBits(AudioFormat format, byte[] result) {
		int channels = format.getChannels();
		Serialize.write16LittleEndian(channels, result, OFFSET_CHANNEL_COUNT);
		
		long sampleRate = format.getSampleRate();
		Serialize.write32LittleEndian((int)sampleRate, result, OFFSET_SAMPLE_RATE);
		
		int bytesPerSample = format.getBytesPerSample();
		int bytesPerFrame = channels * bytesPerSample;
		long bytesPerSecond = sampleRate * bytesPerFrame;
		Serialize.write32LittleEndian((int)bytesPerSecond, result, OFFSET_BYTES_PER_SECOND);
		Serialize.write16LittleEndian(bytesPerFrame, result, OFFSET_BYTES_PER_FRAME);
		
		int bitsPerSample = format.getValidBitsPerSample();
		Serialize.write16LittleEndian(bitsPerSample, result, OFFSET_BITS_PER_SAMPLE);
		return bitsPerSample;
	}

	private static int getFormatNumber(AudioFormat format) {
		int formatNumber;
		switch (format.getSampleFormat()) {
		case FLOAT:
			formatNumber = FormatType.WAVE_FORMAT_IEEE_FLOAT.getFormatValue();
			break;
		case PCM:
			formatNumber = FormatType.WAVE_FORMAT_PCM.getFormatValue();
			break;
		default:
			throw new IllegalStateException("Unsupported sample format " + format);
		}
		return formatNumber;
	}

	static {
		if (SIZE_MAXIMUM != 40) {
			throw new Error("Program error; MAXIMUM size of chunk must be 40");
		}
		if (SIZE_OF_EXTENDED_CHUNK != 22) {
			throw new Error("Program error; EXTENDED size of chunk must be 22 if present");
		}
	}
}
