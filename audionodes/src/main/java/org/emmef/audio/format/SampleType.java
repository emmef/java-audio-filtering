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
	 * The number of speaker locations is always equal to the number of
	 * channels.
	 * 
	 * @return a positive number.
	 */
	long getLocationMask();
	
	/**
	 * Returns the sample value that indicates 0DBF.
	 * <p>
	 * For integer valud values, this generally is  
	 * @return
	 */
	double getValue0Dbf();
}
