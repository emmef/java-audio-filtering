package org.emmef.audio.filter.tools;

import org.emmef.audio.filter.Filter;

public class Integrator implements Filter {
	public static final int MINIMUM_INTEGRATION_SAMPLES = 10;
	
	public static final double characteristicDecayPerSample(double characteristicSamples) {
		if (characteristicSamples < 1e-2) {
			return 0.0;
		}

		return Math.exp(-1.0 / characteristicSamples);
	}

	public static final double characteristicSampleMultiplication(double characteristicSamples) {
		if (characteristicSamples < 1e-2) {
			return 1.0;
		}

		return 1.0 - Math.exp(-1.0 / characteristicSamples);
	}

	public static final double samples(int sampleRate, double time) {
		return time * sampleRate;
	}
	
	public static Integrator of(int sampleRate, double characteristicSeconds) {
		return of(sampleRate, characteristicSeconds, 0.0);
	}
	
	public static Integrator of(int sampleRate, double characteristicSeconds, double startValue) {
		if (sampleRate < 1) {
			throw new IllegalArgumentException("Samplerate must be at least 1");
		}
		double characteristicSamples = characteristicSeconds * sampleRate;
		if (characteristicSamples < MINIMUM_INTEGRATION_SAMPLES) {
			throw new IllegalArgumentException(
					"Samplerate (" + sampleRate + 
					") and characteristic time (" + characteristicSeconds + 
					") yield " + characteristicSamples + 
					" characteristic samples, which is below the minimum of " + 
					MINIMUM_INTEGRATION_SAMPLES);
		}
		
		return new Integrator(
				characteristicDecayPerSample(characteristicSamples), 
				characteristicSampleMultiplication(characteristicSamples),
				characteristicSamples, startValue);
	}
	
	public final double integrationDecayFactor;
	public final double integrationSampleFactor;
	public final double integrationSamples;
	public final double startValue;
	private double memory = 0.0;
	
	private Integrator(double integrationDecayFactor, double integrationSampleFactor, double integrationSamples, double startValue) {
		this.integrationDecayFactor = integrationDecayFactor;
		this.integrationSampleFactor = integrationSampleFactor;
		this.integrationSamples = integrationSamples;
		this.startValue = startValue;
		this.memory = startValue;
	}
	
	public Integrator copy() {
		return copyWithStartValue(0.0);
	}
	
	public Integrator copyWithStartValue(double startValue) {
		return new Integrator(integrationDecayFactor, integrationSampleFactor, integrationSamples, startValue);
	}

	@Override
	public void reset() {
		memory = startValue;
	}
	
	@Override
	public double filter(double newSample) {
		memory *= integrationDecayFactor;
		memory += integrationSampleFactor * newSample;
		return memory;
	}
	
	
	
	public double peekMemory() {
		return memory;
	}
	
	@Override
	public String toString() {
		return String.format("%s(samples=%3.1e decay=%1.8e; mul=%3.1e; starts=%3.1e", 
				getClass().getSimpleName(), 
				integrationSamples, integrationDecayFactor, integrationSampleFactor, startValue);
	}
}
