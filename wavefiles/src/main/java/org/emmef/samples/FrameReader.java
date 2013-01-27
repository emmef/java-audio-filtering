package org.emmef.samples;

import java.io.IOException;
import java.io.InputStream;

import org.emmef.fileformat.riff.RiffUtils;
import org.emmef.utils.LimitedInputStream;

public class FrameReader {
	private final int channels;
	private final byte[] buffer;
	private final InputStream source;
	private final SampleDecoder decoder;
	private final int bytesPerFrame;
	private final int bytesPerSample;
	private int position;
	private int limit;

	public FrameReader(int channels, int bufferSize, InputStream source, long maxFrames, SampleDecoder decoder) {
		if (channels < 1) {
			throw new IllegalArgumentException("Number of channels must be at least 1");
		}
		this.channels = channels;
		bytesPerSample = decoder.bytesPerSample();
		bytesPerFrame = channels * bytesPerSample;
		buffer = new byte[Math.max((bufferSize + bytesPerFrame - 1) / bytesPerFrame, 1) * bytesPerFrame];
		this.source = new LimitedInputStream(RiffUtils.checkNotNull(source, "source"), maxFrames * bytesPerFrame);
		this.decoder = RiffUtils.checkNotNull(decoder, "Sample decoder");
	}
	
	public long readFrame(double[] target, int offset, int count) throws IOException {
		int endOffset = offset + channels * count;
		
		RiffUtils.checkBufferOffsetAndLength(target.length, endOffset, count);
		
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
	
	public long readFrame(float[] target, int offset, int count) throws IOException {
		int endOffset = offset + channels * count;
		
		RiffUtils.checkBufferOffsetAndLength(target.length, endOffset, count);
		
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
}
