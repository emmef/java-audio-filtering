package org.emmef.fileformat.iff.parse;

import org.emmef.fileformat.iff.InterchangeFormatException;

public class UnrecognizedChunkException extends InterchangeFormatException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnrecognizedChunkException(String message) {
		super(message);
	}

}
