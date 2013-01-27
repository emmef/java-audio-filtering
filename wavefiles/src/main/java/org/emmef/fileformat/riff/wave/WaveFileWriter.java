package org.emmef.fileformat.riff.wave;

import java.io.File;
import java.io.IOException;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.frame.Whence;
import org.emmef.audio.nodes.SoundSink;

class WaveFileWriter implements SoundSink {

	public WaveFileWriter(File file, AudioFormat format, int bufferSize) throws IOException {
		throw new UnsupportedOperationException("Actual writing of WAV files not yet supported!");
	}

	@Override
	public SoundMetrics getMetrics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long seekFrame(long framePosition, Whence whence) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public long writeFrames(double[] buffer) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long writeFrames(float[] buffer) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long writeFrames(double[] buffer, int frameCount) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long writeFrames(float[] buffer, int frameCount) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
