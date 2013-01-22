package org.emmef.samples;

import java.nio.ByteBuffer;

public interface SampleReader {
	double readDouble(ByteBuffer buffer);
	float readFloat(ByteBuffer buffer);
	int bytesPerSample();
}
