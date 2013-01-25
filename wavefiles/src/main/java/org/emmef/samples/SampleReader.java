package org.emmef.samples;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class SampleReader {
	
	public static int read8(InputStream stream) throws IOException {
		return (byte)readByte(stream); // sign extend when converted back to int
	}

	public static int read8Unsigned(InputStream stream) throws IOException {
		return readByte(stream);
	}
	
	public static int read16LittleEndian(InputStream stream) throws IOException {
		return readByte(stream) | readByte(stream) << 24 >> 16;
	}

	public static int read16BigEndian(InputStream stream) throws IOException {
		return readByte(stream) << 24 >> 16 | readByte(stream);
	}
	
	public static int read24LittleEndian(InputStream stream) throws IOException {
		return readByte(stream) | readByte(stream) << 8 | readByte(stream) << 24 >> 8;
	}

	public static int read24BigEndian(InputStream stream) throws IOException {
		return readByte(stream) << 24 >> 8 | readByte(stream) << 8 | readByte(stream);
	}
	
	public static int read32LittleEndian(InputStream stream) throws IOException {
		return readByte(stream) | readByte(stream) << 8 | readByte(stream) << 16 | readByte(stream) << 24;
	}
	
	public static int read32BigEndian(InputStream stream) throws IOException {
		return readByte(stream) << 24 | readByte(stream) << 16 | readByte(stream) << 8 | readByte(stream);
	}
	
	public static float readFloatLittleEndian(InputStream stream) throws IOException {
		return Float.intBitsToFloat(read32LittleEndian(stream));
	}
	
	public static float readFloatBigEndian(InputStream stream) throws IOException {
		return Float.intBitsToFloat(read32BigEndian(stream));
	}
	
	public static long read64LittleEndian(InputStream stream) throws IOException {
		return
				readByte(stream) |
				readByte(stream) << 8 |
				readByte(stream) << 16 |
				readByte(stream) << 24 |
				readByte(stream) << 32 |
				readByte(stream) << 40 |
				readByte(stream) << 48 |
				readByte(stream) << 56;
	}
	
	public static long read64BigEndian(InputStream stream) throws IOException {
		return
				readByte(stream) << 56 |
				readByte(stream) << 48 |
				readByte(stream) << 40 |
				readByte(stream) << 32 |
				readByte(stream) << 24 |
				readByte(stream) << 16 |
				readByte(stream) << 8 |
				readByte(stream);
	}
	
	public static double readDoubleLittleEndian(InputStream stream) throws IOException {
		return Double.longBitsToDouble(read64LittleEndian(stream));
	}
	
	public static double readDoubleBigEndian(InputStream stream) throws IOException {
		return Double.longBitsToDouble(read64BigEndian(stream));
	}
	
	public static int read8(byte[] buffer, int offset) {
		return buffer[offset]; // sign extend when converted back to int
	}

	public static int read8Unsigned(byte[] buffer, int offset) {
		return 0xff & buffer[offset];
	}
	
	public static int read16LittleEndian(byte[] buffer, int offset) {
		return buffer[offset] | buffer[offset + 1] << 24 >> 16;
	}

	public static int read16BigEndian(byte[] buffer, int offset) {
		return buffer[offset] << 24 >> 16 | buffer[offset + 1];
	}
	
	public static int read24LittleEndian(byte[] buffer, int offset) {
		return buffer[offset] | buffer[offset + 1] << 8 | buffer[offset + 2] << 24 >> 8;
	}

	public static int read24BigEndian(byte[] buffer, int offset) {
		return buffer[offset] << 24 >> 8 | buffer[offset + 1] << 8 | buffer[offset + 2];
	}
	
	public static int read32LittleEndian(byte[] buffer, int offset) {
		return buffer[offset] | buffer[offset + 1] << 8 | buffer[offset + 2] << 16 | buffer[offset + 3] << 24;
	}
	
	public static int read32BigEndian(byte[] buffer, int offset) {
		return buffer[offset] << 24 | buffer[offset + 1] << 16 | buffer[offset + 2] << 8 | buffer[offset + 3];
	}
	
	public static float readFloatLittleEndian(byte[] buffer, int offset) {
		return Float.intBitsToFloat(read32LittleEndian(buffer, offset));
	}
	
	public static float readFloatBigEndian(byte[] buffer, int offset) {
		return Float.intBitsToFloat(read32BigEndian(buffer, offset));
	}
	
	public static long read64LittleEndian(byte[] buffer, int offset) {
		int position = offset;
		return
				buffer[position++] |
				buffer[position++] << 8 |
				buffer[position++] << 16 |
				buffer[position++] << 24 |
				buffer[position++] << 32 |
				buffer[position++] << 40 |
				buffer[position++] << 48 |
				buffer[position] << 56;
	}

	public static long read64BigEndian(byte[] buffer, int offset) {
		int position = offset;
		return
				buffer[position++] << 56 |
				buffer[position++] << 48 |
				buffer[position++] << 40 |
				buffer[position++] << 32 |
				buffer[position++] << 24 |
				buffer[position++] << 16 |
				buffer[position++] << 8 |
				buffer[position];
	}
	
	public static double readDoubleLittleEndian(byte[] buffer, int offset) {
		return Double.longBitsToDouble(read64LittleEndian(buffer, offset));
	}
	
	public static double readDoubleBigEndian(byte[] buffer, int offset) {
		return Double.longBitsToDouble(read64BigEndian(buffer, offset));
	}
	
	public static int ensureBufferOffsetAndCount(byte[] buffer, int offset, int count) {
		if (hasRemaining(buffer, offset, count)) {
			return offset;
		}
		throw new IllegalArgumentException("Offset (" + offset + ") + count(" + count + ") larger that buffer length (" + buffer.length + ")");
	}
	
	public static boolean hasRemaining(byte[] buffer, int offset, int count) {
		return offset + count <= buffer.length;
	}
	
	public static int readByte(InputStream stream) throws IOException {
		int read = stream.read();
		if (read < 0) {
			throw new EOFException();
		}
		return read;
	}
}
