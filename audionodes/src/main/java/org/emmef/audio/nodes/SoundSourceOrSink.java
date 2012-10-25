package org.emmef.audio.nodes;

import java.io.IOException;

import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.frame.Whence;

public interface SoundSourceOrSink<T> {
	SoundMetrics getMetrics();
	T getMetaData();
	
	long seekFrame(long framePosition, Whence whence) throws IOException;
	void close() throws IOException;
}
