package org.emmef.audio.buckets;

public class BucketScanner implements Detection {
	public static final long SCALE = 2L^40;
	public static final LongInteger ZERO = new LongInteger(1);
	private static final double WINDOW_MIN = 0.01;
	private static final double WINDOW_MAX = 0.5;

	private final long[] bucket;
	private final double multiplier;
	private int bucketPosition;
	private final LongInteger minimum = new LongInteger(2);
	private final LongInteger maximum = new LongInteger(2);
	private final LongInteger sum = new LongInteger(2);
	private boolean wholeBucket;

	public BucketScanner(long sampleRate, double windowSize) {
		this((int)Math.round(sampleRate * Math.min(WINDOW_MAX, Math.max(WINDOW_MIN, windowSize))));
		
	}
	
	private BucketScanner(int bucketSize) {
		if (bucketSize < 1) {
			throw new IllegalStateException("Bucket should contain at least 1 sample");
		}
		this.bucket = new long[bucketSize];
		this.multiplier = 1.0 / (1.0 * SCALE * bucketSize);
		reset();
	}

	@Override
	public double addSample(double sample) {
		addScaledSample((long) (sample * sample * SCALE));
		return getValue();
	}

	@Override
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

	@Override
	public boolean isWholeBucketScanned() {
		return wholeBucket;
	}

	@Override
	public int getBucketSize() {
		return bucket.length;
	}

	@Override
	public double getMinimum() {
		return multiplier * minimum.doubleValue();
	}

	@Override
	public double getMaximum() {
		return multiplier * maximum.doubleValue();
	}

	@Override
	public double getValue() {
		return Math.sqrt(multiplier * sum.doubleValue());
	}

	private void addScaledSample(long sample) {
		final long oldestSample = bucket[bucketPosition];
		bucket[bucketPosition] = sample;
		sum.subtract(oldestSample);
		sum.add(sample);
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
		} else {
			bucketPosition++;
		}
	}

}
