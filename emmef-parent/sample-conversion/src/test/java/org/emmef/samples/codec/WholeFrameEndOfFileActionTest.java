package org.emmef.samples.codec;

import static org.junit.Assert.*;

import java.io.EOFException;

import org.junit.Ignore;
import org.junit.Test;

public class WholeFrameEndOfFileActionTest {

	@Test(expected=IllegalArgumentException.class)
	public void testZeroBytesPerFrame() {
		WholeFrameEndOfFileAction.create(0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNegativeBytesPerFrame() {
		WholeFrameEndOfFileAction.create(-1);
	}

	/**
	 * TODO Validate test
	 */
	@Ignore
	@Test
	public void test() {
		for (int bytesPerFrame = 1; bytesPerFrame < 128; bytesPerFrame++) {
			WholeFrameEndOfFileAction action = WholeFrameEndOfFileAction.create(bytesPerFrame);
			
			for (int requestedFrames = 0; requestedFrames < 5 * bytesPerFrame; requestedFrames += bytesPerFrame) {
				for (int actualFrames = 0; actualFrames <= requestedFrames; actualFrames++) {
					boolean wholeNumberOfFrames = actualFrames % bytesPerFrame == 0;
					Exception exception = null;
					try {
						action.onEndOfFile(requestedFrames, actualFrames);
					}
					catch (Exception e) {
						exception = e;
					}
					
					if (exception != null) {
						if (exception instanceof EOFException) {
							if (wholeNumberOfFrames) {
								fail("Unexpected exception as " + actualFrames + " is a multple of " + bytesPerFrame);
							}
						}
						else {
							fail("Unexpected exception: expected " + EOFException.class + " but got " + exception);
						}
					}
					else if (!wholeNumberOfFrames) {
						fail("Expected an exceptions as " + actualFrames + " is not a mltiple of " + bytesPerFrame);
					}
				}
			}
		}
	}
}
