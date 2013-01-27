package org.emmef.samples.codec;


public final class SampleScales {
	public static final int MIN_VALUE_24_BIT = -0x800000;
	public static final int MAX_VALUE_24_BIT =  0x7fffff;
	
	public static final double SCALE_8_TO_DOUBLE = -1.0 / Byte.MIN_VALUE;
	public static final float SCALE_8_TO_FLOAT = (float)SCALE_8_TO_DOUBLE;
	
	public static final double SCALE_16_TO_DOUBLE = -1.0 / Short.MIN_VALUE;
	public static final float SCALE_16_TO_FLOAT = (float)SCALE_16_TO_DOUBLE;
	
	public static final double SCALE_24_TO_DOUBLE = -1.0 / MIN_VALUE_24_BIT;
	public static final float SCALE_24_TO_FLOAT = (float)SCALE_24_TO_DOUBLE;
	
	public static final double SCALE_32_TO_DOUBLE = -1.0 / Integer.MIN_VALUE;
	public static final float SCALE_32_TO_FLOAT = (float)SCALE_32_TO_DOUBLE;
	
	public static final double SCALE_64_TO_DOUBLE = -1.0 / Long.MIN_VALUE;
	public static final float SCALE_64_TO_FLOAT = (float)SCALE_64_TO_DOUBLE;
	
	public static final double SCALE_8_FROM_DOUBLE = -Byte.MIN_VALUE;
	public static final float SCALE_8_FROM_FLOAT = (float)SCALE_8_FROM_DOUBLE;
	
	public static final double SCALE_16_FROM_DOUBLE = -Short.MIN_VALUE;
	public static final float SCALE_16_FROM_FLOAT = (float)SCALE_16_FROM_DOUBLE;
	
	public static final double SCALE_24_FROM_DOUBLE = -MIN_VALUE_24_BIT;
	public static final float SCALE_24_FROM_FLOAT = (float)SCALE_24_FROM_DOUBLE;
	
	public static final double SCALE_32_FROM_DOUBLE = -1.0 * Integer.MIN_VALUE;
	public static final float SCALE_32_FROM_FLOAT = (float)SCALE_32_FROM_DOUBLE;
	
	public static final double SCALE_64_FROM_DOUBLE = -1.0 * Long.MIN_VALUE;
	public static final float SCALE_64_FROM_FLOAT = (float)SCALE_64_FROM_DOUBLE;

	/**
	 * Returns the closest int to the provided real value.
	 * <p>
	 * Exceptions:
	 * <ul>
	 * <li>If the number is NaN, 0 is returned.
	 * <li>If the number is < {@link Integer#MIN_VALUE}, {@link Integer#MIN_VALUE} is returned
	 * <li>If the number is > {@link Integer#MAX_VALUE}, {@link Integer#MAX_VALUE} is returned
	 * </ul>
	 * <p>
	 * This method makes use of the java language specification that states that
	 * narrowing from floating point numbers to integer and long are capped
	 * by the limits of the type.
	 * 
	 * @param sample input value.
	 * @return closest int to the provided real value.
	 */
	public static int toScaledInteger(double sample) {
		double rint = Math.rint(SCALE_32_FROM_DOUBLE * sample);
		return (int)rint;
	}
	
	/**
	 * Returns the closest long to the provided real value.
	 * <p>
	 * Exceptions:
	 * <ul>
	 * <li>If the number is NaN, 0 is returned.
	 * <li>If the number is < {@link Long#MIN_VALUE}, {@link Long#MIN_VALUE} is returned
	 * <li>If the number is > {@link Long#MAX_VALUE}, {@link Long#MAX_VALUE} is returned
	 * </ul>
	 * <p>
	 * This method makes use of the java language specification that states that
	 * narrowing from floating point numbers to integer and long are capped
	 * by the limits of the type.
	 * 
	 * @param sample input value.
	 * @return closest long to the provided real value.
	 */
	public static long toScaledLong(double sample) {
		return (long)Math.rint(SCALE_64_FROM_DOUBLE * sample);
	}

	/**
	 * Returns the closest short to the provided real value.
	 * <p>
	 * Exceptions:
	 * <ul>
	 * <li>If the number is NaN, 0 is returned.
	 * <li>If the number is < {@link Short#MIN_VALUE}, {@link Short#MIN_VALUE} is returned
	 * <li>If the number is > {@link Short#MAX_VALUE}, {@link Short#MAX_VALUE} is returned
	 * </ul>
	 * 
	 * @param sample input value.
	 * @return closest short to the provided real value.
	 */
	public static short toScaledShort(double sample) {
		int rint = (int)Math.rint(SCALE_16_FROM_DOUBLE * sample);
		return rint < Short.MIN_VALUE ? Short.MIN_VALUE : rint > Short.MAX_VALUE ? Short.MAX_VALUE : (short)rint;
	}
	
	/**
	 * Returns the closest byte to the provided real value.
	 * <p>
	 * Exceptions:
	 * <ul>
	 * <li>If the number is NaN, 0 is returned.
	 * <li>If the number is < {@link Byte#MIN_VALUE}, {@link Byte#MIN_VALUE} is returned
	 * <li>If the number is > {@link Byte#MAX_VALUE}, {@link Byte#MAX_VALUE} is returned
	 * </ul>
	 * 
	 * @param sample input value.
	 * @return closest byte to the provided real value.
	 */
	public static byte toScaledByte(double sample) {
		int rint = (int)Math.rint(SCALE_8_FROM_DOUBLE * sample);
		
		byte b = rint < Byte.MIN_VALUE ? Byte.MIN_VALUE : rint > Byte.MAX_VALUE ? Byte.MAX_VALUE : (byte)rint;
		return b;
	}
	
	/**
	 * Returns the closest 128 based, 8-bit word to the provided real value.
	 * <p>
	 * In some audio formats, 8 bit unsigned values are used, where
	 * 0..127 are negative values, 128 is 0 and 129..255 are positive values.
	 * <p>
	 * Exceptions:
	 * <ul>
	 * <li>If the number is NaN, 0 is returned.
	 * <li>If the number is < 0, 0 is returned
	 * <li>If the number is > 255, 255 is returned
	 * </ul>
	 * 
	 * @param sample input value.
	 * @return the closest 128 based, 8-bit word to the provided real value.
	 */
	public static byte toScaled128UpByte(double sample) {
		int rint = (int)Math.rint(SCALE_8_FROM_DOUBLE * sample);
		
		return (byte)(128 + (rint < Byte.MIN_VALUE ? Byte.MIN_VALUE : rint > Byte.MAX_VALUE ? Byte.MAX_VALUE : rint));
	}
	
	/**
	 * Returns the closest 24-bit signed word to the provided real value.
	 * <p>
	 * <ul>
	 * <li>If the number is NaN, 0 is returned.
	 * <li>If the number is < {@link #MIN_VALUE_24_BIT}, {@link #MIN_VALUE_24_BIT}  is returned
	 * <li>If the number is > {@link #MAX_VALUE_24_BIT}, {@link #MAX_VALUE_24_BIT} is returned
	 * </ul>
	 * 
	 * @param sample input value.
	 * @return the closest 24-bit signed word to the provided real value.
	 */
	public static int toScaled24Bit(double sample) {
		int rint = (int)Math.rint(SCALE_24_FROM_DOUBLE * sample);
		
		return rint < MIN_VALUE_24_BIT ? MIN_VALUE_24_BIT : rint > MAX_VALUE_24_BIT ? MAX_VALUE_24_BIT : rint;
	}
}