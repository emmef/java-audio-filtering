package org.emmef.samples;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.emmef.utils.Permutations;
import org.junit.Test;

public class IntegerCodecTestTest {
	
	@Test
	public void test() {
		List<String> input = new ArrayList<>(8);
		StringBuilder hexadecimalBuilder = new StringBuilder();
		StringBuilder bigEndianBuilder = new StringBuilder();
		StringBuilder littleEndianBuilder = new StringBuilder();
		
		for (int byteValue = -128; byteValue < 128; byteValue++) {
			for (int count = 1; count <= 8; count++) {
				testAllPermutationScenarios(byteValue, count, input, hexadecimalBuilder, bigEndianBuilder, littleEndianBuilder);
			}
		}
	}

	private static void testAllPermutationScenarios(int byteValue, int count, List<String> input, StringBuilder hexadecimalBuilder, StringBuilder bigEndianBuilder, StringBuilder littleEndianBuilder) {
		createInput(input, byteValue, count);
		
		for (List<String> perm : Permutations.createObjectPermutations(input)) {
			// perm is a unique permutation of the digits in input
			hexadecimalBuilder.setLength(0);
			bigEndianBuilder.setLength(0);
			littleEndianBuilder.setLength(0);

			// Generate hexadecimal representations
			for (int i = 0; i < count; i++) {
				// original representation, in order
				hexadecimalBuilder.append(perm.get(i));
				// big-endian representation, in order but might have to be sign extended
				bigEndianBuilder.append(perm.get(i));
				// little-endian representation, in reverse order but might have to be sign extended
				littleEndianBuilder.append(perm.get(count - i - 1));
			}
			int bytes = 4 * ((count + 3) / 4);
			int digits = 2 * bytes; // total number of digits required if sign extension is needed
			
			if (bigEndianBuilder.charAt(0) >= '8') {
				// The most significant bit of the most significant byte is set, so need sign extension
				while (bigEndianBuilder.length() < digits) {
					bigEndianBuilder.insert(0, 'f');
				}
			}
			// remove leading zeros
			while (bigEndianBuilder.length() > 1 && bigEndianBuilder.charAt(0) == '0') {
				bigEndianBuilder.delete(0,  1);
			}
			
			if (littleEndianBuilder.charAt(0) >= '8') {
				// The most significant bit of the most significant byte is set, so need sign extension
				while (littleEndianBuilder.length() < digits) {
					littleEndianBuilder.insert(0, 'f');
				}
			}
			// remove leading zeros
			while (littleEndianBuilder.length() > 1 && littleEndianBuilder.charAt(0) == '0') {
				littleEndianBuilder.delete(0,  1);
			}
			
			// Verify if the IntegerCodecTest does its work correctly.
			testScenario(hexadecimalBuilder.toString(), littleEndianBuilder.toString(), bigEndianBuilder.toString());
		}
	}

	/**
	 * Creates an array of strings to be used for permutation generation.
	 * <p>
	 * The first element is the zero-padded, two-digit hexadecimal representation of
	 * {@code byteValue}. The rest of the elements are the zero-padded,
	 * two-digit hexadecimal representations of their respective indices.
	 * <p>
	 * If, for example, {@code byteValue} is 13 and {@code count} is 5,
	 * the generated list of string would be {@code ["0d", "01", "02", "03", "04"]}.
	 * <p>
	 * Filling the list with "low" two-digit presentations is done to prevent the
	 * "other" digits form causing sign extension.
	 * 
	 * @param byteValue byte value for first element
	 * @param count number of elements
	 * @return a list of strings with length {@code count}.
	 */
	private static List<String> createInput(List<String> input, int byteValue, int count) {
		input.clear();
		input.add(String.format("%02x", 0xff & byteValue));
		for (int i = 1; i < count; i++) {
			input.add(String.format("%02x", i));
		}
		
		return input;
	}
	
	public static void testScenario(String hexadecimal, String expectedLittleEndian, String expectedBigEndian) {
		IntegerCodecTest test = new IntegerCodecTest(hexadecimal);
		
		assertEquals("(" + hexadecimal + ") Little indian integer hexadecimal", expectedLittleEndian, test.getLittleEndian());
		assertEquals("(" + hexadecimal + ") Big indian integer hexadecimal", expectedBigEndian, test.getBigEndian());
	}

}
