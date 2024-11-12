package org.emmef.audio.format;

public interface StreamProperties {
	long getFrames();

	boolean isSeekable();
}
