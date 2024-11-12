package org.emmef.utils;

import java.util.ArrayList;
import java.util.List;

public class Permutations {
	
	public static List<int[]> createIndexPermutations(int length) {
		int indices[] = new int[length];
		
		for (int i = 0; i < indices.length; i++) {
			indices[i] = -1;
		}
		
		List<int[]> results = new ArrayList<>();
		
		generateForIndex(0, results, indices);
		
		return results;
	}
	
	public static <T> List<List<T>> createObjectPermutations(List<T> input) {
		int indices[] = new int[input.size()];
		
		for (int i = 0; i < indices.length; i++) {
			indices[i] = -1;
		}

		List<List<T>> results = new ArrayList<>();
		
		generateForIndex(0, results, indices, input);
		
		return results;
	}
	
	private static <T> void generateForIndex(int index, List<List<T>> results, int[] indices, List<T> values) {
		if (index == indices.length) {
			List<T> result = new ArrayList<>(index);
			for (int i = 0; i < index; i++) {
				result.add(values.get(indices[i]));
			}
			results.add(result);
			return;
		}
		for (int i = 0; i < indices.length; i++) {
			if (alreadyUsed(index, indices, i)) {
				continue;
			}
			indices[index] = i;
			generateForIndex(index + 1, results, indices, values);
			indices[index] = -1;
		}
	}
	
	private static void generateForIndex(int index, List<int[]> results, int[] indices) {
		if (index == indices.length) {
			results.add(indices.clone());
			return;
		}
		for (int i = 0; i < indices.length; i++) {
			if (alreadyUsed(index, indices, i)) {
				continue;
			}
			indices[index] = i;
			generateForIndex(index + 1, results, indices);
			indices[index] = -1;
		}
	}

	private static boolean alreadyUsed(int index, int[] indices, int i) {
		for (int j = 0; j < index; j++) {
			if (indices[j] == i) {
				return true;
			}
		}
		
		return false;
	}

}
