package org.emmef.utils;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SimpleTestStreamTest {
	static final int LIMIT = 100;
	static final byte[] BUFFER = new byte[LIMIT];
	static final SimpleTestStream STREAM = new SimpleTestStream(LIMIT);

	@Before
	public void resetStream() {
		STREAM.resetStream();
	}

	/**
	 * TODO Validate test
	 */
	@Test
	@Ignore
	public void testStreamRead() throws IOException {
		for (int i = 0; i < LIMIT; i++) {
			assertTrue("Positive value while end of stream not yet reached.", STREAM.read() > 0);
		}
		assertTrue("EOF if end of buffer reached", STREAM.read() == -1);
	}
	
	@Test
	public void testStreamSkip() throws IOException {
		for (int i = 0; i < LIMIT; i++) {
			assertTrue("Skip when end of stream not yet reached", STREAM.skip(1) == 1);
		}
		assertTrue("Zero skip() if end of buffer reached", STREAM.skip(1) == 0);
	}
	
	@Test
	public void testStreamSkipReadMix() throws IOException {
		long total = 0;
		do {
			int count = (int)(Math.random() * 10);
			long expectedResult = total + count > LIMIT ? LIMIT - total : count;
			long actual;
			if (Math.random() > 0.5) {
				actual = STREAM.skip(count);
			}
			else {
				actual = STREAM.read(BUFFER, 0, count);
			}
			assertEquals("Number of expected reads or skips (read=" + total + "; count=" + count + "; expected=" + expectedResult + "; actual=" + actual, expectedResult, actual);
			total += actual;
		}
		while (total < LIMIT);
	}

}
