package org.emmef.audio.buckets;

public class LongInteger implements Comparable<LongInteger> {
	public static final int MAX_INTS = 16;
	private static final long POSITIVE_MASK = 0x7FFFFFFFFFFFFFFFL; 
	private static final long CARRY_MASK =    0x8000000000000000L;
	private static final double[] scales;
	
	static {
		scales = new double[MAX_INTS];
		scales[0] = 1.0;
		for (int i = 1; i < MAX_INTS; i++) {
			int power = i * 63;
			scales[i] = Math.pow(2, power);
		}
	}
	
	private long[] ints;
	private boolean isPositive;
	
	private LongInteger(long[] ints, boolean isPositive) {
		this.ints = ints;
		this.isPositive = isPositive;
	}
	
	public LongInteger(int numberOfInts) {
		this(new long[checkNumberOfInts(numberOfInts)], true);
	}
	
	public void add(long value) {
		if (isPositive) {
			if (value > 0) {
				addInts(value);
			}
			else {
				subtractInts(-value);
			}
		}
		else {
			if (value < 0) {
				addInts(-value);
			}
			else {
				subtractInts(value);
			}
		}
	}
	
	public void subtract(long value) {
		if (isPositive) {
			if (value > 0) {
				subtractInts(value);
			}
			else {
				addInts(-value);
			}
		}
		else {
			if (value < 0) {
				subtractInts(-value);
			}
			else {
				addInts(value);
			}
		}
	}
	
	public int compareTo(LongInteger o) {
		if (isPositive) {
			if (!o.isPositive) {
				return 1;
			}
			return compareInts(ints, o.ints);
		}
		if (o.isPositive) {
			return -1;
		}
		return compareInts(o.ints, ints);
	}
	
	public void set(LongInteger value) { 
		if (value == null) {
			throw new NullPointerException("value");
		}
		if (value.ints.length <= ints.length) {
			int i;
			for (i = 0; i < value.ints.length; i++) {
				ints[i] = value.ints[i];
			}
			for (; i < ints.length; i++) {
				ints[i] = 0;
			}
			isPositive = value.isPositive;
			return;
		}
		for (int i = ints.length; i < value.ints.length; i++) {
			if (value.ints[i] != 0) {
				throw new ArithmeticException("Cannot hold value");
			}
		}
	}
	
	public void set(long value) {
		ints[0] = POSITIVE_MASK & value;
		isPositive = (CARRY_MASK & value) == 0;
		for (int i = 1; i < ints.length; i++) {
			ints[i] = 0;
		}
	}
	
	public LongInteger clone() {
		return new LongInteger(ints.clone(), isPositive);
	}
	
	public double doubleValue() {
		int i;
		for (i = ints.length - 1; i >= 0; i--) {
			if (ints[i] != 0) {
				break;
			}
		}
		double sum = 0.0;
		for (int cnt = 0; i >= 0 && cnt < 2; i--, cnt++) {
			sum += scales[i] * ints[i]; 
		}
		return isPositive ? sum : -sum;
	}

	private void subtractInts(long positiveNumber) {
		if (positiveNumber <= ints[0]) {
			ints[0] -= positiveNumber;
			return;
		}
		for (int i = 1; i < ints.length; i++) {
			if (ints[i] != 0) {
				ints[i]--;
				for (int j = i - 1; j > 0; j--) {
					ints[j] = POSITIVE_MASK;
				}
				ints[0] = POSITIVE_MASK & (-1 + ints[0] - positiveNumber);
				return;
			}
		}
		ints[0] = positiveNumber - ints[0];
		isPositive = !isPositive;
		for (int i = 1; i < ints.length; i++) {
			ints[i] = 0;
		}
	}

	private void addInts(long positiveNumber) {
		long x = positiveNumber;
		long sum = 0;
		for (int i = 0; i < ints.length; i++) {
			sum = ints[i] + x;
			final long positive = sum & POSITIVE_MASK;
			ints[i] = positive;
			if (sum == positive) {
				break;
			}
			x = 1;
		}
		if ((sum & CARRY_MASK) != 0) {
			throw new ArithmeticException("Arithmic overflow!");
		}
	}

	private static int compareInts(long[] first, long[] second) {
		int i;
		if (second.length > first.length) {
			for (i = second.length - 1; i >= first.length; i--) {
				if (second[i] != 0) {
					return -1;
				}
			}
		}
		else {
			for (i = first.length - 1; i >= second.length; i--) {
				if (first[i] != 0) {
					return 1;
				}
			}
		}
		for (; i >= 0; i--) {
			long diff = first[i] - second[i];
			if (diff > 0) {
				return 1;
			}
			if (diff < 0) {
				return -1;
			}
		}
		
		return 0;
	}

	private static int checkNumberOfInts(int numberOfInts) {
		if (numberOfInts < 1 || numberOfInts > MAX_INTS) {
			throw new IllegalArgumentException("Number of integers should be between 1 and " + MAX_INTS);
		}
		return numberOfInts;
	}
}
