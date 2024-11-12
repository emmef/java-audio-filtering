package org.emmef.sndfile;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.format.AudioFormats;


public final class SubFormat {
	private final int format;
	private final String name;
	
	SubFormat(FormatInfo info) {
		format = info.format;
		name = info.name;
	}
	
	public int getFormat() {
		return format;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return Integer.toHexString(getFormat()) + '\t' + getName();
	}
	
	AudioFormat createAudioFormat(int channels, long sampleRate) {
		switch(format) {
		case SoundFileType.FORMAT_PCM_S8:
		case SoundFileType.FORMAT_PCM_U8:
			return AudioFormats.pcm().channels(channels).rate(sampleRate).bitDepth(8);
		case SoundFileType.FORMAT_PCM_16:
			return AudioFormats.pcm().channels(channels).rate(sampleRate).bitDepth(16);
		case SoundFileType.FORMAT_PCM_24:
			return AudioFormats.pcm().channels(channels).rate(sampleRate).bitDepth(24);
		case SoundFileType.FORMAT_PCM_32:
			return AudioFormats.pcm().channels(channels).rate(sampleRate).bitDepth(32);
		case SoundFileType.FORMAT_FLOAT:
			return AudioFormats.floats().channels(channels).rate(sampleRate).set0DbfValue(1.0).bitDepth(32);
		case SoundFileType.FORMAT_DOUBLE:
			return AudioFormats.floats().channels(channels).rate(sampleRate).set0DbfValue(1.0).bitDepth(64);
		case SoundFileType.FORMAT_ULAW:
		case SoundFileType.FORMAT_ALAW:
			return AudioFormats.floats().channels(channels).rate(sampleRate).set0DbfValue(1.0).bitDepth(8);
		default:
			return null;
		}
	}
}