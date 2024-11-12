package org.emmef.audio.servicemanager;

public class SoundUriUnsupportedException extends SoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SoundUriUnsupportedException(String message) {
		super(message);
	}

	public SoundUriUnsupportedException(Exception e) {
		super(e);
	}

}
