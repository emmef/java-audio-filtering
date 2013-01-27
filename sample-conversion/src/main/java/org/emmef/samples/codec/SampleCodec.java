package org.emmef.samples.codec;


public interface SampleCodec {
	/**
	 * Type of serialization scheme.
	 */
	enum Scheme {
		/**
		 * A floating point serialization scheme.
		 * <p>
		 * Input and output values for a
		 * CODECs with this storage type are generally limited to the limits of
		 * the floating point type.
		 */
		FLOAT,
		/**
		 * A two's complement integer serialization scheme.
		 * <p>
		 * Input and output values for CODECs with this storage types are
		 * limited to the range <br>
		 * <blockquote>[{@code -1} .. ({@code 1} + {@code 1}/
		 * {@code minimum-integer-value} )].</blockquote> The
		 * {@code  minimum-integer-value} is negative, so the result is
		 * <em>slightly less
		 * than one</em>). The minimum value depends on the number of bits that
		 * is used to store the sample. The bigger the number of bit, the
		 * smaller the asymmetry.
		 */
		TWOS_COMPLEMENT
	}

	/**
	 * Returns the serialization scheme of the CODEC.
	 * <p>
	 * Naturally, this is a constant for each CODEC.
	 * 
	 * @return the serialization scheme of the CODEC.
	 */
	Scheme getScheme();
	
	/**
	 * Returns the number of bytes that a serialized sample takes.
	 * <p>
	 * Naturally, this is a constant for each CODEC.
	 * 
	 * @return the number of bytes that a serialized sample takes.
	 */
	int bytesPerSample();
	
	/**
	 * Decodes and returns  a sample from the provider buffer at the given offset.
	 * <p>
	 * This method assumes the caller checks that the buffer is not {@code null}
	 * and that the offset plus the number of bytes do not exceed the buffer
	 * size. If not, low level exceptions are probable.
	 * 
	 * @param buffer
	 *            buffer from which to read serialized data
	 * @param offset
	 *            offset inside the buffer
	 * @return a decoded value
	 * @throws NullPointerException
	 *             if the buffer is {@code null}
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the combination of offset and the number of bytes per
	 *             sample exceed the buffer size.
	 */
	double decodeDouble(byte[] buffer, int offset);
	
	/**
	 * Decodes and returns a sample from the provider buffer at the given offset.
	 * <p>
	 * This method assumes the caller checks that the buffer is not {@code null}
	 * and that the offset plus the number of bytes do not exceed the buffer
	 * size. If not, low level exceptions are probable.
	 * 
	 * @param buffer
	 *            buffer from which to read serialized data
	 * @param offset
	 *            offset inside the buffer
	 * @return a decoded value
	 * @throws NullPointerException
	 *             if the buffer is {@code null}
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the combination of offset and the number of bytes per
	 *             sample exceed the buffer size.
	 */
	float decodeFloat(byte[] buffer, int offset);
	
	/**
	 * Encode a sample to the provider buffer at at the given offset.
	 * <p>
	 * This method assumes the caller checks that the buffer is not {@code null}
	 * and that the offset plus the number of bytes do not exceed the buffer
	 * size. If not, low level exceptions are probable.
	 * 
	 * @param buffer
	 *            buffer from which to read serialized data
	 * @param offset
	 *            offset inside the buffer

	 * @throws NullPointerException
	 *             if the buffer is {@code null}
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the combination of offset and the number of bytes per
	 *             sample exceed the buffer size.
	 */
	void encodeDouble(double sample, byte[] buffer, int offset);
	
	/**
	 * Encode a sample to the provider buffer at at the given offset.
	 * <p>
	 * This method assumes the caller checks that the buffer is not {@code null}
	 * and that the offset plus the number of bytes do not exceed the buffer
	 * size. If not, low level exceptions are probable.
	 * 
	 * @param buffer
	 *            buffer from which to read serialized data
	 * @param offset
	 *            offset inside the buffer

	 * @throws NullPointerException
	 *             if the buffer is {@code null}
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the combination of offset and the number of bytes per
	 *             sample exceed the buffer size.
	 */
	void encodeFloat(float sample, byte[] buffer, int offset);
}
