package org.emmef.fileformat.riff;

public class RiffDataChunk extends RiffChunk {
	private final byte[] buffer;

	public RiffDataChunk(RiffChunk source, byte[] buffer) {
		super(source);
		if (RiffUtils.checkNotNull(buffer, "buffer").length != source.getContentLength()) {
			throw new IllegalArgumentException("Length of buffer (" + buffer.length + ") must equal content length: " + source.getContentLength());
		}
		this.buffer = buffer;
	}
	
	protected final byte[] getBuffer() {
		return buffer;
	}
	
	public final int getByteAt(int index) {
		return getByteAt(index, buffer);
	}
	
	public final int getWordAt(int index) {
		return getWordAt(index, buffer);
	}
	
	public final long getDWordAt(int index) {
		return getDWordAt(index, buffer);
	}
	
	public static final int getByteAt(int index, byte[] buffer) {
		return buffer[index];
	}
	
	public static final int getWordAt(int index, byte[] buffer) {
		return (0xff & buffer[index]) | ((0xff & buffer[index + 1]) << 8);
	}
	
	public static final long getDWordAt(int index, byte[] buffer) {
		long result = 0xffL & buffer[index + 3];
		result <<= 8;
		result |= 0xff & buffer[index + 2];
		result <<= 8;
		result |= 0xff & buffer[index + 1];
		result <<= 8;
		result |= 0xff & buffer[index];
		return result;
	}
	
}
