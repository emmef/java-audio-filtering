package org.emmef.fileformat.riff.wave.format;

public interface AudioFormatSampleRateSetter {
	AudioFormatBitDepthSetter rate(long sampleRate);
	AudioFormatBitDepthSetter cd();
	AudioFormatBitDepthSetter dvd();
}
