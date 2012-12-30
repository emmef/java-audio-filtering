package org.emmef.audio.nodes;

import java.io.IOException;

import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.frame.Whence;

public interface SoundSourceOrSink {
	SoundMetrics getMetrics();
	Object getMetaData();
	
	long seekFrame(long framePosition, Whence whence) throws IOException;
	void close() throws IOException;
}
