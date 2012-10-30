package org.emmef.audio.format;

public interface AudioFormatBitDepthSetter {
	AudioFormat bitDepth(int validBitsPerSample);
	AudioFormatBitDepthSetter set0DbfValue(double zeroDbSampleValue);
}
