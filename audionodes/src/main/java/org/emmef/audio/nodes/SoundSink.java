package org.emmef.audio.nodes;

import java.io.IOException;

public interface SoundSink extends SoundSourceOrSink {

	long writeFrames(double[] buffer) throws IOException;

	long writeFrames(float[] buffer) throws IOException;

	long writeFrames(double[] buffer, int frameCount) throws IOException;

	long writeFrames(float[] buffer, int frameCount) throws IOException;
}
