package org.emmef.fileformat.iff;

public class InterchangeFormatException extends Exception {
	private static final long serialVersionUID = 1L;

	public InterchangeFormatException(String message) {
		super(message);
	}

	public InterchangeFormatException(Throwable e) {
		super(e);
	}
}
