package org.emmef.samples.codec;

import static com.google.common.base.Preconditions.*;
import static org.emmef.utils.Checks.*;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes encoded frames from a sample buffer to an output stream.
 */
public class FrameWriter {
	private final int channels;
	private final OutputStream output;
	private final SampleCodec encoder;
	private final int bytesPerSample;
	private final byte[] buffer;
	private long totalFrames;
	
	/**
	 * Creates a new frame writer.
	 * 
	 * @param channels number of channels per frame
	 * @param output output stream that will contain serialized data
	 * @param encoder codec that scales and serializes the samples.
	 */
	public FrameWriter(int channels, OutputStream output, SampleCodec encoder) {
		this.channels = channels;
		this.output = checkNotNull(output, "Output stream cannot be null");
		this.encoder = checkNotNull(encoder, "Sample encoder");
		bytesPerSample = encoder.bytesPerSample();
		buffer = new byte[channels * bytesPerSample];
	}

	/**
	 * Returns the number of frames written so far.
	 * <p>
	 * This is always a zero or positive value.
	 * 
	 * @return the number of frames written so far.
	 */
	public long getTotalFrames() {
		return totalFrames;
	}

	/**
	 * Writes a number of frames from a sample buffer and encodes them to the
	 * output stream.
	 * 
	 * @param source
	 *            source buffer
	 * @param offset
	 *            offset within the source buffer to read from
	 * @param count
	 *            the number of frames to write
	 * @throws IOException
	 *             if an IO problem occurred
	 * @throws IllegalArgumentException
	 *             if the {@code source} buffer is {@code null} or if the
	 *             {@code offset} and {@code count} * {@code number-of-channels}
	 *             exceeds the buffer limit.
	 */
	public void writeFrame(double[] source, int offset, int count) throws IOException {
		checkOffsetAndCount(checkNotNull(source, "target").length, offset, count);
		int position = 0;
		for (int i = 0; i < count; i++) {
			for (int channel = 0; channel < channels; channel++, position += bytesPerSample) {
				encoder.encodeDouble(source[offset++], buffer, position);
			}
			output.write(buffer);
			totalFrames++;
		}
	}

	/**
	 * Writes a number of frames from a sample buffer and encodes them to the
	 * output stream.
	 * 
	 * @param source
	 *            source buffer
	 * @param offset
	 *            offset within the source buffer to read from
	 * @param count
	 *            the number of frames to write
	 * @throws IOException
	 *             if an IO problem occurred
	 * @throws IllegalArgumentException
	 *             if the {@code source} buffer is {@code null} or if the
	 *             {@code offset} and {@code count} * {@code number-of-channels}
	 *             exceeds the buffer limit.
	 */
	public void writeFrame(float[] source, int offset, int count) throws IOException {
		checkOffsetAndCount(checkNotNull(source, "target").length, offset, count);
		int position = 0;
		for (int i = 0; i < count; i++) {
			for (int channel = 0; channel < channels; channel++, position += bytesPerSample) {
				encoder.encodeFloat(source[offset++], buffer, position);
			}
			output.write(buffer);
			totalFrames++;
		}
	}

}
