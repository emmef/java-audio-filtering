package org.emmef.samples;

import static org.emmef.samples.SampleScales.*;

import java.nio.ByteBuffer;

public enum SampleCodecs implements SampleCodec {
	SIGNED_8() {
		@Override
		public double decodeDouble(ByteBuffer buffer) {
			byte b = buffer.get();
			return SCALE_8_TO_DOUBLE * b;
		}
		
		@Override
		public float decodeFloat(ByteBuffer buffer) {
			return SCALE_8_TO_FLOAT * buffer.get();
		}
		
		@Override
		public int bytesPerSample() {
			return 1;
		}

		@Override
		public void encodeDouble(ByteBuffer buffer, double sample) {
			buffer.put(toScaledByte(sample));
		}

		@Override
		public void encodeFloat(ByteBuffer buffer, float sample) {
			encodeDouble(buffer, sample);
		}
	},
	UNSIGNED_8() {
		@Override
		public double decodeDouble(ByteBuffer buffer) {
			return SCALE_8_TO_DOUBLE * (-128 + (0xff & buffer.get()));
		}
		
		@Override
		public float decodeFloat(ByteBuffer buffer) {
			return SCALE_8_TO_FLOAT * (-128 + (0xff & buffer.get()));
		}
		
		@Override
		public int bytesPerSample() {
			return 1;
		}

		@Override
		public void encodeDouble(ByteBuffer buffer, double sample) {
			buffer.put(toScaled128UpByte(sample));
		}

		@Override
		public void encodeFloat(ByteBuffer buffer, float sample) {
			encodeDouble(buffer, sample);
		}
	},
	SIGNED_16() {
		@Override
		public double decodeDouble(ByteBuffer buffer) {
			return SCALE_16_TO_DOUBLE * buffer.getShort();
		}

		@Override
		public float decodeFloat(ByteBuffer buffer) {
			return SCALE_16_TO_FLOAT * buffer.getShort();
		}

		@Override
		public int bytesPerSample() {
			return 2;
		}

		@Override
		public void encodeDouble(ByteBuffer buffer, double sample) {
			buffer.putShort(toScaledShort(sample));
		}

		@Override
		public void encodeFloat(ByteBuffer buffer, float sample) {
			encodeDouble(buffer, sample);
		}
	},
	PACKED_24() {
		@Override
		public double decodeDouble(ByteBuffer buffer) {
			return SCALE_24_TO_DOUBLE * readPacked24BitsToInt(buffer);
		}

		@Override
		public float decodeFloat(ByteBuffer buffer) {
			return SCALE_24_TO_FLOAT * readPacked24BitsToInt(buffer);
		}

		@Override
		public int bytesPerSample() {
			return 3;
		}

		@Override
		public void encodeDouble(ByteBuffer buffer, double sample) {
			int bit24 = toScaled24Bit(sample);
			buffer.put((byte)(0xff & bit24));
			bit24 >>= 8;
			buffer.put((byte)(0xff & bit24));
			bit24 >>= 8;
			buffer.put((byte)(0xff & bit24));
		}

		@Override
		public void encodeFloat(ByteBuffer buffer, float sample) {
			encodeDouble(buffer, sample);
		}
	},
	PADDED_24() {
		@Override
		public double decodeDouble(ByteBuffer buffer) {
			return SCALE_24_TO_DOUBLE * readPadded24BitsToInteger(buffer);
		}

		@Override
		public float decodeFloat(ByteBuffer buffer) {
			return SCALE_24_TO_FLOAT * readPadded24BitsToInteger(buffer);
		}

		@Override
		public int bytesPerSample() {
			return 3;
		}

		@Override
		public void encodeDouble(ByteBuffer buffer, double sample) {
			int bit24 = toScaled24Bit(sample);
			buffer.put((byte)(0xff & bit24));
			bit24 >>= 8;
			buffer.put((byte)(0xff & bit24));
			bit24 >>= 8;
			buffer.put((byte)(0xff & bit24));
			buffer.put((byte)0);
		}

		@Override
		public void encodeFloat(ByteBuffer buffer, float sample) {
			encodeDouble(buffer, sample);
		}
	},
	SCALED_24() {
		@Override
		public double decodeDouble(ByteBuffer buffer) {
			return SCALE_24_TO_DOUBLE * readScaled24BitsToInteger(buffer);
		}

		@Override
		public float decodeFloat(ByteBuffer buffer) {
			return SCALE_24_TO_FLOAT * readScaled24BitsToInteger(buffer);
		}

		@Override
		public int bytesPerSample() {
			return 3;
		}

		@Override
		public void encodeDouble(ByteBuffer buffer, double sample) {
			buffer.putInt(toScaledInteger(sample));
		}

		@Override
		public void encodeFloat(ByteBuffer buffer, float sample) {
			encodeDouble(buffer, sample);
		}
	},
	SIGNED_32() {
		@Override
		public double decodeDouble(ByteBuffer buffer) {
			return SCALE_32_TO_DOUBLE * buffer.getInt();
		}

		@Override
		public float decodeFloat(ByteBuffer buffer) {
			return SCALE_32_TO_FLOAT * buffer.getInt();
		}

		@Override
		public int bytesPerSample() {
			return 4;
		}

		@Override
		public void encodeDouble(ByteBuffer buffer, double sample) {
			buffer.putInt(toScaledInteger(sample));
		}

		@Override
		public void encodeFloat(ByteBuffer buffer, float sample) {
			encodeDouble(buffer, sample);
		}
	},
	SIGNED_64() {
		@Override
		public double decodeDouble(ByteBuffer buffer) {
			return SCALE_64_TO_DOUBLE * buffer.getLong();
		}
		
		@Override
		public float decodeFloat(ByteBuffer buffer) {
			return SCALE_64_TO_FLOAT * buffer.getLong();
		}
		
		@Override
		public int bytesPerSample() {
			return 8;
		}

		@Override
		public void encodeDouble(ByteBuffer buffer, double sample) {
			buffer.putLong(toScaledLong(sample));
		}

		@Override
		public void encodeFloat(ByteBuffer buffer, float sample) {
			encodeDouble(buffer, sample);
		}
	},
	FLOAT() {
		@Override
		public double decodeDouble(ByteBuffer buffer) {
			return buffer.getFloat();
		}
		
		@Override
		public float decodeFloat(ByteBuffer buffer) {
			return buffer.getFloat();
		}
		
		@Override
		public int bytesPerSample() {
			return 4;
		}

		@Override
		public void encodeDouble(ByteBuffer buffer, double sample) {
			buffer.putFloat((float)sample);
		}

		@Override
		public void encodeFloat(ByteBuffer buffer, float sample) {
			buffer.putFloat(sample);
		}
		
		@Override
		public Storage getStorage() {
			return Storage.FLOAT;
		}
	},
	DOUBLE() {
		@Override
		public double decodeDouble(ByteBuffer buffer) {
			return buffer.getDouble();
		}
		
		@Override
		public float decodeFloat(ByteBuffer buffer) {
			return (float)buffer.getDouble();
		}
		
		@Override
		public int bytesPerSample() {
			return 8;
		}

		@Override
		public void encodeDouble(ByteBuffer buffer, double sample) {
			buffer.putDouble(sample);
		}

		@Override
		public void encodeFloat(ByteBuffer buffer, float sample) {
			buffer.putDouble(sample);
		}
		
		@Override
		public Storage getStorage() {
			return Storage.FLOAT;
		}
	};
	
	@Override
	public Storage getStorage() {
		return Storage.TWOS_COMPLEMENT;
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
