package org.emmef.samples.serialization;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.emmef.utils.Permutations;
import org.junit.Test;

public class SerializationTest {
	private static final String HEXDIGITS = "0123456789abcdef";
	
	@Test
	public void testReadWrite8Bits() throws IOException {
		System.out.println("Testing 8 bit samples");

		ByteInputStream iStream = new ByteInputStream(1);
		ByteOutpuStream oStream = new ByteOutpuStream(1);
		
		for (int byteValue = -128; byteValue < 128; byteValue++) {
			String digits = String.format("%02x", 0xff & byteValue);
			System.out.println("- byte 0x" + digits);
			DeserializeScenario codecTest = new DeserializeScenario(digits);
			
			/*
			 * Verify deserialized output for a stream
			 */
			iStream.reset(Collections.singletonList(Byte.valueOf((byte)byteValue)));
			int read = Deserialize.read8(iStream);
			assertEquals(codecTest.getBigEndian(), Integer.toHexString(read));
			
			/*
			 * Verify serialized output into a stream
			 * by comparing the data in the output stream with the
			 * original input stream
			 */
			oStream.reset();
			Serialize.write08(read, oStream);
			assertArrayEquals(iStream.getData(), oStream.getData());
			
			/*
			 * Verify deserialized output for a buffer
			 */
			read = Deserialize.read8(iStream.getData(), 0);
			assertEquals(codecTest.getBigEndian(), Integer.toHexString(read));
			
			/*
			 * Verify serialized output into a buffer
			 * by comparing the data in the output buffer with the
			 * original input buffer
			 */
			oStream.reset();
			Serialize.write08(read, oStream.getData(), 0);
			assertArrayEquals(iStream.getData(), oStream.getData());
		}
	}
	
	@Test
	public void testReadWrite16Bits() throws IOException {
		System.out.println("Testing 16 bit samples");
		
		ByteInputStream iStream = new ByteInputStream(2);
		ByteOutpuStream oStream = new ByteOutpuStream(2);
		List<Byte> input = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		for (int byteValue = -128; byteValue < 128; byteValue++) {
			setInputData(input, byteValue, 2);
			
			for (List<Byte> permutation : Permutations.createObjectPermutations(input)) {
				String digits = createDigits(permutation, builder);
				DeserializeScenario codecTest = new DeserializeScenario(digits);
				
				/*
				 * Verify little-endian deserialized output for a stream
				 */
				iStream.reset(permutation);
				int readLittleEndian = Deserialize.read16LittleEndian(iStream);
				assertEquals(codecTest.getLittleEndian(), Integer.toHexString(readLittleEndian));
				
				/*
				 * Verify little-endian serialized output into a stream
				 * by comparing the data in the output stream with the
				 * original input stream
				 */
				oStream.reset();
				Serialize.write16LittleEndian(readLittleEndian, oStream);
				assertArrayEquals(iStream.getData(), oStream.getData());
				
				/*
				 * Verify big-endian deserialized output for a stream
				 */
				iStream.reset(permutation);
				int readBigEndian = Deserialize.read16BigEndian(iStream);
				assertEquals(codecTest.getBigEndian(), Integer.toHexString(readBigEndian));
				
				/*
				 * Verify big-endian serialized output into a stream
				 * by comparing the data in the output stream with the
				 * original input stream
				 */
				oStream.reset();
				Serialize.write16BigEndian(readBigEndian, oStream);
				assertArrayEquals(iStream.getData(), oStream.getData());

				/*
				 * Verify little-endian deserialized output for a buffer
				 */
				readLittleEndian = Deserialize.read16LittleEndian(iStream.getData(), 0);
				assertEquals(codecTest.getLittleEndian(), Integer.toHexString(readLittleEndian));
				
				/*
				 * Verify little-endian serialized output into a buffer
				 * by comparing the data in the output buffer with the
				 * original input buffer
				 */
				oStream.reset();
				Serialize.write16LittleEndian(readLittleEndian, oStream.getData(), 0);
				assertArrayEquals(iStream.getData(), oStream.getData());

				/*
				 * Verify big-endian deserialized output for a buffer
				 */
				readBigEndian = Deserialize.read16BigEndian(iStream.getData(), 0);
				assertEquals(codecTest.getBigEndian(), Integer.toHexString(readBigEndian));
				
				/*
				 * Verify big-endian serialized output into a buffer
				 * by comparing the data in the output buffer with the
				 * original input buffer
				 */
				oStream.reset();
				Serialize.write16BigEndian(readBigEndian, oStream.getData(), 0);
				assertArrayEquals(iStream.getData(), oStream.getData());
			}
		}
	}
	
