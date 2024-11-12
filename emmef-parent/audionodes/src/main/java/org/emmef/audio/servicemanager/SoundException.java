package org.emmef.audio.servicemanager;

public class SoundException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SoundException(Throwable e) {
		super(e);
	}

	public SoundException(String message) {
		super(message);
	}
}
