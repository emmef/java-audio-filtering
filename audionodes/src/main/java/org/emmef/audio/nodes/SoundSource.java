package org.emmef.audio.nodes;

import java.io.IOException;

import org.emmef.audio.format.OpaqueFormat;

public interface SoundSource<T extends OpaqueFormat> extends SoundSourceOrSink<T> {
	
	long readFrames(double[] buffer) throws IOException;

	long readFrames(float[] buffer) throws IOException;

	long readFrames(double[] buffer, int frameCount) throws IOException;

	long readFrames(float[] buffer, int frameCount) throws IOException;

}
