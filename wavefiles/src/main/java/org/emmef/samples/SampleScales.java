package org.emmef.samples;

public final class SampleScales {
	public static final double SCALE_8_TO_DOUBLE = 1.0 / 128;
	public static final float SCALE_8_TO_FLOAT = (float)SCALE_8_TO_DOUBLE;
	
	public static final double SCALE_16_TO_DOUBLE = 1.0 / 0x8000;
	public static final float SCALE_16_TO_FLOAT = (float)SCALE_16_TO_DOUBLE;
	
	public static final double SCALE_24_TO_DOUBLE = 1.0 / 0x800000;
	public static final float SCALE_24_TO_FLOAT = (float)SCALE_24_TO_DOUBLE;
	
	public static final double SCALE_32_TO_DOUBLE = -1.0 / 0x80000000;
	public static final float SCALE_32_TO_FLOAT = (float)SCALE_32_TO_DOUBLE;
	
	public static final double SCALE_64_TO_DOUBLE = -1.0 / 0x8000000000000000L;
	public static final float SCALE_64_TO_FLOAT = (float)SCALE_64_TO_DOUBLE;
	
	public static final double SCALE_8_FROM_DOUBLE = 0x7f;
	public static final float SCALE_8_FROM_FLOAT = (float)SCALE_8_FROM_DOUBLE;
	
	public static final double SCALE_16_FROM_DOUBLE = 0x7fff;
	public static final float SCALE_16_FROM_FLOAT = (float)SCALE_16_FROM_DOUBLE;
	
	public static final double SCALE_24_FROM_DOUBLE = 0x7fffff;
	public static final float SCALE_24_FROM_FLOAT = (float)SCALE_24_FROM_DOUBLE;
	
	public static final double SCALE_32_FROM_DOUBLE = 0x7fffffff;
	public static final float SCALE_32_FROM_FLOAT = (float)SCALE_32_FROM_DOUBLE;
	
	public static final double SCALE_64_FROM_DOUBLE = 0x7fffffffffffffffL;
	public static final float SCALE_64_FROM_FLOAT = (float)SCALE_64_FROM_DOUBLE;
}