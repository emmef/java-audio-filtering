package org.emmef.audio.nodes;

import java.io.IOException;

import org.emmef.audio.format.OpaqueFormat;

public interface SoundSink<T extends OpaqueFormat> extends SoundSourceOrSink<T> {

	long writeFrames(double[] buffer) throws IOException;

	long writeFrames(float[] buffer) throws IOException;

	long writeFrames(double[] buffer, int frameCount) throws IOException;

	long writeFrames(float[] buffer, int frameCount) throws IOException;
}
