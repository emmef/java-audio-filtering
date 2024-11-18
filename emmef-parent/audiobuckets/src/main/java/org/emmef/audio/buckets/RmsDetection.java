package org.emmef.audio.buckets;

import java.util.Arrays;

public class RmsDetection {
	private static final double INPUT_SCALE = Math.pow(2, 32);
	private static final double OUTPUT_SCALE = 1.0 / INPUT_SCALE;
	private static final double WINDOW_PERCEIVED = 0.4;
	private static final double WINDOW_SHORT = 0.002;
	private static final double WINDOW_FACTOR = WINDOW_PERCEIVED / WINDOW_SHORT;
	private static final int STEPS = (int) ((Math.log(WINDOW_FACTOR)) / Math.log(Math.sqrt(2.0)));
	private static final double STEP_POWER_FACTOR = 1.0 / STEPS;

	private final Integration.Factors attackFactors;
	private final Integration.DoubleIntegrator attackIntegrator;
	private final Integration.Factors releaseFactors;
	private final SampleMaximumSmoothRelease releaseIntegrator;
	private final SampleAndHoldMaximumSmoothRelease sampleAndHold;
	private long buffer[] = null;
	private Entry entries[];
	private int writeAt = 0;
	private int processedSamples = 0;

	private final Integration.Factors fastReleaseFactors;

	public RmsDetection(long sampleRate, double attack, double fastRelease, double release) {
		attackFactors = new Integration.Factors();
		attackIntegrator = new Integration.DoubleIntegrator(attackFactors);
		releaseFactors = new Integration.Factors();
		releaseIntegrator = new SampleMaximumSmoothRelease(releaseFactors);
		fastReleaseFactors = new Integration.Factors();
		sampleAndHold = new SampleAndHoldMaximumSmoothRelease(getHoldCount(), fastReleaseFactors);
		entries = new Entry[STEPS + 1];
		for (int i = 0; i < entries.length; i++) {
			double relativeWindowSize = WINDOW_SHORT * Math.pow(WINDOW_FACTOR, STEP_POWER_FACTOR * i) / WINDOW_PERCEIVED;
			double squaredScale = Math.sqrt(relativeWindowSize);
			entries[i] = new Entry(squaredScale);
		}

		reconfigure(sampleRate, attack, fastRelease, release);
	}

	public double addSample(double sample) {
		long sumValue = Math.round(INPUT_SCALE * sample * sample);

		double max = 0.0625 * sampleAndHold.addSampleGetValue(sumValue);
		for (int i = 0; i < entries.length; i++) {
			max = Math.max(max, entries[i].addAndGet(sumValue));
		}
		double detection = Math.sqrt(OUTPUT_SCALE * max);
		buffer[writeAt] = sumValue;
		writeAt = (writeAt + 1) % buffer.length;
		processedSamples++;

		double smoothAttack = attackIntegrator.integrate(detection);
		return releaseIntegrator.addSampleGetValue(smoothAttack);
	}

	public double getValue() {
		if (buffer != null && processedSamples < buffer.length) {
			double max = entries[0].getValue(processedSamples);
			for (int i = 1; i < entries.length; i++) {
				max = Math.max(max, entries[i].getValue(processedSamples));
			}
			return max;
		}
		return releaseIntegrator.getValue();
	}

	public void reconfigure(long sampleRate, double attack, double fastRelease, double release) {
		attackFactors.setCount(attack * sampleRate / Integration.DOUBLE_INTEGRATOR_PROLONGATION_FACTOR, 1.0);
		releaseFactors.setCount(release * sampleRate / Integration.DOUBLE_INTEGRATOR_PROLONGATION_FACTOR, 1.0);
		fastReleaseFactors.setCount(fastRelease * sampleRate / Integration.DOUBLE_INTEGRATOR_PROLONGATION_FACTOR, 1.0);

		sampleAndHold.setHoldCount(getHoldCount());
		sampleAndHold.setValue(0);

		final int windowSize = (int) Math.round(0.5 + WINDOW_PERCEIVED * sampleRate);
		if (buffer == null || windowSize != buffer.length) {
			buffer = new long[windowSize];
		} else {
			Arrays.fill(buffer, 0);
		}
		writeAt = 0;
		processedSamples = 0;

		for (int i = 0; i <= STEPS; i++) {
			double logFactor = Math.pow(WINDOW_FACTOR, STEP_POWER_FACTOR * i);
			int bucketSize = (int) Math.round(0.5 + WINDOW_SHORT * logFactor * sampleRate);
			entries[i].reconfigure(bucketSize);
		}
	}

	int getHoldCount() {
		return (int) (3.0 * attackFactors.getCount() * Integration.DOUBLE_INTEGRATOR_PROLONGATION_FACTOR);
	}

	public int getProcessedSamples() {
		return processedSamples;
	}

	public boolean isFullFrameProcessed() {
		return buffer != null && processedSamples >= buffer.length;
	}

	public int windowSamples() {
		return buffer != null ? buffer.length : 0;
	}

	public void reset() {
		Arrays.fill(buffer, 0);
		for (int i = 0; i <= STEPS; i++) {
			entries[i].reset();
		}
	}

	private final class Entry {
		SampleAndHoldMaximumSmoothRelease sampleAndHold;
		int readPosition = 0;
		int bucketSize = 0;
		long sum = 0;
		double usedScale = 1.0;
		double scale;

		Entry(double scale) {
			this.readPosition = 0;
			this.sampleAndHold = new SampleAndHoldMaximumSmoothRelease(getHoldCount(), fastReleaseFactors);
			this.scale = scale;
		}

		void reconfigure(int bucketSize) {
			this.bucketSize = bucketSize;
			this.sampleAndHold.setHoldCount(getHoldCount());
			this.sampleAndHold.setValue(0);
			this.usedScale = scale / bucketSize;
			this.readPosition = (buffer.length - bucketSize) % buffer.length;
			this.sum = 0;
		}

		double addAndGet(long input) {
			sum -= buffer[readPosition];
			sum += input;
			readPosition = (readPosition + 1) % buffer.length;
			return sampleAndHold.addSampleGetValue(sum * usedScale);
		}

		double getValue(int samplesCount) {
			if (samplesCount >= bucketSize) {
				return sampleAndHold.getValue();
			}
			else if (samplesCount > 0) {
				return (sum * usedScale) * bucketSize / Math.min(samplesCount, bucketSize);
			}
			return 0;
		}

		void reset() {
			this.sum = 0;
			this.readPosition = (buffer.length - bucketSize) % buffer.length;
		}
	}
}