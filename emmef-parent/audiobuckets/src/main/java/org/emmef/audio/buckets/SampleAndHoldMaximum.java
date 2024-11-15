package org.emmef.audio.buckets;

public class SampleAndHoldMaximum {
	private int holdCount;
	private int countDown = 0;
	private double value = 0.0;

	public SampleAndHoldMaximum(int holdCount) {
		this.holdCount = holdCount;
	}

	public double addSampleGetValue(double sample) {
		if (sample > value) {
			value = sample;
			countDown = holdCount;
		} else if (countDown > 0) {
			countDown--;
		} else {
			value = sample;
		}
		return value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void setHoldCount(int holdCount) {
		this.holdCount = holdCount;
	}

	public int getHoldCount() {
		return holdCount;
	}
}
