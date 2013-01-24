package org.emmef.samples;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.emmef.fileformat.riff.RiffUtils;

public class FrameReader {
	private final int channels;
	private final ByteBuffer buffer;
	private final FileChannel source;
	private final SampleDecoder reader;
	private final int frameBytes;

	public FrameReader(int channels, int bufferSize, FileChannel source, SampleDecoder reader) {
		if (channels < 1) {
			throw new IllegalArgumentException("Number of channels must be at least 1");
		}
		this.channels = channels;
		buffer = ByteBuffer.allocate(Math.max(channels * reader.bytesPerSample() * 2, bufferSize));
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		this.source = RiffUtils.checkNotNull(source, "source");
		this.reader = RiffUtils.checkNotNull(reader, "reader");
		frameBytes = channels * reader.bytesPerSample();
	}
	
	public long readFrame(double[] target, int offset, int count) throws IOException {
		int endOffset = offset + channels * count;
		
		if (endOffset > target.length) {
			throw new IllegalArgumentException("Attempt to read past end of destination buffer: length=" + target.length + "; offset=" + offset + "; count=" + count + "; channels=" + channels + "; end-offset = " + endOffset);
		}
		int actualCount = 0;
		int position = offset;
		while (actualCount < count) {
			int remaining = buffer.remaining();
			if (remaining < frameBytes) {
				int reads = source.read(buffer);
				if (reads == -1) {
					return actualCount;
				}
				remaining = buffer.remaining();
				if (remaining < frameBytes) {
					return actualCount;
				}
			}
			for (int i = 0; i < channels; i++) {
				target[position++] = reader.decodeDouble(buffer);
			}
			
			actualCount++;
		}
		return actualCount;
	}
	
	public long readFrame(float[] target, int offset, int count) throws IOException {
		int endOffset = offset + channels * count;
		
		if (endOffset > target.length) {
			throw new IllegalArgumentException("Attempt to read past end of destination buffer: length=" + target.length + "; offset=" + offset + "; count=" + count + "; channels=" + channels + "; end-offset = " + endOffset);
		}
		int actualCount = 0;
		int position = offset;
		while (actualCount < count) {
			int remaining = buffer.remaining();
			if (remaining < frameBytes) {
				int reads = source.read(buffer);
				if (reads == -1) {
					return actualCount;
				}
				remaining = buffer.remaining();
				if (remaining < frameBytes) {
					return actualCount;
				}
			}
			for (int i = 0; i < channels; i++) {
				target[position++] = reader.decodeFloat(buffer);
			}
			
			actualCount++;
		}
		return actualCount;
	}
	
}
