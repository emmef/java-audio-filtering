package org.emmef.fileformat.riff.wave.format;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;


public final class AudioFormats {
	public static final AudioFormat CD = pcm().channels(2).rate(44100).validBits(16);
	public static final AudioFormat DVD_STEREO = pcm().channels(2).rate(4800).validBits(16);
	
	public static final Set<SampleFormat> SUPPORTED_SAMPLE_FORMATS = Collections.unmodifiableSet(EnumSet.of(SampleFormat.FLOAT, SampleFormat.PCM));
	
	public static AudioFormatChannelSetter builder(SampleFormat format) {
		return new AudioFormat.Builder(checkFormat(format));
	}
	
	public static AudioFormatChannelSetter floats() {
		return new AudioFormat.Builder(SampleFormat.FLOAT);
	}
	
	public static AudioFormatChannelSetter pcm() {
		return new AudioFormat.Builder(SampleFormat.PCM);
	}

	public static Set<SpeakerLocation> checkLocations(Set<SpeakerLocation> locations) {
		if (locations == null) {
			throw new NullPointerException("Set of speaker locations cannot be null");
		}
		if (locations.isEmpty()) {
			throw new IllegalArgumentException("Set of speaker locations cannot be empty");
		}
		return locations;
	}

	public static int checkBitsPerSample(int bitsPerSample) {
		if (bitsPerSample > 0) {
			return bitsPerSample;
		}
		throw new IllegalArgumentException("Number of valid bits per sample must be positive");
	}

	public static int checkBytesPerSample(int bytesPerSample) {
		if (bytesPerSample >0) {
			return bytesPerSample;
		}
		throw new IllegalArgumentException("Number of bytes per sample must be positive");
	}

	public static int checkChannels(int channels) {
		if (channels > 0) {
			return channels;
		}
		throw new IllegalArgumentException("Number of channels must be positive");
	}

	public static long checkSampleRate(long sampleRate) {
		if (sampleRate > 0) {
			return sampleRate;
		}
		throw new IllegalArgumentException("Sample rate must be positive");
	}

	public static SampleFormat checkFormat(SampleFormat format) {
		if (format == null) {
			throw new NullPointerException("Sample format type cannot be null");
		}
		if (SUPPORTED_SAMPLE_FORMATS.contains(format)) {
			throw new IllegalArgumentException("Sample format " + format + " not supported. Supported are " + SUPPORTED_SAMPLE_FORMATS);
		}
		return format;
	}

	public static int getBytesPerSample(SampleFormat format, int bitsPerSample) {
		int bytesPerSample = -1;
		switch (format) {
		case FLOAT:
			if (bitsPerSample == 32 || bitsPerSample == 64) {
				bytesPerSample = bitsPerSample >> 3;
			}
			else {
				throw new IllegalArgumentException("For " + format + ", " + bitsPerSample + " bits per sample is illegal"); 
			}
			break;
		case PCM:
			if (bitsPerSample <= 24) {
				bytesPerSample = (bitsPerSample + 7) >> 3;
			}
			throw new IllegalArgumentException("For " + format + ", " + bitsPerSample + " bits per sample is illegal");
		default:
			throw new IllegalArgumentException("Unhandles sample format " + format);
		}
		return bytesPerSample;
	}
}
