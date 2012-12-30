package org.emmef.audio.servicemanager;

public class SoundException extends IllegalArgumentException {

	public SoundException(Throwable e) {
		super(e);
	}

	public SoundException(String message) {
		super(message);
	}
}
