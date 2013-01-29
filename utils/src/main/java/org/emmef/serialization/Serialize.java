package org.emmef.serialization;

import java.io.IOException;
import java.io.OutputStream;

public class Serialize {
	public static void write08(int value, byte[] buffer, int offset) {
		buffer[offset] = (byte)value;
	}
	
	public static void write08(int value, OutputStream stream) throws IOException  {
		stream.write(value);
	}
	
	public static void write16BigEndian(int value, byte[] buffer, int offset) {
		buffer[offset] = (byte)(value >> 8);
		buffer[offset + 1] = (byte)value;
	}
	
	public static void write16BigEndian(int value, OutputStream stream) throws IOException  {
		stream.write(value >> 8);
		stream.write(value);
	}
	
	public static void write16LittleEndian(int value, byte[] buffer, int offset) {
		buffer[offset] = (byte)value;
		buffer[offset + 1] = (byte)(value >> 8);
	}
	
	public static void write16LittleEndian(int value, OutputStream stream) throws IOException  {
		stream.write(value);
		stream.write(value >> 8);
	}
	
	public static void write24BigEndian(int value, byte[] buffer, int offset) {
		buffer[offset] = (byte)(value >> 16);
		buffer[offset + 1] = (byte)(value >> 8);
		buffer[offset + 2] = (byte)value;
	}
	
	public static void write24BigEndian(int value, OutputStream stream) throws IOException  {
		stream.write(value >> 16);
		stream.write(value >> 8);
		stream.write(value);
	}
	
	public static void write24LittleEndian(int value, byte[] buffer, int offset) {
		buffer[offset] = (byte)value;
		buffer[offset + 1] = (byte)(value >> 8);
		buffer[offset + 2] = (byte)(value >> 16);
	}
	
	public static void write24LittleEndian(int value, OutputStream stream) throws IOException  {
		stream.write(value);
		stream.write(value >> 8);
		stream.write(value >> 16);
	}

	public static void write32BigEndian(int value, byte[] buffer, int offset) {
		buffer[offset] = (byte)(value >> 24);
		buffer[offset + 1] = (byte)(value >> 16);
		buffer[offset + 2] = (byte)(value >> 8);
		buffer[offset + 3] = (byte)value;
	}
	
	public static void write32BigEndian(int value, OutputStream stream) throws IOException  {
		stream.write(value >> 24);
		stream.write(value >> 16);
		stream.write(value >> 8);
		stream.write(value);
	}

	public static void write32LittleEndian(int value, byte[] buffer, int offset) {
		buffer[offset] = (byte)value;
		buffer[offset + 1] = (byte)(value >> 8);
		buffer[offset + 2] = (byte)(value >> 16);
		buffer[offset + 3] = (byte)(value >> 24);
	}
	
	public static void write32LittleEndian(int value, OutputStream stream) throws IOException  {
		stream.write(value);
		stream.write(value >> 8);
		stream.write(value >> 16);
		stream.write(value >> 24);
	}
	
	public static void write64BigEndian(long value, byte[] buffer, int offset) {
		buffer[offset] = (byte)(value >> 56);
		buffer[offset + 1] = (byte)(value >> 48);
		buffer[offset + 2] = (byte)(value >> 40);
		buffer[offset + 3] = (byte)(value >> 32);
		buffer[offset + 4] = (byte)(value >> 24);
		buffer[offset + 5] = (byte)(value >> 16);
		buffer[offset + 6] = (byte)(value >> 8);
		buffer[offset + 7] = (byte)value;
	}
	
	public static void write64BigEndian(long value, OutputStream stream) throws IOException  {
		writeLongLsb(value >> 56, stream);
		writeLongLsb(value >> 48, stream);
		writeLongLsb(value >> 40, stream);
		writeLongLsb(value >> 32, stream);
		writeLongLsb(value >> 24, stream);
		writeLongLsb(value >> 16, stream);
		writeLongLsb(value >> 8, stream);
		writeLongLsb(value, stream);
	}
	
	public static void write64LittleEndian(long value, byte[] buffer, int offset) {
		buffer[offset] = (byte)value;
		buffer[offset + 1] = (byte)(value >> 8);
		buffer[offset + 2] = (byte)(value >> 16);
		buffer[offset + 3] = (byte)(value >> 24);
		buffer[offset + 4] = (byte)(value >> 32);
		buffer[offset + 5] = (byte)(value >> 40);
		buffer[offset + 6] = (byte)(value >> 48);
		buffer[offset + 7] = (byte)(value >> 56);
	}
	
	public static void write64LittleEndian(long value, OutputStream stream) throws IOException  {
		writeLongLsb(value, stream);
		writeLongLsb(value >> 8, stream);
		writeLongLsb(value >> 16, stream);
		writeLongLsb(value >> 24, stream);
		writeLongLsb(value >> 32, stream);
		writeLongLsb(value >> 40, stream);
		writeLongLsb(value >> 48, stream);
		writeLongLsb(value >> 56, stream);
	}

	public static void writeDoubleBigEndian(double value, byte[] buffer, int offset) {
		write64BigEndian(Double.doubleToRawLongBits(value), buffer, offset);
	}
	
	public static void writeDoubleBigEndian(double value, OutputStream stream) throws IOException {
		write64BigEndian(Double.doubleToRawLongBits(value), stream);
	}
	
	public static void writeDoubleLittleEndian(double value, byte[] buffer, int offset) {
		write64LittleEndian(Double.doubleToRawLongBits(value), buffer, offset);
	}
	
	public static void writeDoubleLittleEndian(double value, OutputStream stream) throws IOException {
		write64LittleEndian(Double.doubleToRawLongBits(value), stream);
	}
	
	public static void writeFloatBigEndian(float value, byte[] buffer, int offset) {
		write32BigEndian(Float.floatToRawIntBits(value), buffer, offset);
	}
	
	public static void writeFloatBigEndian(float value, OutputStream stream) throws IOException  {
		write32BigEndian(Float.floatToRawIntBits(value), stream);
	}
	
	public static void writeFloatLittleEndian(float value, byte[] buffer, int offset) {
		write32LittleEndian(Float.floatToRawIntBits(value), buffer, offset);
	}
	
	public static void writeFloatLittleEndian(int floatValue, OutputStream stream) throws IOException  {
		write16LittleEndian(Float.floatToRawIntBits(floatValue), stream);
	}

	public static void writeLongLsb(long value, OutputStream stream) throws IOException {
		stream.write((int)value);
	}
}
