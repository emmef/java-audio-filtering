package org.emmef.samples.codec;

import java.io.EOFException;

import org.emmef.utils.EndOfFile;

/**
 * {@link EndOfFileAction} that throws an {@link EOFException} if the number of
 * bytes read was not a multiple of the number of bytes per frame.
 */
public final class WholeFrameEndOfFileAction implements EndOfFile.Event {
	private static final WholeFrameEndOfFileAction[] CACHE = createCache();
	
	private static WholeFrameEndOfFileAction[] createCache() {
		WholeFrameEndOfFileAction[] cache = new WholeFrameEndOfFileAction[32];
		for (int i = 0; i < cache.length; i++) {
			cache[i] = new WholeFrameEndOfFileAction(i + 1);
		}
		return cache;
	}
	
	public static WholeFrameEndOfFileAction create(int bytesPerFrame) {
		if (bytesPerFrame < 1) {
			throw new IllegalArgumentException("Bytes per frame must be a positive number");
		}
		if (bytesPerFrame <= CACHE.length) {
			return CACHE[bytesPerFrame - 1];
		}
		return new WholeFrameEndOfFileAction(bytesPerFrame);
	}
	
	private final int bytesPerFrame;

	private WholeFrameEndOfFileAction(int bytesPerFrame) {
		this.bytesPerFrame = bytesPerFrame;
	}
	
	@Override
	public EndOfFile.Result onEndOfFile(long requestedNumber, long actualNumber) throws EOFException {
		if (actualNumber % bytesPerFrame != 0) {
			return new EndOfFile.Result.Throw("Incomplete frame read");
		}
		
		return EndOfFile.Result.CONTINUE;
	}
}