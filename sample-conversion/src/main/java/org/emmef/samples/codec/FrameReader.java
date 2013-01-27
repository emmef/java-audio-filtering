package org.emmef.samples.codec;

import static org.emmef.utils.Preconditions.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.emmef.utils.LimitedInputStream;

/**
 * Reads decoded frames from an input stream into a sample buffer.
 * <p>
 * The reader only reads multiples of the <em>frame size</em> from the input
 * stream. The frame size is the number of channels in each frame times the
 * number of bytes per serialized sample.
 * <p>
 * There is also a maximum number of frames that will be read from the input
 * stream that can be set at construction.
 */
public class FrameReader {
	private final int channels;
	private final byte[] buffer;
	private final InputStream source;
	private final SampleCodec decoder;
	private final int bytesPerFrame;
	private final int bytesPerSample;
	private int position;
	private int limit;

	/**
	 * Creates a new frame reader.
	 * 
	 * @param channels
	 *            number of channels per frame
	 * @param bufferSize
	 *            size of the internal buffer, that will be rounded up to the
	 *            next multiple of the frame size
	 * @param source
	 *            input stream with serialized data
	 * @param maxFrames
	 *            the maximum number of frames to read. When this number of
	 *            frame has been read, the read methods always return 0 and no
	 *            samples are read.
	 * @param decoder
	 *            codec that deserializes and scales the samples.
	 */
	public FrameReader(int channels, int bufferSize, InputStream source, long maxFrames, SampleCodec decoder) {
		if (channels < 1) {
			throw new IllegalArgumentException("Number of channels must be at least 1");
		}
		this.channels = channels;
		bytesPerSample = decoder.bytesPerSample();
		bytesPerFrame = channels * bytesPerSample;
		
		buffer = new byte[Math.max((bufferSize + bytesPerFrame - 1) / bytesPerFrame, 1) * bytesPerFrame];
		this.source = new LimitedInputStream(checkNotNull(source, "source"), maxFrames * bytesPerFrame, WholeFrameEndOfFileAction.create(bytesPerFrame));
		this.decoder = checkNotNull(decoder, "Sample decoder");
	}
	
	/**
	 * Reads a number of decoded frames in the target buffer.
	 * 
	 * @param target
	 *            target buffer
	 * @param offset
	 *            offset within the target buffer to write to
	 * @param count
	 *            the number of frames to read
	 * @return the actual number of frames read
	 * @throws EOFException
	 *             if the end of the stream was reached unexpectedly and the
	 *             number of read bytes was not a complete frame.
	 * @throws IOException
	 *             if an IO problem occurred in the input stream
	 * @throws IllegalArgumentException
	 *             if the {@code target} buffer is {@code null} or if the
	 *             {@code offset} and {@code count} * {@code number-of-channels}
	 *             exceeds the buffer limit.
	 */
	public long read(double[] target, int offset, int count) throws IOException {
		int endOffset = offset + channels * count;
		
		checkOffsetAndCount(checkNotNull(target, "target").length, endOffset, count);
		
		if (endOffset > target.length) {
			throw new IllegalArgumentException("Attempt to read past end of destination buffer: length=" + target.length + "; offset=" + offset + "; count=" + count + "; channels=" + channels + "; end-offset = " + endOffset);
		}
		int actualCount = 0;
		int targetOffset = offset;
		while (actualCount < count) {
			if (position < limit) {
				for (int i = 0; i < channels; i++, position += bytesPerSample) {
					target[targetOffset++] = decoder.decodeDouble(buffer, position);
				}
				actualCount++;
			}
			else {
				int reads = source.read(buffer);
				if (reads == -1) {
					return actualCount;
				}
				limit = bytesPerFrame * (reads / bytesPerFrame);
			}
		}
		return actualCount;
	}
	
	/**
	 * Reads a number of decoded frames in the target buffer.
	 * 
	 * @param target
	 *            target buffer
	 * @param offset
	 *            offset within the target buffer to write to
	 * @param count
	 *            the number of frames to read
	 * @return the actual number of frames read
	 * @throws EOFException
	 *             if the end of the stream was reached unexpectedly and the
	 *             number of read bytes was not a complete frame.
	 * @throws IOException
	 *             if an IO problem occurred in the input stream
	 * @throws IllegalArgumentException
	 *             if the {@code target} buffer is {@code null} or if the
	 *             {@code offset} and {@code count} * {@code number-of-channels}
	 *             exceeds the buffer limit.
	 */
	public long read(float[] target, int offset, int count) throws IOException {
		int endOffset = offset + channels * count;
		
		checkOffsetAndCount(checkNotNull(target, "target").length, endOffset, count);
		
		int actualCount = 0;
		int targetOffset = offset;
		while (actualCount < count) {
			if (position < limit) {
				for (int i = 0; i < channels; i++, position += bytesPerSample) {
					target[targetOffset++] = decoder.decodeFloat(buffer, position);
				}
				actualCount++;
			}
			else {
				int reads = source.read(buffer);
				if (reads == -1) {
					return actualCount;
				}
				limit = bytesPerFrame * (reads / bytesPerFrame);
			}
		}
		return actualCount;
	}
	
	public int getBytesPerSample() {
		return bytesPerSample;
	}
	
	public int getBytesPerFrame() {
		return bytesPerFrame;
	}
}
