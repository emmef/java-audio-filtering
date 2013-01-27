package org.emmef.utils;

import java.io.IOException;
import java.io.InputStream;

class SimpleTestStream extends InputStream {
	private final long limit;
	private long read;

	SimpleTestStream(long limit) {
		this.limit = limit;
	}
	
	void resetStream() {
		read = 0;
	}
	
	@Override
	public int read() throws IOException {
		if (read < limit) {
			read++;
			return (int)(Math.random() * 256);
		}
		return -1;
	}
	
	@Override
	public long skip(long n) throws IOException {
		if (read + n > limit)  {
			long result = limit - read;
			read = limit;
			return result;
		}
		read += n;
		return n;
	}
}