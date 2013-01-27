package org.emmef.samples;


public interface SampleDecoder extends BytesPerSample {
	double decodeDouble(byte[] buffer, int offset);
	float decodeFloat(byte[] buffer, int offset);
}
