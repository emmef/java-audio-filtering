package org.emmef.audio.nodes;

import java.io.IOException;

import org.emmef.audio.format.OpaqueFormat;
import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.frame.FrameType;
import org.emmef.audio.frame.Whence;

public interface SoundSourceOrSink<T extends OpaqueFormat> {
	long getFrameCount();
	boolean isSeekable();
	SoundMetrics<T> createInfo();
	T getBinaryFormat();
	FrameType getFrameType();
	
	long seekFrame(long framePosition, Whence whence) throws IOException;
	void close() throws IOException;
}
