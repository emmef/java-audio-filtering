package org.emmef.samples;

import java.io.IOException;
import java.io.OutputStream;

import org.emmef.fileformat.riff.RiffUtils;

public class FrameWriter {
	private final int channels;
	private final OutputStream output;
	private final SampleEncoder encoder;
	private final int bytesPerSample;
	private final byte[] buffer;
	private long totalFrames;
	
	public FrameWriter(int channels, OutputStream output, SampleEncoder encoder) {
		this.channels = channels;
		this.output = RiffUtils.checkNotNull(output, "Output stream cannot be null");
		this.encoder = RiffUtils.checkNotNull(encoder, "Sample encoder");
		bytesPerSample = encoder.bytesPerSample();
		buffer = new byte[channels * bytesPerSample];
	}
	
	public long getTotalFrames() {
		return totalFrames;
	}
	
	public void writeFrame(double[] target, int offset, int count) throws IOException {
		RiffUtils.checkBufferOffsetAndLength(target.length, offset, count);
		int position = 0;
		for (int i = 0; i < count; i++) {
			for (int channel = 0; channel < channels; channel++, position += bytesPerSample) {
				encoder.encodeDouble(target[offset++], buffer, position);
			}
			output.write(buffer);
			totalFrames++;
		}
	}
	
	public void writeFrame(float[] target, int offset, int count) throws IOException {
		RiffUtils.checkBufferOffsetAndLength(target.length, offset, count);
		int position = 0;
		for (int i = 0; i < count; i++) {
			for (int channel = 0; channel < channels; channel++, position += bytesPerSample) {
				encoder.encodeFloat(target[offset++], buffer, position);
			}
			output.write(buffer);
			totalFrames++;
		}
	}

}
