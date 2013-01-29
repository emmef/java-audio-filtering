package org.emmef.samples.codec;

import static org.emmef.samples.codec.SampleScales.*;

import java.nio.ByteBuffer;

import org.emmef.samples.serialization.Deserialize;
import org.emmef.samples.serialization.Serialize;

public enum SampleCodecs implements SampleCodec {
	SIGNED_8() {
		@Override
		public double decodeDouble(byte[] buffer, int offset) {
			return SCALE_8_TO_DOUBLE * Deserialize.read8(buffer, offset);
		}
		
		@Override
		public float decodeFloat(byte[] buffer, int offset) {
			return SCALE_8_TO_FLOAT * Deserialize.read8(buffer, offset);
		}
		
		@Override
		public int bytesPerSample() {
			return 1;
		}

		@Override
		public void encodeDouble(double sample, byte[] buffer, int offset) {
			Serialize.write08(toScaledByte(sample), buffer, offset);
		}

		@Override
		public void encodeFloat(float sample, byte[] buffer, int offset) {
			encodeDouble(sample, buffer, offset);
		}
	},
	UNSIGNED_8() {
		@Override
		public double decodeDouble(byte[] buffer, int offset) {
			return SCALE_8_TO_DOUBLE * (-128 + (0xff & Deserialize.read8(buffer, offset)));
		}
		
		@Override
		public float decodeFloat(byte[] buffer, int offset) {
			return SCALE_8_TO_FLOAT * (-128 + (0xff & Deserialize.read8(buffer, offset)));
		}
		
		@Override
		public int bytesPerSample() {
			return 1;
		}

		@Override
		public void encodeDouble(double sample, byte[] buffer, int offset) {
			Serialize.write08(toScaled128UpByte(sample), buffer, offset);
		}

		@Override
		public void encodeFloat(float sample, byte[] buffer, int offset) {
			encodeDouble(sample, buffer, offset);
		}
	},
	SIGNED_16() {
		@Override
		public double decodeDouble(byte[] buffer, int offset) {
			return SCALE_16_TO_DOUBLE * Deserialize.read16LittleEndian(buffer, offset);
		}

		@Override
		public float decodeFloat(byte[] buffer, int offset) {
			return SCALE_16_TO_FLOAT * Deserialize.read16LittleEndian(buffer, offset);
		}

		@Override
		public int bytesPerSample() {
			return 2;
		}

		@Override
		public void encodeDouble(double sample, byte[] buffer, int offset) {
			Serialize.write16LittleEndian(toScaledShort(sample), buffer, offset);
		}

		@Override
		public void encodeFloat(float sample, byte[] buffer, int offset) {
			encodeDouble(sample, buffer, offset);
		}
	},
	PACKED_24() {
		@Override
		public double decodeDouble(byte[] buffer, int offset) {
			return SCALE_24_TO_DOUBLE * Deserialize.read24LittleEndian(buffer, offset);
		}

		@Override
		public float decodeFloat(byte[] buffer, int offset) {
			return SCALE_24_TO_FLOAT * Deserialize.read24LittleEndian(buffer, offset);
		}

		@Override
		public int bytesPerSample() {
			return 3;
		}

		@Override
		public void encodeDouble(double sample, byte[] buffer, int offset) {
			Serialize.write24LittleEndian(toScaled24Bit(sample), buffer, offset);
		}

		@Override
		public void encodeFloat(float sample, byte[] buffer, int offset) {
			encodeDouble(sample, buffer, offset);
		}
	},
	PRE_PADDED_24() {
		@Override
		public double decodeDouble(byte[] buffer, int offset) {
			return SCALE_24_TO_DOUBLE * Deserialize.read24LittleEndian(buffer, offset + 1);
		}

		@Override
		public float decodeFloat(byte[] buffer, int offset) {
			return SCALE_24_TO_FLOAT * Deserialize.read24LittleEndian(buffer, offset + 1);
		}

		@Override
		public int bytesPerSample() {
			return 4;
		}

		@Override
		public void encodeDouble(double sample, byte[] buffer, int offset) {
			Serialize.write08(0, buffer, offset);
			Serialize.write24LittleEndian(toScaled24Bit(sample), buffer, offset + 1);
		}

		@Override
		public void encodeFloat(float sample, byte[] buffer, int offset) {
			encodeDouble(sample, buffer, offset);
		}
	},
	POST_PADDED_24() {
		@Override
		public double decodeDouble(byte[] buffer, int offset) {
			return SCALE_24_TO_DOUBLE * Deserialize.read24LittleEndian(buffer, offset);
		}

		@Override
		public float decodeFloat(byte[] buffer, int offset) {
			return SCALE_24_TO_FLOAT * Deserialize.read24LittleEndian(buffer, offset);
		}

		@Override
		public int bytesPerSample() {
			return 4;
		}

		@Override
		public void encodeDouble(double sample, byte[] buffer, int offset) {
			Serialize.write24LittleEndian(toScaled24Bit(sample), buffer, offset);
			Serialize.write08(0, buffer, offset + 3);
			
		}

		@Override
		public void encodeFloat(float sample, byte[] buffer, int offset) {
			encodeDouble(sample, buffer, offset);
		}
	},
	SIGNED_32() {
		@Override
		public double decodeDouble(byte[] buffer, int offset) {
			return SCALE_32_TO_DOUBLE * Deserialize.read32LittleEndian(buffer, offset);
		}

		@Override
		public float decodeFloat(byte[] buffer, int offset) {
			return SCALE_32_TO_FLOAT * Deserialize.read32LittleEndian(buffer, offset);
		}

		@Override
		public int bytesPerSample() {
			return 4;
		}

		@Override
		public void encodeDouble(double sample, byte[] buffer, int offset) {
			Serialize.write32LittleEndian(toScaledInteger(sample), buffer, offset);
		}

		@Override
		public void encodeFloat(float sample, byte[] buffer, int offset) {
			encodeDouble(sample, buffer, offset);
		}
	},
	SIGNED_64() {
		@Override
		public double decodeDouble(byte[] buffer, int offset) {
			return SCALE_64_TO_DOUBLE * Deserialize.read64LittleEndian(buffer, offset);
		}
		
		@Override
		public float decodeFloat(byte[] buffer, int offset) {
			return SCALE_64_TO_FLOAT * Deserialize.read64LittleEndian(buffer, offset);
		}
		
		@Override
		public int bytesPerSample() {
			return 8;
		}

		@Override
		public void encodeDouble(double sample, byte[] buffer, int offset) {
			Serialize.write64LittleEndian(toScaledLong(sample), buffer, offset);
		}

		@Override
		public void encodeFloat(float sample, byte[] buffer, int offset) {
			encodeDouble(sample, buffer, offset);
		}
	},
	FLOAT() {
		@Override
		public double decodeDouble(byte[] buffer, int offset) {
			return Deserialize.readFloatLittleEndian(buffer, offset);
		}
		
		@Override
		public float decodeFloat(byte[] buffer, int offset) {
			return Deserialize.readFloatLittleEndian(buffer, offset);
		}
		
		@Override
		public int bytesPerSample() {
			return 4;
		}

		@Override
		public void encodeDouble(double sample, byte[] buffer, int offset) {
			Serialize.writeFloatLittleEndian((float)sample, buffer, offset);
		}

		@Override
		public void encodeFloat(float sample, byte[] buffer, int offset) {
			Serialize.writeFloatLittleEndian(sample, buffer, offset);
		}
		
		@Override
		public Scheme getScheme() {
			return Scheme.FLOAT;
		}
	},
	FLOAT_COOLEDIT() {
		@Override
		public double decodeDouble(byte[] buffer, int offset) {
			return SCALE_24_TO_DOUBLE * Deserialize.readFloatLittleEndian(buffer, offset);
		}
		
		@Override
		public float decodeFloat(byte[] buffer, int offset) {
			return SCALE_24_TO_FLOAT * Deserialize.readFloatLittleEndian(buffer, offset);
		}
		
		@Override
		public int bytesPerSample() {
			return 4;
		}

		@Override
		public void encodeDouble(double sample, byte[] buffer, int offset) {
			Serialize.writeFloatLittleEndian((float)sample, buffer, offset);
		}

		@Override
		public void encodeFloat(float sample, byte[] buffer, int offset) {
			Serialize.writeFloatLittleEndian(sample, buffer, offset);
		}
		
		@Override
		public Scheme getScheme() {
			return Scheme.FLOAT;
		}
	},
	DOUBLE() {
		@Override
		public double decodeDouble(byte[] buffer, int offset) {
			return Deserialize.readDoubleLittleEndian(buffer, offset);
		}
		
		@Override
		public float decodeFloat(byte[] buffer, int offset) {
			return (float)Deserialize.readDoubleLittleEndian(buffer, offset);
		}
		
		@Override
		public int bytesPerSample() {
			return 8;
		}

		@Override
		public void encodeDouble(double sample, byte[] buffer, int offset) {
			Serialize.writeDoubleLittleEndian(sample, buffer, offset);
		}

		@Override
		public void encodeFloat(float sample, byte[] buffer, int offset) {
			Serialize.writeDoubleLittleEndian(sample, buffer, offset);
		}
		
		@Override
		public Scheme getScheme() {
			return Scheme.FLOAT;
		}
	};
	
