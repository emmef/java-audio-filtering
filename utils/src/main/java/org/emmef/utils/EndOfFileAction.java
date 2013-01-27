package org.emmef.utils;

import java.io.EOFException;

/**
 * Decides what happens when an input stream could not read or skip the
 * expected number of bytes.
 */
public interface EndOfFileAction {
	void onEndOfFile(long requestedNumber, long actualNumber) throws EOFException;
	
	/**
	 * If less bytes could be read than requested, just return the
	 * actual number of bytes in the input stream implementation.
	 */
	EndOfFileAction CONTINUE = new EndOfFileAction() {
		
		@Override
		public void onEndOfFile(long requestedNumber, long actualNumber) {
			// Nothing to do
		}
	};
	
	/**
	 * If less bytes could be read than requested, throw an {@link EOFException}.
	 */
	EndOfFileAction EOF = new EndOfFileAction() {
		
		
		@Override
		public void onEndOfFile(long requestedNumber, long actualNumber) throws EOFException {
			throw new EOFException();
		}
	};
}