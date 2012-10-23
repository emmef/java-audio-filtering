package org.emmef.audio.format;

public final class SoundMetrics<T extends OpaqueFormat> {
	private final long frames;
	private final int samplerate;
	private final int channels;
	private final boolean seekable;
	private final T binaryFormat;
	private final Class<? extends T> formatClass;
	
	public SoundMetrics(int channels, T binaryFormat, long frames, int samplerate, boolean seekable, Class<? extends T> formatClass) {
		if (channels <= 0) {
			throw new IllegalStateException("Number of channels cannot be 0");
		}
		if (samplerate <= 0) {
			throw new IllegalStateException("Samplerate must be positive");
		}
		if (binaryFormat == null) {
			throw new NullPointerException("binaryFormat");
		}
		if (formatClass == null) {
			throw new NullPointerException("formatClass");
		}
		
		this.channels = channels;
		this.binaryFormat = binaryFormat;
		this.frames = frames;
		this.samplerate = samplerate;
		this.seekable = seekable;
		this.formatClass = formatClass;
	}
	
	public SoundMetrics<T> withChannels(int alternativeChannels) {
		return new SoundMetrics<T>(alternativeChannels, binaryFormat, frames, samplerate, seekable, formatClass);
	}
	
	public int getChannels() {
		return channels;
	}

	public T getFormat() {
		return binaryFormat;
	}

	public long getFrames() {
		return frames;
	}

	public int getSamplerate() {
		return samplerate;
	}

	public boolean isSeekable() {
		return seekable;
	}
	
	public Class<? extends T> getFormatClass() {
		return formatClass;
	}
}