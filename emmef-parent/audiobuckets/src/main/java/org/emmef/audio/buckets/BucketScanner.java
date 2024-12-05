package org.emmef.audio.buckets;

public class BucketScanner {
	public static final long MAX_SAMPLE_RATE = 192000;
	public static final double MAX_WINDOW_SECONDS = 1.0;
	public static final double MAX_WINDOW_SAMPLES = Math.round(0.5 + MAX_WINDOW_SECONDS * MAX_SAMPLE_RATE);
	public static final double SCALE = Long.MAX_VALUE / MAX_WINDOW_SAMPLES;
	public static final LongInteger ZERO = new LongInteger(1);
	
	private final long[] bucket;
	private final double multiplier;
	private int bucketPosition = 0;
	private long minimum;
	private long maximum;
	private long sum;
	private boolean wholeBucket;

	public BucketScanner(double sampleRate, double bucketSeconds) {
		long bucketSamples = Math.round(bucketSeconds * sampleRate);
		if (bucketSamples < 1) {
			throw new IllegalArgumentException("Invalid bucket size: combination of sample rate (" + sampleRate + ") and seconds (" + bucketSeconds + ") yields zero samples");
		}
		else if (bucketSamples > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Invalid bucket size: combination of sample rate (" + sampleRate + ") and seconds (" + bucketSeconds + ") is too large:" + bucketSamples);
		}
		int bucketSize = (int)bucketSamples;
		this.bucket = new long[bucketSize];
		this.multiplier = 1.0 / (SCALE * bucketSize);
		reset();
	}

	public final void addSample(double sample) {
		long quantizedSample = Math.round(sample * sample * SCALE);
		final long oldestSample = bucket[bucketPosition];
		bucket[bucketPosition] = quantizedSample;
		sum -= oldestSample;
		sum += quantizedSample;
		if (wholeBucket) {
			minimum = Math.min(minimum, sum);
			maximum = Math.max(maximum, sum);
		}
		if (bucketPosition == bucket.length - 1) {
			bucketPosition = 0;
			wholeBucket = true;
		}
		else {
			bucketPosition++;
		}
	}

	private long getSumOfScaledSquared() {
		return sum;
	}

	public void reset() {
		bucketPosition = 0;
		sum = 0;
		wholeBucket = false;
		minimum = Long.MAX_VALUE;
		maximum = Long.MIN_VALUE;
		for (int i = 0; i < bucket.length; i++) {
			bucket[i] = 0;
		}
	}
	
	public boolean isWholeBucketScanned() {
		return wholeBucket;
	}
	
	public int getBucketSize() {
		return bucket.length;
	}
	
	public double getMinimum() {
		return multiplier * minimum;
	}

	public double getMaximum() {
		return multiplier * maximum;
	}

	public double getMeanSquared() {
		return multiplier * getSumOfScaledSquared();
	}

	public double getRootMeanSquared() {
		return Math.sqrt(getMeanSquared());
	}

}
