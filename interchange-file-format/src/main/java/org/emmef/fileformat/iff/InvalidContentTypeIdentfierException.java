package org.emmef.fileformat.iff;

public class InvalidContentTypeIdentfierException extends InterchangeFormatException {

	public InvalidContentTypeIdentfierException(String message) {
		super(message);
	}

	public InvalidContentTypeIdentfierException(InvalidChunkIdentifierException e) {
		super(e);
	}

}
