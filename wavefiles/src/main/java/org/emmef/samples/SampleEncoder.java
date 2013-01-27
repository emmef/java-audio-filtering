package org.emmef.samples;


public interface SampleEncoder extends BytesPerSample {
	void encodeDouble(double sample, byte[] buffer, int offset);
	void encodeFloat(float sample, byte[] buffer, int offset);
}
