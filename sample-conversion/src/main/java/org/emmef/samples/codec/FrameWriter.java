package org.emmef.samples.codec;

import static com.google.common.base.Preconditions.*;
import static org.emmef.utils.Checks.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes encoded frames from a sample buffer to an output stream.
 */
public class FrameWriter {
	private static final Logger log = LoggerFactory.getLogger(FrameWriter.class);
	
	private final Object lock;
	private final int channels;
	private final OutputStream output;
	private final SampleCodec codec;
	private final int bytesPerSample;
	private final byte[] buffer;
	private final int bytesPerFrame;
	private final AtomicLong framePosition = new AtomicLong();
	private int position;
	
	
	/**
	 * Creates a new frame writer.
	 * 
	 * @param channels number of channels per frame
	 * @param output output stream that will contain serialized data
	 * @param codec codec that scales and serializes the samples.
	 */
	public FrameWriter(int channels, OutputStream output, SampleCodec codec, int bufferSize, Object lock) {
		this.lock = lock != null ? lock : new Object[0];
		this.channels = channels;
		this.output = checkNotNull(output, "Output stream cannot be null");
		this.codec = checkNotNull(codec, "Sample encoder");
		bytesPerSample = codec.bytesPerSample();
		bytesPerFrame = bytesPerSample * channels;
		buffer = new byte[codec.getFrameBufferSize(bufferSize, 0, channels)];
	}

	public int getBytesPerFrame() {
		return bytesPerFrame;
	}
	
	/**
	 * Returns frame position, which is equal to the number of frames written so
	 * far if it was not changed with {@link #getAndSetFramePosition(long)}.
	 * <p>
	 * This is always a zero or positive value. The value gets only updated if
	 * frames have actually been written to the output stream.
	 * 
	 * @return the number of frames written so far.
	 */
	public long getFramePosition() {
		return framePosition.get();
	}
	
	/**
	 * Gets and sets the frame posi
	 * @param newPosition
	 * @return
	 */
	public long getAndSetFramePosition(long newPosition) {
		if (newPosition < 0) {
			throw new IllegalArgumentException("New frame position must be zero or positive");
		}
		return framePosition.getAndSet(newPosition);
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
	public long writeFrame(double[] source, int offset, int count) throws IOException {
		checkOffsetAndCount(checkNotNull(source, "target").length, offset, count, channels);
		int sourcePosition = offset;
		int sourceEndPosition = offset + count *  channels;
		
		synchronized (lock) {
			while (sourcePosition < sourceEndPosition) {
				while (position < buffer.length && sourcePosition < sourceEndPosition) {
					codec.encodeDouble(source[sourcePosition++], buffer, position);
					position += bytesPerSample;
				}
				if (position == buffer.length) {
					unsafeFlush();
				}
			}
		}
		return count;
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
	public long writeFrame(float[] source, int offset, int count) throws IOException {
		checkOffsetAndCount(checkNotNull(source, "target").length, offset, count, channels);
		int sourcePosition = offset;
		int sourceEndPosition = offset + count *  channels;
		
		synchronized (lock) {
			while (sourcePosition < sourceEndPosition) {
				while (position < buffer.length && sourcePosition < sourceEndPosition) {
					codec.encodeFloat(source[sourcePosition++], buffer, position);
					position += bytesPerSample;
				}
				if (position == buffer.length) {
					unsafeFlush();
				}
			}
		}
		return count;
	}
	
	/**
	 * Flushes al serialized samples in the buffer and updates and returns the
	 * totalFrame count obtained with {@link #getTotalFrames()}.
	 * 
	 * @throws IOException
	 */
	public long flush() throws IOException {
		synchronized (lock) {
			return unsafeFlush();
		}
	}

	public int getChannels() {
		return channels;
	}
	
	public SampleCodec getCodec() {
		return codec;
	}

	private long unsafeFlush() throws IOException {
		if (position == 0) {
			log.debug("Flusing(nothing to flush)");
			return framePosition.get();
		}
		int flushCount = position;
		position = 0;
		output.write(buffer, 0, flushCount);
		output.flush();
		long result = framePosition.addAndGet(flushCount / bytesPerFrame);
		return result;
	}
}
