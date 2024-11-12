package org.emmef.config.nativeloader;

public enum LoaderResult {
	NEWLY_LOADED(true), ALREADY_LOADED(true), IO_FAILURE(false), UNAVAILABLE_FOR_PLATFORM(false);
	
	private final boolean success;
	
	private LoaderResult(boolean success) {
		this.success = success;
	}
	
	public boolean success() {
		return success;
	}
}
