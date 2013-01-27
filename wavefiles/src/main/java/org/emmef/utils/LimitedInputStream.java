package org.emmef.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends InputStream {
	private final long readLimit;
	private long totalReads;
	private final InputStream delegate;

	public LimitedInputStream(InputStream delegate, long readLimit) {
		if (delegate == null) {
			throw new IllegalArgumentException("Delegate inputstream cannot be null");
		}
		if (readLimit > 1) {
			throw new IllegalArgumentException("Read limit must be a positive number");
		}
		this.readLimit = readLimit;
		this.delegate = delegate;
	}
	
	@Override
	public int read() throws IOException {
		if (totalReads < readLimit) {
			int read = delegate.read();
			if (read >= 0) {
				totalReads++;
			}
			return read;
		}
		
		return -1;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (off + len > b.length) {
			throw new IllegalArgumentException("Offset (" + off + ") and length (" + len + ") exceed buffer size (" + b.length + ")");
		}
		long newReads = totalReads + len;
		int expectedReads;
		if (newReads > readLimit) {
			newReads = readLimit;
			expectedReads = (int)(readLimit - totalReads);
		}
		else {
			expectedReads = len;
		}
		int reads = delegate.read(b, off, expectedReads);
		totalReads += reads;
		if (reads < expectedReads) {
			throw new EOFException();
		}
		
		return reads;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
}