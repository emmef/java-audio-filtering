package org.emmef.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.emmef.utils.Permutations;
import org.junit.Test;

public class PermutationsTest {
	@Test
	public void testIndexPermutations() {
		for (int i = 1; i < 9; i++) {
			testIndexPermutations(i);
		}
	}
	
	@Test
	public void testObjectPermutations() {
		for (int i = 1; i < 9; i++) {
			testObjectPermutations(i);
		}
	}

	private static void testIndexPermutations(int length) {
		List<int[]> list = Permutations.createIndexPermutations(length);
		
		int faculty = faculty(length);
		assertEquals("Number of permutations must be " + length + "! (" + faculty + ")", faculty, list.size());
		Set<String> usedPermutations = new HashSet<>();
		for (int i = 0; i < list.size(); i++) {
			int[] permutation = list.get(i);
			String concatenated = Arrays.toString(permutation);
			assertFalse("Permutation[" + i + "] " + concatenated + " must be unique", usedPermutations.contains(concatenated));
			usedPermutations.add(concatenated);
		}
	}

	private static void testObjectPermutations(int length) {
		List<Object> input = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			input.add("[" + i + ";" + new Object() + "]");
		}
		List<List<Object>> list = Permutations.createObjectPermutations(input);
		
		int faculty = faculty(length);
		assertEquals("Number of permutations must be " + length + "! (" + faculty + ")", faculty, list.size());
		Set<String> usedPermutations = new HashSet<>();
		for (int i = 0; i < list.size(); i++) {
			List<Object> permutation = list.get(i);
			String concatenated = permutation.toString();
			assertFalse("Permutation[" + i + "] " + concatenated + " must be unique", usedPermutations.contains(concatenated));
			usedPermutations.add(concatenated);
		}
		
	}
	
	public static int faculty(int input) {
		int result = 1;
		for (int i = 2; i <= input; i++) {
			result *= i;
		}
		return result;
	}
}
