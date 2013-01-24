package org.emmef.samples;

public interface BytesPerSample {
	enum Storage {
		FLOAT, TWOS_COMPLEMENT, SYMMETRIC
	}
	int bytesPerSample();
	Storage getStorage();
}