	@Override
	public Scheme getScheme() {
		return Scheme.TWOS_COMPLEMENT;
	}
	
	public static int readPacked24BitsToInt(ByteBuffer buffer) {
		int sum = buffer.get() & 0xff;
		sum += (buffer.get() & 0xff) << 8;
		sum += buffer.get() << 16; // sign extend
		return sum;
	}
	
	public static int readPadded24BitsToInteger(ByteBuffer buffer) {
		return buffer.getInt() << 8 >> 8; // tail-the-sign-along, bit-23 interpreted as sign
	}
	
	public static int readScaled24BitsToInteger(ByteBuffer buffer) {
		return buffer.getInt() >> 8; // discard low bits
	}
	
	public static void main(String[] args) {
		double value;
		
		value = 0.25;
		for (int i = 0; i < 80; i++) {
			printValue(value);
			value *= 1.5;
		}
		
		value = -0.25;
		for (int i = 0; i < 80; i++) {
			printValue(value);
			value *= 1.5;
		}
	}

	private static void printValue(double value) {
		System.out.printf("Double %-20f; long %-20s int %-20s short %-20s byte %-20s\n",
				value,
				Long.toString((long)Math.rint(value)),
				Integer.toString((int)Math.rint(value)),
				Integer.toString((short)Math.rint(value)),
				Integer.toString((byte)Math.rint(value)));
	}
	
}
