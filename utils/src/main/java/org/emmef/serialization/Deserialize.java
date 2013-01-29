package org.emmef.serialization;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class Deserialize {
	
	public static int read8(InputStream stream) throws IOException {
		return (byte)readByteAsInt(stream); // sign extend when converted back to int
	}
	
	public static int read16LittleEndian(InputStream stream) throws IOException {
		return readByteAsInt(stream) | readByteAsInt(stream) << 24 >> 16;
	}

	public static int read16BigEndian(InputStream stream) throws IOException {
		return readByteAsInt(stream) << 24 >> 16 | readByteAsInt(stream);
	}
	
	public static int read24LittleEndian(InputStream stream) throws IOException {
		return readByteAsInt(stream) | readByteAsInt(stream) << 8 | readByteAsInt(stream) << 24 >> 8;
	}

	public static int read24BigEndian(InputStream stream) throws IOException {
		return readByteAsInt(stream) << 24 >> 8 | readByteAsInt(stream) << 8 | readByteAsInt(stream);
	}
	
	public static int read32LittleEndian(InputStream stream) throws IOException {
		return readByteAsInt(stream) | readByteAsInt(stream) << 8 | readByteAsInt(stream) << 16 | readByteAsInt(stream) << 24;
	}
	
	public static int read32BigEndian(InputStream stream) throws IOException {
		return readByteAsInt(stream) << 24 | readByteAsInt(stream) << 16 | readByteAsInt(stream) << 8 | readByteAsInt(stream);
	}
	
	public static float readFloatLittleEndian(InputStream stream) throws IOException {
		return Float.intBitsToFloat(read32LittleEndian(stream));
	}
	
	public static float readFloatBigEndian(InputStream stream) throws IOException {
		return Float.intBitsToFloat(read32BigEndian(stream));
	}
	
	public static long read64LittleEndian(InputStream stream) throws IOException {
		return
				readByteAsLong(stream) |
				readByteAsLong(stream) << 8 |
				readByteAsLong(stream) << 16 |
				readByteAsLong(stream) << 24 |
				readByteAsLong(stream) << 32 |
				readByteAsLong(stream) << 40 |
				readByteAsLong(stream) << 48 |
				readByteAsLong(stream) << 56;
	}
	
	public static long read64BigEndian(InputStream stream) throws IOException {
		return
				readByteAsLong(stream) << 56 |
				readByteAsLong(stream) << 48 |
				readByteAsLong(stream) << 40 |
				readByteAsLong(stream) << 32 |
				readByteAsLong(stream) << 24 |
				readByteAsLong(stream) << 16 |
				readByteAsLong(stream) << 8 |
				readByteAsLong(stream);
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
	
	public static int read16LittleEndian(byte[] buffer, int offset) {
		return 0xff & buffer[offset] | buffer[offset + 1] << 24 >> 16;
	}

	public static int read16BigEndian(byte[] buffer, int offset) {
		return buffer[offset] << 24 >> 16 | 0xff & buffer[offset + 1];
	}
	
	public static int read24LittleEndian(byte[] buffer, int offset) {
		return 0xff & buffer[offset] | (0xff & buffer[offset + 1]) << 8 | buffer[offset + 2] << 24 >> 8;
	}

	public static int read24BigEndian(byte[] buffer, int offset) {
		return buffer[offset] << 24 >> 8 | (0xff & buffer[offset + 1]) << 8 | 0xff & buffer[offset + 2];
	}
	
	public static int read32LittleEndian(byte[] buffer, int offset) {
		return 0xff & buffer[offset] | (0xff & buffer[offset + 1]) << 8 | (0xff & buffer[offset + 2]) << 16 | buffer[offset + 3] << 24;
	}
	
	public static int read32BigEndian(byte[] buffer, int offset) {
		return buffer[offset] << 24 | (0xff & buffer[offset + 1]) << 16 | (0xff & buffer[offset + 2]) << 8 | 0xff & buffer[offset + 3];
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
				0xffL & buffer[position++] |
				(0xffL & buffer[position++]) << 8 |
				(0xffL & buffer[position++]) << 16 |
				(0xffL & buffer[position++]) << 24 |
				(0xffL & buffer[position++]) << 32 |
				(0xffL & buffer[position++]) << 40 |
				(0xffL & buffer[position++]) << 48 |
				(0xffL & buffer[position]) << 56;
	}

	public static long read64BigEndian(byte[] buffer, int offset) {
		int position = offset;
		return
				(0xffL & buffer[position++]) << 56 |
				(0xffL & buffer[position++]) << 48 |
				(0xffL & buffer[position++]) << 40 |
				(0xffL & buffer[position++]) << 32 |
				(0xffL & buffer[position++]) << 24 |
				(0xffL & buffer[position++]) << 16 |
				(0xffL & buffer[position++]) << 8 |
				0xffL & buffer[position++];
	}
	
	public static double readDoubleLittleEndian(byte[] buffer, int offset) {
		return Double.longBitsToDouble(read64LittleEndian(buffer, offset));
	}
	
	public static double readDoubleBigEndian(byte[] buffer, int offset) {
		return Double.longBitsToDouble(read64BigEndian(buffer, offset));
	}
	
	public static void checkRemaining(byte[] buffer, int offset, int count) {
		checkRemaining(buffer.length, offset, count);
	}
	
	public static void checkRemaining(int limit, int offset, int count) {
		if (hasRemaining(limit, offset, count)) {
			return;
		}
		throw new IllegalArgumentException("Offset (" + offset + ") + count(" + count + ") larger than limit (" + limit + ")");
	}
	
	public static boolean hasRemaining(int limit, int offset, int count) {
		return offset + count <= limit;
	}
	
	public static int readByteAsInt(InputStream stream) throws IOException {
		int read = stream.read();
		if (read < 0) {
			throw new EOFException();
		}
		return read;
	}
	
	public static long readByteAsLong(InputStream stream) throws IOException {
		int read = stream.read();
		if (read < 0) {
			throw new EOFException();
		}
		return read;
	}
}