	@Test
	public void testReadWrite24Bits() throws IOException {
		ByteInputStream iStream = new ByteInputStream(3);
		ByteOutpuStream oStream = new ByteOutpuStream(3);
		List<Byte> input = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		for (int byteValue = -128; byteValue < 128; byteValue++) {
			setInputData(input, byteValue, 3);
			for (List<Byte> permutation : Permutations.createObjectPermutations(input)) {
				String digits = createDigits(permutation, builder);
				DeserializeScenario codecTest = new DeserializeScenario(digits);
				
				/*
				 * Verify little-endian deserialized output for a stream
				 */
				iStream.reset(permutation);
				int readLittleEndian = Deserialize.read24LittleEndian(iStream);
				assertEquals(codecTest.getLittleEndian(), Integer.toHexString(readLittleEndian));
				
				/*
				 * Verify little-endian serialized output into a stream
				 * by comparing the data in the output stream with the
				 * original input stream
				 */
				oStream.reset();
				Serialize.write24LittleEndian(readLittleEndian, oStream);
				assertArrayEquals(iStream.getData(), oStream.getData());
				
				/*
				 * Verify big-endian deserialized output for a stream
				 */
				iStream.reset(permutation);
				int readBigEndian = Deserialize.read24BigEndian(iStream);
				assertEquals(codecTest.getBigEndian(), Integer.toHexString(readBigEndian));
				
				/*
				 * Verify big-endian serialized output into a stream
				 * by comparing the data in the output stream with the
				 * original input stream
				 */
				oStream.reset();
				Serialize.write24BigEndian(readBigEndian, oStream);
				assertArrayEquals(iStream.getData(), oStream.getData());

				/*
				 * Verify little-endian deserialized output for a buffer
				 */
				readLittleEndian = Deserialize.read24LittleEndian(iStream.getData(), 0);
				assertEquals(codecTest.getLittleEndian(), Integer.toHexString(readLittleEndian));
				
				/*
				 * Verify little-endian serialized output into a buffer
				 * by comparing the data in the output buffer with the
				 * original input buffer
				 */
				oStream.reset();
				Serialize.write24LittleEndian(readLittleEndian, oStream.getData(), 0);
				assertArrayEquals(iStream.getData(), oStream.getData());

				/*
				 * Verify big-endian deserialized output for a buffer
				 */
				readBigEndian = Deserialize.read24BigEndian(iStream.getData(), 0);
				assertEquals(codecTest.getBigEndian(), Integer.toHexString(readBigEndian));
				
				/*
				 * Verify big-endian serialized output into a buffer
				 * by comparing the data in the output buffer with the
				 * original input buffer
				 */
				oStream.reset();
				Serialize.write24BigEndian(readBigEndian, oStream.getData(), 0);
				assertArrayEquals(iStream.getData(), oStream.getData());
			}
		}
	}
	
	@Test
	public void testReadWrite32Bits() throws IOException {
		ByteInputStream iStream = new ByteInputStream(4);
		ByteOutpuStream oStream = new ByteOutpuStream(4);
		List<Byte> input = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		for (int byteValue = -128; byteValue < 128; byteValue++) {
			setInputData(input, byteValue, 4);
			for (List<Byte> permutation : Permutations.createObjectPermutations(input)) {
				String digits = createDigits(permutation, builder);
				DeserializeScenario codecTest = new DeserializeScenario(digits);
				
				/*
				 * Verify little-endian deserialized output for a stream
				 */
				iStream.reset(permutation);
				int readLittleEndian = Deserialize.read32LittleEndian(iStream);
				assertEquals(codecTest.getLittleEndian(), Integer.toHexString(readLittleEndian));
				
				/*
				 * Verify little-endian serialized output into a stream
				 * by comparing the data in the output stream with the
				 * original input stream
				 */
				oStream.reset();
				Serialize.write32LittleEndian(readLittleEndian, oStream);
				assertArrayEquals(iStream.getData(), oStream.getData());
				
				/*
				 * Verify big-endian deserialized output for a stream
				 */
				iStream.reset(permutation);
				int readBigEndian = Deserialize.read32BigEndian(iStream);
				assertEquals(codecTest.getBigEndian(), Integer.toHexString(readBigEndian));
				
				/*
				 * Verify big-endian serialized output into a stream
				 * by comparing the data in the output stream with the
				 * original input stream
				 */
				oStream.reset();
				Serialize.write32BigEndian(readBigEndian, oStream);
				assertArrayEquals(iStream.getData(), oStream.getData());

				/*
				 * Verify little-endian deserialized output for a buffer
				 */
				readLittleEndian = Deserialize.read32LittleEndian(iStream.getData(), 0);
				assertEquals(codecTest.getLittleEndian(), Integer.toHexString(readLittleEndian));
				
				/*
				 * Verify little-endian serialized output into a buffer
				 * by comparing the data in the output buffer with the
				 * original input buffer
				 */
				oStream.reset();
				Serialize.write32LittleEndian(readLittleEndian, oStream.getData(), 0);
				assertArrayEquals(iStream.getData(), oStream.getData());

				/*
				 * Verify big-endian deserialized output for a buffer
				 */
				readBigEndian = Deserialize.read32BigEndian(iStream.getData(), 0);
				assertEquals(codecTest.getBigEndian(), Integer.toHexString(readBigEndian));
				
				/*
				 * Verify big-endian serialized output into a buffer
				 * by comparing the data in the output buffer with the
				 * original input buffer
				 */
				oStream.reset();
				Serialize.write32BigEndian(readBigEndian, oStream.getData(), 0);
				assertArrayEquals(iStream.getData(), oStream.getData());
			}
		}
	}
	
