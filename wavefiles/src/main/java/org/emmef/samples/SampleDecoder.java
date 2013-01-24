package org.emmef.samples;

import java.nio.ByteBuffer;

public interface SampleDecoder extends BytesPerSample {
	double decodeDouble(ByteBuffer buffer);
	float decodeFloat(ByteBuffer buffer);
}
