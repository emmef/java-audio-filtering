package org.emmef.fileformat.riff.wave;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.format.AudioFormatSampleRateSetter;
import org.emmef.audio.format.AudioFormats;
import org.emmef.audio.format.SpeakerLocations;

import com.google.common.base.Preconditions;

public class AudioFormatChunks {
	
	public static final AudioFormat fromChunks(AudioFormatChunk formatChunk) {
		Preconditions.checkNotNull(formatChunk, "Chunk cannot be null");
		switch (formatChunk.getFormatType()) {
		case WAVE_FORMAT_EXTENSIBLE:
			return fromChunkExtensible(formatChunk);
		case WAVE_FORMAT_IEEE_FLOAT:
			return fromChunkFloatingPoint(formatChunk);
		case WAVE_FORMAT_PCM:
			return fromChunkPcm(formatChunk);
		default:
			throw new IllegalStateException("Unsupported chunk format " + formatChunk.getFormatType());
		}
	}
	
	private static AudioFormat fromChunkPcm(AudioFormatChunk chunk) {
		int containerBytesPerSample = chunk.getContainerBytesPerSample();
		int specifiedBitsPerSample = chunk.getBitsPerSample();
		
		int roundedUpBytesPerSample = specifiedBitsPerSample + 7 >> 3;
		
		if (roundedUpBytesPerSample != containerBytesPerSample) {
			AudioFormat format = getKnownMismatchExceptionFormat(chunk, containerBytesPerSample, specifiedBitsPerSample);
			
			if (format != null) {
				return format;
			}
			throw new IllegalArgumentException("Container bytes per sample (" + containerBytesPerSample + ") must match rounded-up value for " + specifiedBitsPerSample + " bits: " + roundedUpBytesPerSample);
		}
		
		AudioFormatSampleRateSetter builder = createPcmBuilder(chunk);
		
		return builder.rate(chunk.getSampleRate()).bitDepth(specifiedBitsPerSample);
	}
	

	private static AudioFormat fromChunkFloatingPoint(AudioFormatChunk chunk) {
		int containerBytesPerSample = chunk.getContainerBytesPerSample();
		int specifiedBitsPerSample = chunk.getBitsPerSample();
		if (containerBytesPerSample != 4 && containerBytesPerSample != 8) {
			throw new IllegalArgumentException("Invalid bytes per fample for float data: " + containerBytesPerSample);
		}
		
		int roundedUpBytesPerSample = specifiedBitsPerSample + 7 >> 3;
		
		if (roundedUpBytesPerSample != containerBytesPerSample) {
			AudioFormat format = getKnownMismatchExceptionFormat(chunk, containerBytesPerSample, specifiedBitsPerSample);
			
			if (format != null) {
				return format;
			}
			throw new IllegalArgumentException("Container bytes per sample (" + containerBytesPerSample + ") must match rounded-up value for " + specifiedBitsPerSample + " bits: " + roundedUpBytesPerSample);
		}
		
		AudioFormatSampleRateSetter builder = createFloatBuilder(chunk);
		
		return builder.rate(chunk.getSampleRate()).bitDepth(specifiedBitsPerSample);
	}

	private static AudioFormat fromChunkExtensible(AudioFormatChunk chunk) {
		int extendedSubFormat = chunk.getExtendedSubFormat();
		
		AudioFormatSampleRateSetter builder;
		if (extendedSubFormat == FormatType.WAVE_FORMAT_IEEE_FLOAT.getFormatValue()) {
			builder = createFloatBuilder(chunk);
		}
		else if (extendedSubFormat == FormatType.WAVE_FORMAT_PCM.getFormatValue()) {
			builder = createPcmBuilder(chunk);
		}
		else {
			throw new IllegalArgumentException("Unknown extended sub-format-type: " + Long.toHexString(0xffffffffL & extendedSubFormat));
		}
		
		return builder.rate(chunk.getSampleRate()).bitDepth(chunk.getValidBitsPerSample());
	}

	private static AudioFormat getKnownMismatchExceptionFormat(AudioFormatChunk chunk, int containerBytesPerSample, int specifiedBitsPerSample) {
		if (specifiedBitsPerSample == 24 && containerBytesPerSample == 4) {
			return createFloatBuilder(chunk)
					.rate(chunk.getSampleRate())
					.set0DbfValue(Math.pow(2.0, 23.0))
					.bitDepth(32);
		}
		
		return null;
	}
	
	
	public static AudioFormatSampleRateSetter createPcmBuilder(AudioFormatChunk chunk) {
		SpeakerLocations locations = getLocations(chunk);
		int channels = chunk.getChannels();
		AudioFormatSampleRateSetter builder;
		if (locations != null) {
			builder = AudioFormats.pcm().locations(locations);
		}
		else {
			builder = AudioFormats.pcm().channels(channels);
		}
		return builder;
	}

	public static AudioFormatSampleRateSetter createFloatBuilder(AudioFormatChunk chunk) {
		SpeakerLocations locations = getLocations(chunk);
		int channels = chunk.getChannels();
		AudioFormatSampleRateSetter builder;
		if (locations != null) {
			builder = AudioFormats.floats().locations(locations);
		}
		else {
			builder = AudioFormats.floats().channels(channels);
		}
		return builder;
	}

	public static SpeakerLocations getLocations(AudioFormatChunk chunk) {
		int channels = chunk.getChannels();
		SpeakerLocations locations = null;
		if (chunk.hasExtendedChannelMask()) {
			locations = SpeakerLocations.ofMask(chunk.getExtendedChannelMask());
			if (locations.getChannels() != channels) {
				throw new IllegalArgumentException("Number of channels (" + channels + ") doesn't match with channel mask " + locations);
			}
		}
		return locations;
	}
}
