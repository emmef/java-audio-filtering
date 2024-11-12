package org.emmef.fileformat.iff.parse;

import org.emmef.fileformat.iff.InterchangeFormatException;

public class UnrecognizedContentTypeException extends InterchangeFormatException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnrecognizedContentTypeException(String message) {
		super(message);
	}

}
