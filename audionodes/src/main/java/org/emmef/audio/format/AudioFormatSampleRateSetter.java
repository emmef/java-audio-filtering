package org.emmef.audio.format;

public interface AudioFormatSampleRateSetter {
	AudioFormatBitDepthSetter rate(long sampleRate);
	AudioFormatBitDepthSetter cd();
	AudioFormatBitDepthSetter dvd();
}
