package org.emmef.audio.buckets;

public class SampleMaximumSmoothRelease {
	private Integration.Factors factors;
	private double value = 0.0;
	private double int1 = 0.0;

	public SampleMaximumSmoothRelease(Integration.Factors factors) {
		this.factors = factors;
	}

	public double addSampleGetValue(double sample) {
		if (sample > value) {
			value = sample;
			int1 = value;
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
}

