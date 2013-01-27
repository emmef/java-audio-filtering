package org.emmef.audio.format;

import org.emmef.audio.frame.FrameType;

public final class SoundMetrics implements FrameMetrics, SampleType, StreamProperties {
	private final AudioFormat audioFormat;
	private final long frameCount;
	private final boolean seekable;
	
	public SoundMetrics(AudioFormat format, long frameCount, boolean seekable) {
		audioFormat = format;
		this.frameCount = frameCount;
		this.seekable = seekable;
	}

	@Override
	public final SampleFormat getSampleFormat() {
		return audioFormat.getSampleFormat();
	}
	
	public final AudioFormat getAudioFormat() {
		return audioFormat;
	}
	
	public final FrameType getFrameType() {
		return audioFormat;
	}
	
	@Override
	public final int getBytesPerSample() {
		return audioFormat.getBytesPerSample();
	}

	@Override
	public final int getValidBitsPerSample() {
		return audioFormat.getValidBitsPerSample();
	}
	
	@Override
	public boolean hasSpareBits() {
		return audioFormat.hasSpareBits();
	}

	@Override
	public final long getLocationMask() {
		return audioFormat.getLocationMask();
	}

	@Override
	public final double getValue0Dbf() {
		return audioFormat.getValue0Dbf();
	}

	@Override
	public final int getChannels() {
		return audioFormat.getChannels();
	}

	@Override
	public final long getSampleRate() {
		return audioFormat.getSampleRate();
	}
	
	@Override
	public long getFrames() {
		return frameCount;
	}

	@Override
	public boolean isSeekable() {
		return seekable;
	}
}