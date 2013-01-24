package org.emmef.samples;

import java.nio.ByteBuffer;

public interface SampleEncoder extends BytesPerSample {
	void encodeDouble(ByteBuffer buffer, double sample);
	void encodeFloat(ByteBuffer buffer, float sample);
}
