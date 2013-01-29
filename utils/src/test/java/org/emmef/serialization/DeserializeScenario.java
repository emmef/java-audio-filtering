package org.emmef.serialization;

import org.emmef.serialization.Deserialize;

/**
 * Predicts the hexadecimal outcome of a serialized (also hexadecimal) value.
 * <p>
 * This class is used to test the behavior and correctness of the
 * {@link Deserialize} classes.
 */
public class DeserializeScenario {
	private final String hexadecimal;
	private final String littleEndian;
	private final String bigEndian;

	/**
	 * Creates a new scenario from the provided hexadecimal value.
	 * <p>
	 * The predicted output values for big-endian and little-endian
	 * serialization can be obtained by the {@link #getBigEndian()} and
	 * {@link #littleEndian} methods respectively.
	 * <p>
	 * The result of negative values are always sign extended, which means that
	 * all bits are set until the 4 or 8 byte word boundary. Leading zeroes are
	 * always removed from the output.
	 * 
	 * @param hex
	 *            hexadecimal input value, a string with a length between 2 and
	 *            16 inclusive and that can only contain hexadecimal digits.
	 */
	public DeserializeScenario(String hex) {
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
	
	/**
	 * Returns the hexadecimal value, ensured in lower-case.
	 * 
	 * @return a non-{@code null} String
	 */
	public String getHexadecimal() {
		return hexadecimal;
	}
	
	/**
	 * Returns the hexadecimal value of the number that is the result of
	 * deserialization in big-endian order (most significant first).
	 * <p>
	 * The "value" is interpreted as a two's complement number with the input
	 * length. If that interpretation is a positive number, the value will be
	 * the same as the input, except that all leadin zeroes are removed. If the
	 * interpretation is a negative number, (that have the first digit >
	 * {@code '8'}), the number if sign extended to the next word boundary. The
	 * word boundary is 32-bits for inputs with length &lt;= 8 and 64-bits
	 * otherwise, resulting in an output of 8 or 16 characters respectively.
	 * 
	 * @return a non-{@code null} String
	 */
	public String getBigEndian() {
		return bigEndian;
	}
	
	/**
	 * Returns the hexadecimal value of the number that is the result of
	 * deserialization in little-endian order (most significant first).
	 * <p>
	 * The "value" is the input, except that all character pairs have been
	 * inverted: {@code "01ab"} would become {@code "ab01"}.
	 * <p>
	 * The "value" is interpreted as a two's complement number with the input
	 * length. If that interpretation is a positive number, the value will be
	 * the same as the input, except that all leadin zeroes are removed. If the
	 * interpretation is a negative number, (that have the first digit >
	 * {@code '8'}), the number if sign extended to the next word boundary. The
	 * word boundary is 32-bits for inputs with length &lt;= 8 and 64-bits
	 * otherwise, resulting in an output of 8 or 16 characters respectively.
	 * 
	 * @return a non-{@code null} String
	 */
	public String getLittleEndian() {
		return littleEndian;
	}
	
	@Override
	public String toString() {
		return "{input=" + hexadecimal + "; be=" + bigEndian + "; le=" + littleEndian + "}";
	}
}
