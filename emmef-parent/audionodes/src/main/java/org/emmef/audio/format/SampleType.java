package org.emmef.audio.format;

public interface SampleType {
	/**
	 * Returns the type of sample format, which can be floating point or PCM
	 * (integers).
	 * 
	 * @return a non-{@code null} {@link SampleFormat}
	 */
	SampleFormat getSampleFormat();

	/**
	 * Returns the number of bytes that each sample takes up in storage.
	 * <p>
	 * This number is equal or bigger than eight times the number of valid bits
	 * per sample as returned by {@link #getValidBitsPerSample()}.
	 * 
	 * @return a positive number
	 */
	int getBytesPerSample();

	/**
	 * Returns the valid number of bits per sample.
	 * <p>
	 * The valid number of bits doesn't have to be a multiple of 8. However, for
	 * efficiency reasons, containers round the number of bits up to the next
	 * byte or even more. For example, a 20-bit sample is stored in 3 or even 4
	 * bytes.
	 * <p>
	 * Different {@link SampleFormat}s can allow different sets of valid bits.
	 * Floats, for instance, only allow 32 or 64 bits, while PCM allows
	 * everything up to 32 bits.
	 * 
	 * @return a positive number
	 */
	int getValidBitsPerSample();

	/**
	 * Returns a bit-mask of speaker locations for each frame.
	 * <p>
	 * The mask is either 0 (which means default order) or the number of speaker
	 * locations (bits set) must always equal to the number of channels
	 * <p>
	 * Full specs:
	 * http://msdn.microsoft.com/en-us/windows/hardware/gg463006.aspx
	 * 
	 * @return a positive number.
	 */
	long getLocationMask();
	
	/**
	 * Returns the value of 0 dB.
	 * <p>
	 * This is pure information AS-IS. All sound nodes have a 0dB magnitude of
	 * 1.0. The information can in some case be used by providers, but there is
	 * no guarantee.
	 * 
	 * @returns a positive number, most likely {@code 1.0}.
	 */
	double getValue0Dbf();
	
	/**
	 * Returns whether the number of valid bits pers sample is smaller than the number of bytes per sample times eight.
	 * @return {@code} if the number of valid bits pers sample is smaller than the number of bytes per sample times eight, {@code false} otherwise.
	 */
	boolean hasSpareBits();
}
