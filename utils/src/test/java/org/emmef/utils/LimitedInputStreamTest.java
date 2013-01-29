package org.emmef.utils;

import static org.junit.Assert.*;

import java.io.EOFException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LimitedInputStreamTest {
	static final int LIMIT = 100;
	static final int HALF_LIMIT = LIMIT / 2;
	static final byte[] BUFFER = new byte[LIMIT];
	static final SimpleTestStream LARGE_STREAM = new SimpleTestStream(2 * LIMIT);
	static final SimpleTestStream LIMIT_STREAM = new SimpleTestStream(LIMIT);
	static final SimpleTestStream SMALL_STREAM = new SimpleTestStream(HALF_LIMIT);
	
	private LimitedInputStream streamUsingLargeStream;
	private LimitedInputStream streamUsingEqualStream;
	private LimitedInputStream streamUsingSmallStream;
	private LimitedInputStream streamUsingLargeStreamThrows;
	private LimitedInputStream streamUsingEqualStreamThrows;
	private LimitedInputStream streamUsingSmallStreamThrows;

	@Before
	public void resetStreams() {
		LARGE_STREAM.resetStream();
		LIMIT_STREAM.resetStream();
		SMALL_STREAM.resetStream();
		streamUsingLargeStream = new LimitedInputStream(LARGE_STREAM, LIMIT, EndOfFile.Event.CONTINUE);
		streamUsingEqualStream = new LimitedInputStream(LIMIT_STREAM, LIMIT, EndOfFile.Event.CONTINUE);
		streamUsingSmallStream = new LimitedInputStream(SMALL_STREAM, LIMIT, EndOfFile.Event.CONTINUE);
		streamUsingLargeStreamThrows = new LimitedInputStream(LARGE_STREAM, LIMIT, EndOfFile.Event.THROW);
		streamUsingEqualStreamThrows = new LimitedInputStream(LIMIT_STREAM, LIMIT, EndOfFile.Event.THROW);
		streamUsingSmallStreamThrows = new LimitedInputStream(SMALL_STREAM, LIMIT, EndOfFile.Event.THROW);
	}
	
	@After
	public void closeStream() {
	}

	@Test
	public void testStreamReadLargeDelegate() throws IOException {
		LimitedInputStream stream = streamUsingLargeStream;
		int limit = LIMIT;
		for (int i = 0; i < limit; i++) {
			int read = stream.read();
			assertTrue("Non-negative value while end of stream not yet reached (reads=" + i + "; value=" + read + ")", read >= 0);
		}
		assertTrue("EOF if end of buffer reached", stream.read() == -1);
	}
	
	@Test
	public void testStreamSkipLargeDelegate() throws IOException {
		LimitedInputStream stream = streamUsingLargeStream;
		int limit = LIMIT;
		for (int i = 0; i < limit; i++) {
			long skips = stream.skip(1);
			assertTrue("Positive skip value while end of stream not yet reached (reads=" + i + "; value=" + skips + ")", skips > 0);
		}
		assertTrue("Zero skip() if end of buffer reached", stream.skip(1) == 0);
	}
	
	@Test
	public void testStreamSkipReadMixLargeDelegate() throws IOException {
		LimitedInputStream stream = streamUsingLargeStream;
		int limit = LIMIT;
		long total = 0;
		do {
			int count = (int)(Math.random() * 10);
			long expectedResult = total + count > limit ? limit - total : count;
			long actual;
			if (Math.random() > 0.5) {
				actual = stream.skip(count);
				assertEquals("Number of expected skips (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
			}
			else {
				actual = stream.read(BUFFER, 0, count);
				assertEquals("Number of expected reads (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
			}
			total += actual;
		}
		while (total < limit);
	}

	@Test
	public void testStreamReadEqualDelegate() throws IOException {
		LimitedInputStream stream = streamUsingEqualStream;
		int limit = LIMIT;
		for (int i = 0; i < limit; i++) {
			int read = stream.read();
			assertTrue("Non-negative value while end of stream not yet reached (reads=" + i + "; value=" + read + ")", read >= 0);
		}
		assertTrue("EOF if end of buffer reached", stream.read() == -1);
	}
	
	@Test
	public void testStreamSkipEqualDelegate() throws IOException {
		LimitedInputStream stream = streamUsingEqualStream;
		int limit = LIMIT;
		for (int i = 0; i < limit; i++) {
			long skips = stream.skip(1);
			assertTrue("Positive skip value while end of stream not yet reached (reads=" + i + "; value=" + skips + ")", skips > 0);
		}
		assertTrue("Zero skip() if end of buffer reached", stream.skip(1) == 0);
	}
	
	@Test
	public void testStreamSkipReadMixEqualDelegate() throws IOException {
		LimitedInputStream stream = streamUsingEqualStream;
		int limit = LIMIT;
		long total = 0;
		do {
			int count = (int)(Math.random() * 10);
			long expectedResult = total + count > limit ? limit - total : count;
			long actual;
			if (Math.random() > 0.5) {
				actual = stream.skip(count);
				assertEquals("Number of expected skips (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
			}
			else {
				actual = stream.read(BUFFER, 0, count);
				assertEquals("Number of expected reads (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
			}
			total += actual;
		}
		while (total < limit);
	}
	
	@Test
	public void testStreamReadSmallerDelegate() throws IOException {
		LimitedInputStream stream = streamUsingSmallStream;
		int limit = HALF_LIMIT;
		for (int i = 0; i < limit; i++) {
			int read = stream.read();
			assertTrue("Non-negative value while end of stream not yet reached (reads=" + i + "; value=" + read + ")", read >= 0);
		}
		assertTrue("EOF if end of buffer reached", stream.read() == -1);
	}
	
	@Test
	public void testStreamSkipSmallerDelegate() throws IOException {
		LimitedInputStream stream = streamUsingSmallStream;
		int limit = HALF_LIMIT;
		for (int i = 0; i < limit; i++) {
			long skips = stream.skip(1);
			assertTrue("Positive skip value while end of stream not yet reached (reads=" + i + "; value=" + skips + ")", skips > 0);
		}
		assertTrue("Zero skip() if end of buffer reached", stream.skip(1) == 0);
	}
	
	@Test
	public void testStreamSkipReadMixSmallerDelegate() throws IOException {
		LimitedInputStream stream = streamUsingSmallStream;
		int limit = HALF_LIMIT;
		long total = 0;
		do {
			int count = (int)(Math.random() * 10);
			long expectedResult = total + count > limit ? limit - total : count;
			long actual;
			if (Math.random() > 0.5) {
				actual = stream.skip(count);
				assertEquals("Number of expected skips (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
			}
			else {
				actual = stream.read(BUFFER, 0, count);
				assertEquals("Number of expected reads (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
			}
			total += actual;
		}
		while (total < limit);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Test
	public void testStreamReadLargeDelegateThrows() throws IOException {
		LimitedInputStream stream = streamUsingLargeStreamThrows;
		int limit = LIMIT;
		for (int i = 0; i < limit; i++) {
			int read = stream.read();
			assertTrue("Non-negative value while end of stream not yet reached (reads=" + i + "; value=" + read + ")", read >= 0);
		}
		assertTrue("EOF if end of buffer reached", stream.read() == -1);
	}
	
	@Test
	public void testStreamSkipLargeDelegateThrows() throws IOException {
		LimitedInputStream stream = streamUsingLargeStreamThrows;
		int limit = LIMIT;
		for (int i = 0; i < limit; i++) {
			long skips = stream.skip(1);
			assertTrue("Positive skip value while end of stream not yet reached (reads=" + i + "; value=" + skips + ")", skips > 0);
		}
		assertTrue("Zero skip() if end of buffer reached", stream.skip(1) == 0);
	}
	
	@Test
	public void testStreamSkipReadMixLargeDelegateThrows() throws IOException {
		LimitedInputStream stream = streamUsingLargeStreamThrows;
		int limit = LIMIT;
		long total = 0;
		do {
			int count = (int)(Math.random() * 10);
			long expectedResult = total + count > limit ? limit - total : count;
			long actual;
			if (Math.random() > 0.5) {
				actual = stream.skip(count);
				assertEquals("Number of expected skips (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
			}
			else {
				actual = stream.read(BUFFER, 0, count);
				assertEquals("Number of expected reads (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
			}
			total += actual;
		}
		while (total < limit);
	}

	@Test
	public void testStreamReadEqualDelegateThrows() throws IOException {
		LimitedInputStream stream = streamUsingEqualStreamThrows;
		int limit = LIMIT;
		for (int i = 0; i < limit; i++) {
			int read = stream.read();
			assertTrue("Non-negative value while end of stream not yet reached (reads=" + i + "; value=" + read + ")", read >= 0);
		}
		assertTrue("EOF if end of buffer reached", stream.read() == -1);
	}
	
	@Test
	public void testStreamSkipEqualDelegateThrows() throws IOException {
		LimitedInputStream stream = streamUsingEqualStreamThrows;
		int limit = LIMIT;
		for (int i = 0; i < limit; i++) {
			long skips = stream.skip(1);
			assertTrue("Positive skip value while end of stream not yet reached (reads=" + i + "; value=" + skips + ")", skips > 0);
		}
		assertTrue("Zero skip() if end of buffer reached", stream.skip(1) == 0);
	}
	
	@Test
	public void testStreamSkipReadMixEqualDelegateThrows() throws IOException {
		LimitedInputStream stream = streamUsingEqualStreamThrows;
		int limit = LIMIT;
		long total = 0;
		do {
			int count = (int)(Math.random() * 10);
			long expectedResult = total + count > limit ? limit - total : count;
			long actual;
			if (Math.random() > 0.5) {
				actual = stream.skip(count);
				assertEquals("Number of expected skips (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
			}
			else {
				actual = stream.read(BUFFER, 0, count);
				assertEquals("Number of expected reads (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
			}
			total += actual;
		}
		while (total < limit);
	}
	
	@Test
	public void testStreamReadSmallerDelegateThrows() throws IOException {
		LimitedInputStream stream = streamUsingSmallStreamThrows;
		int limit = HALF_LIMIT;
		for (int i = 0; i < limit; i++) {
			int read = stream.read();
			assertTrue("Non-negative value while end of stream not yet reached (reads=" + i + "; value=" + read + ")", read >= 0);
		}
		try {
			stream.read();
			fail("EOF if end of buffer reached");
		}
		catch (EOFException e) {
			//
		}
	}
	
	@Test
	public void testStreamSkipSmallerDelegateThrows() throws IOException {
		LimitedInputStream stream = streamUsingSmallStreamThrows;
		int limit = HALF_LIMIT;
		for (int i = 0; i < limit; i++) {
			long skips = stream.skip(1);
			assertTrue("Positive skip value while end of stream not yet reached (reads=" + i + "; value=" + skips + ")", skips > 0);
		}
		try {
			stream.skip(1);
			fail("EOF if end of buffer reached");
		}
		catch (EOFException e) {
			//
		}
	}
	
	@Test
	public void testStreamSkipReadMixSmallerDelegateThrows() throws IOException {
		LimitedInputStream stream = streamUsingSmallStreamThrows;
		int limit = HALF_LIMIT;
		long total = 0;
		do {
			int count = (int)(Math.random() * 10);
			if (total + count == limit) {
				count++;
			}
			boolean underflow = total + count > limit;
			long expectedResult = underflow ? limit - total : count;
			long actual;
			boolean read = false;
			try {
				read = Math.random() > 0.5;
				if (read) {
					actual = stream.read(BUFFER, 0, count);
					assertEquals("Number of expected reads (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
				}
				else {
					actual = stream.skip(count);
					assertEquals("Number of expected skips (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
				}
				if (underflow) {
					fail("Expected EOF on exceeding buffer limit");
				}
				total += actual;
			}
			catch (EOFException e) {
				if (!underflow) {
					fail("Unexpected EOF while " + (read ? "reading" : "skipping") + "(read=" + total + "; count=" + count + "; expected=" + expectedResult + ")");
				}
				break;
			}
		}
		while (total < limit);
	}
}
