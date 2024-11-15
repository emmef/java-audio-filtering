package org.emmef.audio.buckets;

public class SampleAndHoldMaximumSmoothRelease {
	private int holdCount;
	private int countDown = 0;
	private Integration.Factors factors;
	private double value = 0.0;
	private double int1 = 0.0;

	public SampleAndHoldMaximumSmoothRelease(int holdCount, Integration.Factors factors) {
		this.holdCount = holdCount;
		this.factors = factors;
	}

	public double addSampleGetValue(double sample) {
		if (sample > value) {
			value = sample;
			countDown = holdCount;
			int1 = value;
		} else if (countDown > 0) {
			countDown--;
		} else {
			int1 = factors.integrated(int1, sample);
			value = factors.integrated(value, int1);
		}
		return value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
		this.int1 = value;
	}

	void setHoldCount(int holdCount) {
		this.holdCount = holdCount;
	}

	public int getHoldCount() {
		return holdCount;
	}
}

