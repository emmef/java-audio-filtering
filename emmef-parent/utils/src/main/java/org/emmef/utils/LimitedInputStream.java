package org.emmef.utils;

import javax.annotation.Nonnull;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream that reads at most a certain number of bytes
 * from another input stream.
 * <p>
 * This stream does not support "mark" features even if the
 * underlying stream supports them.
 */
public class LimitedInputStream extends InputStream {
	private final InputStream delegate;
	private final long readLimit;
	private final EndOfFile.Event endOfFileEvent;
	private long totalReads;

	/**
	 * Creates a limited input stream that reads at most {@long readLimit} bytes
	 * from {@code delegate}.
	 * <p>
	 * If in one of the read or skip methods the actual number of bytes read is
	 * lower than the expected number, the {@code condition} determines what
	 * should happen.
	 * 
	 * @param delegate
	 *            input stream
	 * @param readLimit
	 *            maximum number of bytes read or skipped from the stream
	 * @param endOfFileAction
	 *            determines what happens if the actual number of bytes read
	 *            from the stream is lower than the expected number in one of
	 *            the read methods.
	 */
	public LimitedInputStream(@Nonnull InputStream delegate, long readLimit, @Nonnull EndOfFile.Event endOfFileAction) {
		if (delegate == null) {
			throw new IllegalArgumentException("Delegate inputstream cannot be null");
		}
		if (readLimit < 1) {
			throw new IllegalArgumentException("Read limit must be a positive number");
		}
		this.readLimit = readLimit;
		this.delegate = delegate;
		endOfFileEvent = endOfFileAction;
	}
	
	/**
	 * Creates a limited input stream that reads at most {@long readLimit} bytes
	 * from {@code delegate}.
	 * <p>
	 * If in one of the read or skip methods the actual number of bytes read is
	 * lower than the expected number, an {@link EOFException} will be thrown.
	 * 
	 * @param delegate
	 *            input stream
	 * @param readLimit
	 *            maximum number of bytes read or skipped from the stream
	 */
	public LimitedInputStream(InputStream delegate, long readLimit) {
		this(delegate, readLimit, EndOfFile.Event.THROW);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If no bytes could be read, {@link EndOfFile.Action} passed at
	 * construction can decide to throw an {@link EOFException}.
	 */
	@Override
	public int read() throws IOException {
		if (totalReads < readLimit) {
			int read = delegate.read();
			if (read >= 0) {
				totalReads++;
			}
			else {
				EndOfFile.Handler.handle(endOfFileEvent, 1, 0);
			}

			return read;
		}
		
		return -1;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If not enough/no bytes could be read, {@link EndOfFile.Action} passed at
	 * construction can decide to throw an {@link EOFException}.
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (off + len > b.length) {
			throw new IllegalArgumentException("Offset (" + off + ") and length (" + len + ") exceed buffer size (" + b.length + ")");
		}
		long requestedOrRemaining = getRequestedOrRemaining(len);
		if (requestedOrRemaining == 0) {
			return 0;
		}
		return (int)checkAndReturnActual(requestedOrRemaining, delegate.read(b, off, (int)requestedOrRemaining));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * If not enough/no bytes could be read, {@link EndOfFile.Action} passed at
	 * construction can decide to throw an {@link EOFException}.
	 */
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If not enough/no bytes could be skipped, {@link EndOfFile.Action} passed
	 * at construction can decide to throw an {@link EOFException}.
	 */
	@Override
	public long skip(long n) throws IOException {
		long requestedOrRemaining = getRequestedOrRemaining(n);
		if (requestedOrRemaining == 0) {
			return 0;
		}
		return checkAndReturnActual(requestedOrRemaining, delegate.skip(requestedOrRemaining));
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new IOException("Mark not supported");
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	private long checkAndReturnActual(long expectedSkips, long skips) throws IOException {
		totalReads += skips;
		
		if (skips < expectedSkips) {
			EndOfFile.Handler.handle(endOfFileEvent, expectedSkips, skips);
		}
		
		return skips;
	}

	private long getRequestedOrRemaining(long n) {
		if (totalReads + n > readLimit) {
			return readLimit - totalReads;
		}
		else {
			return n;
		}
	}
}