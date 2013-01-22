package org.emmef.samples;

import java.nio.ByteBuffer;

public enum SampleReaders implements SampleReader {
	SIGNED_8() {
		@Override
		public double readDouble(ByteBuffer buffer) {
			return SampleScales.SCALE_8_TO_DOUBLE * buffer.get();
		}
		
		@Override
		public float readFloat(ByteBuffer buffer) {
			return SampleScales.SCALE_8_TO_FLOAT * buffer.get();
		}
		
		@Override
		public int bytesPerSample() {
			return 1;
		}
	},
	UNSIGNED_8() {
		@Override
		public double readDouble(ByteBuffer buffer) {
			return SampleScales.SCALE_8_TO_DOUBLE * (-128 + (0xff & buffer.get()));
		}
		
		@Override
		public float readFloat(ByteBuffer buffer) {
			return SampleScales.SCALE_8_TO_FLOAT * (-128 + (0xff & buffer.get()));
		}
		
		@Override
		public int bytesPerSample() {
			return 1;
		}
	},
	SIGNED_16() {
		@Override
		public double readDouble(ByteBuffer buffer) {
			return SampleScales.SCALE_16_TO_DOUBLE * buffer.getShort();
		}

		@Override
		public float readFloat(ByteBuffer buffer) {
			return SampleScales.SCALE_16_TO_FLOAT * buffer.getShort();
		}

		@Override
		public int bytesPerSample() {
			return 2;
		}
	},
	PACKED_24() {
		@Override
		public double readDouble(ByteBuffer buffer) {
			return SampleScales.SCALE_16_TO_DOUBLE * readInt3(buffer);
		}

		@Override
		public float readFloat(ByteBuffer buffer) {
			return SampleScales.SCALE_16_TO_FLOAT * readInt3(buffer);
		}

		@Override
		public int bytesPerSample() {
			return 3;
		}
		
		private int readInt3(ByteBuffer buffer) {
			int sum = buffer.get() & 0xff;
			sum += (buffer.get() & 0xff) << 8;
			sum += buffer.get() << 16; // sign extend
			return sum;
		}
	},
	PADDED_24() {
		@Override
		public double readDouble(ByteBuffer buffer) {
			return SampleScales.SCALE_16_TO_DOUBLE * readPad24(buffer);
		}

		@Override
		public float readFloat(ByteBuffer buffer) {
			return SampleScales.SCALE_16_TO_FLOAT * readPad24(buffer);
		}

		@Override
		public int bytesPerSample() {
			return 3;
		}
		
		private int readPad24(ByteBuffer buffer) {
			return buffer.getInt() << 8 >> 8; // tail-the-sign-along, bit-23 interpreted as sign
		}
	},
	SCALED_24() {
		@Override
		public double readDouble(ByteBuffer buffer) {
			return SampleScales.SCALE_16_TO_DOUBLE * readScaled24(buffer);
		}
		
		@Override
		public float readFloat(ByteBuffer buffer) {
			return SampleScales.SCALE_16_TO_FLOAT * readScaled24(buffer);
		}
		
		@Override
		public int bytesPerSample() {
			return 3;
		}
		
		private int readScaled24(ByteBuffer buffer) {
			return buffer.getInt() >> 8; // discard low bits
		}
	},
	SIGNED_32() {
		@Override
		public double readDouble(ByteBuffer buffer) {
			return SampleScales.SCALE_32_TO_DOUBLE * buffer.getInt();
		}

		@Override
		public float readFloat(ByteBuffer buffer) {
			return SampleScales.SCALE_32_TO_FLOAT * buffer.getInt();
		}

		@Override
		public int bytesPerSample() {
			return 4;
		}
	},
	SIGNED_64() {
		@Override
		public double readDouble(ByteBuffer buffer) {
			return SampleScales.SCALE_64_TO_DOUBLE * buffer.getLong();
		}
		
		@Override
		public float readFloat(ByteBuffer buffer) {
			return SampleScales.SCALE_64_TO_FLOAT * buffer.getLong();
		}
		
		@Override
		public int bytesPerSample() {
			return 8;
		}
	},
	FLOAT() {
		@Override
		public double readDouble(ByteBuffer buffer) {
			return buffer.getFloat();
		}
		
		@Override
		public float readFloat(ByteBuffer buffer) {
			return buffer.getFloat();
		}
		
		@Override
		public int bytesPerSample() {
			return 4;
		}
	},
	DOUBLE() {
		@Override
		public double readDouble(ByteBuffer buffer) {
			return buffer.getDouble();
		}
		
		@Override
		public float readFloat(ByteBuffer buffer) {
			return (float)buffer.getDouble();
		}
		
		@Override
		public int bytesPerSample() {
			return 8;
		}
	};
}
