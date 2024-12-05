package org.emmef.audio.buckets;

public class BucketScanner {
	public static final long MAX_SAMPLE_RATE = 192000;
	public static final double MAX_WINDOW_SECONDS = 1.0;
	public static final double MAX_WINDOW_SAMPLES = Math.round(0.5 + MAX_WINDOW_SECONDS * MAX_SAMPLE_RATE);
	public static final double SCALE = Long.MAX_VALUE / MAX_WINDOW_SAMPLES;
	public static final LongInteger ZERO = new LongInteger(1);
	
	private final long[] bucket;
	private final double multiplier;
	private int bucketPosition;
	private final LongInteger minimum = new LongInteger(2);
	private final LongInteger maximum = new LongInteger(2);
	private final LongInteger sum = new LongInteger(2);
	private boolean wholeBucket;

	public BucketScanner(int bucketSize) {
		if (bucketSize < 1) {
			throw new IllegalStateException("Bucket should contain at least 1 sample");
		}
		this.bucket = new long[bucketSize];
		this.multiplier = 1.0 / (SCALE * bucketSize);
		reset();
	}
	
	public final void addSample(double sample) {
		long quantizedSample = Math.round(sample * sample * SCALE);
		final long oldestSample = bucket[bucketPosition];
		bucket[bucketPosition] = quantizedSample;
		sum.subtract(oldestSample);
		sum.add(quantizedSample);
		if (wholeBucket) {
			if (sum.compareTo(minimum) < 0) {
				minimum.set(sum);
			}
			if (sum.compareTo(maximum) > 0) {
				maximum.set(sum);
			}
		}
		if (bucketPosition == bucket.length - 1) {
			bucketPosition = 0;
			wholeBucket = true;
		}
		else {
			bucketPosition++;
		}
	}
	public void reset() {
		bucketPosition = 0;
		sum.set(0);
		wholeBucket = false;
		minimum.set(Long.MAX_VALUE);
		maximum.set(Long.MIN_VALUE);
		for (int i = 0; i < bucket.length; i++) {
			bucket[i] = 0;
			minimum.add(Long.MAX_VALUE);
			maximum.add(Long.MIN_VALUE);
		}
	}
	
	public boolean isWholeBucketScanned() {
		return wholeBucket;
	}
	
	public int getBucketSize() {
		return bucket.length;
	}
	
	public double getMinimum() {
		return multiplier * minimum.doubleValue();
	}

	public double getMaximum() {
		return multiplier * maximum.doubleValue();
	}

	public double getMeanSquared() {
		return multiplier * sum.doubleValue();
	}

	public double getRootMeanSquared() {
		return Math.sqrt(getMeanSquared());
	}

}
