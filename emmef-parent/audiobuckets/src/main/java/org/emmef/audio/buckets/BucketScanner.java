package org.emmef.audio.buckets;

import lombok.RequiredArgsConstructor;

public class BucketScanner {
	public static final long MAX_SAMPLE_RATE = 192000;
	public static final double MAX_WINDOW_SECONDS = 1.0;
	public static final double MAX_WINDOW_SAMPLES = Math.round(0.5 + MAX_WINDOW_SECONDS * MAX_SAMPLE_RATE);
	public static final double SCALE = Long.MAX_VALUE / MAX_WINDOW_SAMPLES;
	public static final LongInteger ZERO = new LongInteger(1);

	private final long[] bucket;
	private long sampleNumber = 0;
	private double minimum;
	private double maximum;
	private double sum;
	private final Window window[];

	public BucketScanner(double sampleRate, double bucketSeconds, int windows) {
		long bucketSamples = Math.round(bucketSeconds * sampleRate);
		if (bucketSamples < 1) {
			throw new IllegalArgumentException("Invalid bucket size: combination of sample rate (" + sampleRate + ") and seconds (" + bucketSeconds + ") yields zero samples");
		} else if (bucketSamples > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Invalid bucket size: combination of sample rate (" + sampleRate + ") and seconds (" + bucketSeconds + ") is too large:" + bucketSamples);
		}
		int bucketSize = (int) bucketSamples;
		this.bucket = new long[bucketSize];
		this.window = new Window[1];
		this.window[0] = new Window(bucketSize);
		reset();
	}

	public BucketScanner(double sampleRate, double bucketSeconds) {
		this(sampleRate, bucketSeconds, 1);
	}

	public final void addSample(double sample) {
		int bucketPosition = (int) (sampleNumber % bucket.length);
		long quantizedSample = Math.round(sample * sample * SCALE);

		sum = this.window[0].addAndGet(bucketPosition, quantizedSample);
		for (int i = 1; i < window.length; i++) {
			sum = Math.max(sum, this.window[i].addAndGet(bucketPosition, quantizedSample));
		}

		this.bucket[bucketPosition] = quantizedSample;
		sampleNumber++;

		if (isWholeBucketScanned()) {
			minimum = Math.min(minimum, sum);
			maximum = Math.max(maximum, sum);
		}
	}

	public void reset() {
		sampleNumber = 0;
		sum = 0;
		minimum = Double.MAX_VALUE;
		maximum = Double.MIN_VALUE;
		for (int i = 0; i < bucket.length; i++) {
			bucket[i] = 0;
		}
	}

	public boolean isWholeBucketScanned() {
		return sampleNumber >= bucket.length;
	}

	public int getBucketSize() {
		return bucket.length;
	}

	public double getMinimum() {
		return minimum;
	}

	public double getMaximum() {
		return maximum;
	}

	public double getMeanSquared() {
		return sum;
	}

	public double getRootMeanSquared() {
		return Math.sqrt(getMeanSquared());
	}

	public double getValue() {
		return getRootMeanSquared();
	}

	private class Window {
		private final int samples;
		private final double scale;
		private long sum;

		Window(int windowSize) {
			this.samples = Math.min(windowSize, bucket.length);
			final double weight = Math.pow(1.0 * this.samples / bucket.length, 0.25);
			this.scale = weight / (SCALE * this.samples);
			this.sum = 0;
		}

		double addAndGet(int bucketPosition, long newValue) {
			int oldestSamplePosition = (bucketPosition + bucket.length - this.samples) % bucket.length;
			this.sum -= bucket[oldestSamplePosition];
			this.sum += newValue;
			return this.scale * this.sum;
		}

		void reset() {
			this.sum = 0;
		}
	}
}
