package org.emmef.fileformat.iff.parse;

import org.emmef.fileformat.iff.InterchangeFormatException;

public class UnrecognizedChunkException extends InterchangeFormatException {

	public UnrecognizedChunkException(String message) {
		super(message);
	}

}
