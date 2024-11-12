package org.emmef.fileformat.iff;

public class InvalidContentTypeIdentfierException extends InterchangeFormatException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidContentTypeIdentfierException(String message) {
		super(message);
	}

	public InvalidContentTypeIdentfierException(InvalidChunkIdentifierException e) {
		super(e);
	}

}