	@Test
	public void testReadWrite64Bits() throws IOException {
		ByteInputStream iStream = new ByteInputStream(8);
		ByteOutpuStream oStream = new ByteOutpuStream(8);
		List<Byte> input = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		for (int byteValue = -128; byteValue < 128; byteValue++) {
			setInputData(input, byteValue, 8);
			for (List<Byte> permutation : Permutations.createObjectPermutations(input)) {
				String digits = createDigits(permutation, builder);
				DeserializeScenario codecTest = new DeserializeScenario(digits);
				
				/*
				 * Verify little-endian deserialized output for a stream
				 */
				iStream.reset(permutation);
				long readLittleEndian = Deserialize.read64LittleEndian(iStream);
				assertEquals(codecTest.getLittleEndian(), Long.toHexString(readLittleEndian));
				
				/*
				 * Verify little-endian serialized output into a stream
				 * by comparing the data in the output stream with the
				 * original input stream
				 */
				oStream.reset();
				Serialize.write64LittleEndian(readLittleEndian, oStream);
				assertArrayEquals(iStream.getData(), oStream.getData());
				
				/*
				 * Verify big-endian deserialized output for a stream
				 */
				iStream.reset(permutation);
				long readBigEndian = Deserialize.read64BigEndian(iStream);
				assertEquals(codecTest.getBigEndian(), Long.toHexString(readBigEndian));
				
				/*
				 * Verify big-endian serialized output into a stream
				 * by comparing the data in the output stream with the
				 * original input stream
				 */
				oStream.reset();
				Serialize.write64BigEndian(readBigEndian, oStream);
				assertArrayEquals(iStream.getData(), oStream.getData());

				/*
				 * Verify little-endian deserialized output for a buffer
				 */
				readLittleEndian = Deserialize.read64LittleEndian(iStream.getData(), 0);
				assertEquals(codecTest.getLittleEndian(), Long.toHexString(readLittleEndian));
				
				/*
				 * Verify little-endian serialized output into a buffer
				 * by comparing the data in the output buffer with the
				 * original input buffer
				 */
				oStream.reset();
				Serialize.write64LittleEndian(readLittleEndian, oStream.getData(), 0);
				assertArrayEquals(iStream.getData(), oStream.getData());

				/*
				 * Verify big-endian deserialized output for a buffer
				 */
				readBigEndian = Deserialize.read64BigEndian(iStream.getData(), 0);
				assertEquals(codecTest.getBigEndian(), Long.toHexString(readBigEndian));
				
				/*
				 * Verify big-endian serialized output into a buffer
				 * by comparing the data in the output buffer with the
				 * original input buffer
				 */
				oStream.reset();
				Serialize.write64BigEndian(readBigEndian, oStream.getData(), 0);
				assertArrayEquals(iStream.getData(), oStream.getData());
			}
		}
	}

	private String createDigits(List<Byte> input, StringBuilder builder) {
		builder.setLength(0);
		for (int i = 0; i < input.size(); i++) {
			byte byteValue = input.get(i);
			builder.append(HEXDIGITS.charAt((byteValue & 0xf0) >> 4));
			builder.append(HEXDIGITS.charAt(byteValue & 0xf));
		}
		
		return builder.toString();
	}
	
	static void setInputData(List<Byte> list, int byteValue, int count)  {
		list.clear();
		list.add((byte)byteValue);
		System.out.printf("Test %d-bit sequence %02x", 8 * count, 0xff & byteValue);
		for (int i = 1; i < count; i++) {
			list.add(Byte.valueOf((byte)i));
			System.out.printf(" %02x", 0xff & i);
		}
		System.out.println();
	}
	
	private static final class ByteInputStream extends InputStream {
		private final byte[] data;
		private int position;

		public ByteInputStream(int length) {
			data = new byte[length];
			position = 0;
		}
		
		public void reset(List<Byte> inputData) {
			for (int i = 0; i < data.length; i++) {
				data[i] = inputData.get(i);
			}
			position = 0;
		}
		
		public byte[] getData() {
			return data;
		}

		@Override
		public int read() throws IOException {
			if (position < data.length) {
				return 0xff & data[position++];
			}
			return -1;
		}
	}
	
	private static final class ByteOutpuStream extends OutputStream {
		private final byte[] data;
		private int position;
		
		public ByteOutpuStream(int length) {
			data = new byte[length];
			position = 0;
		}
		
		public void reset() {
			position = 0;
			for (int i = 0; i < data.length; i++) {
				data[i] = 0;
			}
		}
		
		public byte[] getData() {
			return data;
		}
		
		@Override
		public void write(int b) throws IOException {
			if (position < data.length) {
				data[position++] = (byte)b;
				return;
			}
			throw new IOException();
		}
	}
}
