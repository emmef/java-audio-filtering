package org.emmef.audio.buckets;

public class SmoothRelease {
	private Integration.Factors factors1;
	private Integration.Factors factors2;
	private double value = 0.0;
	private double int1 = 0.0;

	public SmoothRelease(double firstIntegration, double secondIntegration) {
		this.factors1 = new Integration.Factors(firstIntegration);
		this.factors2 = new Integration.Factors(secondIntegration);
	}

	public double addSampleGetValue(double sample) {
		if (sample > value) {
			value = sample;
			int1 = value;
		} else {
			int1 = factors1.integrated(int1, sample);
			value = factors2.integrated(value, int1);
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

	void setFirstCount(double count) {
		factors1.setCount(count, 1.0);
	}
	void setSecondCount(double count) {
		factors2.setCount(count, 1.0);
	}
}

