package org.emmef.audio.nodes;

import java.io.IOException;

import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.frame.Whence;

public interface SoundSourceOrSink extends AutoCloseable {
	SoundMetrics getMetrics();
	Object getMetaData();
	
	long seekFrame(long framePosition, Whence whence) throws IOException;
	@Override
	void close() throws IOException;
}
