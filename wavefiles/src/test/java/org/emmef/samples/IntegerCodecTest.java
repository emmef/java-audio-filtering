package org.emmef.samples;

public class IntegerCodecTest {
	private final String hexadecimal;
	private final String littleEndian;
	private final String bigEndian;
	
	public IntegerCodecTest(String hex) {
		if (hex == null) {
			throw new NullPointerException("hexadecimal");
		}
		int length = hex.length();
		if (length < 2 || length > 16 || (length & 0x1) != 0) {
			throw new IllegalArgumentException("hexadecimal: expected length is even and between 2 and 16");
		}
		hexadecimal = hex.toLowerCase();
		int intSize = length > 8 ? 16 : 8;
		
		StringBuilder builder = new StringBuilder();
		
		if (hexadecimal.charAt(0) >= '8') {
			for (int i = length; i < intSize; i++) {
				builder.append('f');
			}
		}
		builder.append(hexadecimal);
		while (builder.length() > 1 && builder.charAt(0) == '0') {
			builder.delete(0, 1);
		}
		bigEndian = builder.toString();
		
		builder.setLength(0);
		
		if (hexadecimal.charAt(length - 2) >= '8') {
			for (int i = length; i < intSize; i++) {
				builder.append('f');
			}
		}
		for (int i = length - 2; i >= 0; i -= 2) {
			builder.append(hexadecimal.charAt(i));
			builder.append(hexadecimal.charAt(i + 1));
		}
		while (builder.length() > 1 && builder.charAt(0) == '0') {
			builder.delete(0, 1);
		}
		littleEndian = builder.toString();
	}
	
	public String getHex() {
		return hexadecimal;
	}
	
	public String getBigEndian() {
		return bigEndian;
	}
	
	public String getLittleEndian() {
		return littleEndian;
	}
	
	@Override
	public String toString() {
		return "{input=" + hexadecimal + "; be=" + bigEndian + "; le=" + littleEndian + "}";
	}
}
