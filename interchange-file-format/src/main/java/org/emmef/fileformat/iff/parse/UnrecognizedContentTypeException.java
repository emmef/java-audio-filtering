package org.emmef.fileformat.iff.parse;

import org.emmef.fileformat.iff.InterchangeFormatException;

public class UnrecognizedContentTypeException extends InterchangeFormatException {

	public UnrecognizedContentTypeException(String message) {
		super(message);
	}

}
