package org.emmef.audio.format;

import org.emmef.audio.frame.FrameType;

public final class SoundMetrics extends FrameType {
	private final long frameCount;
	private final boolean seekable;
	
	public SoundMetrics(int channels, long frames, int samplerate, boolean seekable) {
		super(channels, samplerate);
		if (channels <= 0) {
			throw new IllegalStateException("Number of channels cannot be 0");
		}
		if (samplerate <= 0) {
			throw new IllegalStateException("Samplerate must be positive");
		}
		
		this.frameCount = frames;
		this.seekable = seekable;
	}
	
	public SoundMetrics withChannels(int alternativeChannels) {
		return new SoundMetrics(alternativeChannels, frameCount, sampleRate, seekable);
	}
	
	public int getChannels() {
		return channels;
	}

	public long getFrames() {
		return frameCount;
	}

	public int getSamplerate() {
		return sampleRate;
	}

	public boolean isSeekable() {
		return seekable;
	}
}