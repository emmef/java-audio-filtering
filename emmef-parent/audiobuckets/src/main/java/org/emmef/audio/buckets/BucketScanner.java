package org.emmef.audio.buckets;

public class BucketScanner {
	public static final long MAX_SAMPLE_RATE = 192000;
	public static final double MAX_WINDOW_SECONDS = 1.0;
	public static final double MAX_WINDOW_SAMPLES = Math.round(0.5 + MAX_WINDOW_SECONDS * MAX_SAMPLE_RATE);
	public static final double SCALE = Long.MAX_VALUE / MAX_WINDOW_SAMPLES;
	public static final double PEAK_WINDOW_SECONDS = 0.001;
	public static final double MIN_REL_WINDOW_DISTANCE = Math.sqrt(2.0);

	private final long[] bucket;
	private long sampleNumber = 0;
	private double minimum;
	private double maximum;
	private double sum;
	private final Window window[];

	public BucketScanner(double sampleRate, double windowSeconds, boolean weighted) {
		int bucketSamples = getBucketSamples(sampleRate, windowSeconds);
		this.bucket = new long[bucketSamples];
		this.window = new Window[weighted ? getWindows(windowSeconds) : 1];
		this.window[0] = new Window(bucketSamples);

		int peakSamples = (int)Math.round(PEAK_WINDOW_SECONDS * sampleRate);
		final double sizeFactor = 1.0 * peakSamples / bucketSamples;
		for (int i = 1; i < window.length; i++) {
			double pow = (1.0 * i) / (window.length - 1.0);
			int windowSize = (int)Math.round(bucketSamples * Math.pow(sizeFactor, pow));
			this.window[i] = new Window(windowSize);
		}
		reset();
	}

	public BucketScanner(double sampleRate, double bucketSeconds) {
		this(sampleRate, bucketSeconds, true);
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
		for (int i = 0; i < window.length; i++) {
			window[i].reset();
		}
	}

	public boolean isWholeBucketScanned() {
		return sampleNumber >= bucket.length;
	}

	public int getBucketSize() {
		return bucket.length;
	}

	public double getMinimum() {
		return Math.sqrt(minimum);
	}

	public double getMaximum() {
		return Math.sqrt(maximum);
	}

	public double getMeanSquared() {
		return sum;
	}

	public double getValue() {
		return Math.sqrt(getMeanSquared());
	}

	private static int getBucketSamples(double sampleRate, double windowSeconds) {
		if (windowSeconds < PEAK_WINDOW_SECONDS) {
			throw new IllegalArgumentException("Window seconds (" + windowSeconds + ") smaller than peak-measurement window (" + PEAK_WINDOW_SECONDS + ").");
		}
		if (windowSeconds > MAX_WINDOW_SECONDS) {
			throw new IllegalArgumentException("Window seconds (" + windowSeconds + ") larger than maximum window size (" + MAX_WINDOW_SECONDS + ").");
		}
		long bucketSamples = Math.round(windowSeconds * sampleRate);
		if (bucketSamples < 1) {
			throw new IllegalArgumentException("Invalid bucket size: combination of sample rate (" + sampleRate + ") and seconds (" + windowSeconds + ") yields zero samples");
		}
		if (bucketSamples > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Invalid bucket size: combination of sample rate (" + sampleRate + ") and seconds (" + windowSeconds + ") is too large:" + bucketSamples);
		}

		return (int)bucketSamples;
	}

	private static int getWindows(double windowSeconds) {
		double ratio = windowSeconds / PEAK_WINDOW_SECONDS;
		if (ratio < MIN_REL_WINDOW_DISTANCE) {
			return 1;
		}
		return (int)(Math.log(windowSeconds / PEAK_WINDOW_SECONDS) / Math.log(MIN_REL_WINDOW_DISTANCE));
	}

	private class Window {
		private final int samples;
		private final double scale;
		private long sum;

		Window(int windowSize) {
			this.samples = Math.min(windowSize, bucket.length);
			/**
			 * ITU specifies a 0.25 law for the relative loudness as a function of the relative window size.
			 * But we're calculating in squares, so the actual weight after taking the square root
			 * is correct (and relative ordering is not affected).
			 */
			final double weight = Math.pow(1.0 * this.samples / bucket.length, 0.5);
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
