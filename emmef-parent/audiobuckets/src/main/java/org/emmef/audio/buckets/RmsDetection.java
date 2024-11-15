package org.emmef.audio.buckets;

import java.util.Arrays;

public class RmsDetection {
	private static final double SCALE = Math.pow(2, 32);
	private static final double WINDOW_PERCEIVED = 0.4;
	private static final double WINDOW_SHORT = 0.002;
	private static final double WINDOW_FACTOR = WINDOW_PERCEIVED / WINDOW_SHORT;
	private static final int STEPS = (int)((Math.log(WINDOW_FACTOR)) / Math.log(Math.sqrt(2.0)));
	private static final double STEP_POWER_FACTOR = 1.0 / STEPS;

	private final Integration.Factors attackFactors;
	private final Integration.DoubleIntegrator attackIntegrator;
	private final Integration.Factors releaseFactors;
	private final Integration.DoubleIntegrator releaseIntegrator;
	private final SampleAndHoldMaximum sampleAndHold;
	private long buffer[] = null;
	private Entry entries[];
	private int writeAt = 0;

	private final Integration.Factors fastReleaseFactors;

	public RmsDetection(int sampleRate, double attack, double hold, double release, double fastRelease) {
		attackFactors = new Integration.Factors();
		attackIntegrator = new Integration.DoubleIntegrator(attackFactors);
		releaseFactors = new Integration.Factors();
		releaseIntegrator = new Integration.DoubleIntegrator(releaseFactors);
		fastReleaseFactors = new Integration.Factors();
		sampleAndHold = new SampleAndHoldMaximum((int)Math.round(0.5 + hold * sampleRate));
		entries = new Entry[STEPS + 1];
		for (int i = 0; i <= STEPS; i++) {
			double logFactor = Math.pow(WINDOW_FACTOR, STEP_POWER_FACTOR * i);
			double scale = Math.sqrt(Math.sqrt(logFactor));
			entries[i] = new Entry(scale);
		}

		reconfigure(sampleRate, attack, hold, release, fastRelease);
	}

	public void reconfigure(int sampleRate, double attack, double hold, double release, double fastRelease) {
		attackFactors.setCount(attack * sampleRate, 1.0);
		releaseFactors.setCount(release * sampleRate, 1.0);
		fastReleaseFactors.setCount(fastRelease * sampleRate, 1.0);
		sampleAndHold.setHoldCount((int)Math.round(0.5 + hold * sampleRate));
		final int windowSize = (int) Math.round(0.5 + WINDOW_PERCEIVED * sampleRate);
		if (windowSize != buffer.length) {
			buffer = new long[windowSize];
		}
		else {
			Arrays.fill(buffer, 0);
		}
		writeAt = 0;

		for (int i = 0; i <= STEPS; i++) {
			double logFactor = Math.pow(WINDOW_FACTOR, STEP_POWER_FACTOR * i);
			int bucketSize = (int) Math.round(0.5 + WINDOW_SHORT * logFactor * sampleRate);
			entries[i].reconfigure(bucketSize);
		}
	}

	private final class Entry {
		SampleAndHoldMaximumSmoothRelease sampleAndHold;
		int readPosition = 0;
		long sum = 0;
		double usedScale = 1.0;
		double scale;

		Entry(double scale) {
			this.readPosition = 0;
			this.sampleAndHold = new SampleAndHoldMaximumSmoothRelease(0, fastReleaseFactors);
			this.scale = scale;
		}

		void reconfigure(int bucketSize) {
			this.sampleAndHold.setHoldCount(sampleAndHold.getHoldCount());
			this.usedScale = scale / bucketSize;
			this.readPosition = (buffer.length + 1 - bucketSize) % buffer.length;
			this.sum = 0;
		}
	}

}
