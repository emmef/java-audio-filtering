package org.emmef.audio.noisereduction;

import org.emmef.audio.noisedetection.NrMeasurementSettings;

public class RatedTimings implements Timings {
	public final Timings timings;
	public final long sampleRate;
	
	public RatedTimings(Timings defaultTimings, long sampleRate) {
		if (defaultTimings == null) {
			throw new NullPointerException("defaultTimings");
		}
		if (sampleRate < 1e-100) {
			throw new IllegalArgumentException("Samplerate must be positive");
		}
		this.timings = defaultTimings;
		this.sampleRate = sampleRate;
	}

	public final double getAttackTime(double lowestFrequencyInBand) {
		return timings.getAttackTime(lowestFrequencyInBand);
	}

	public final double getMeasurementTime(double lowestFrequencyInBand) {
		return timings.getMeasurementTime(lowestFrequencyInBand);
	}

	public final double getReleaseTime(double lowestFrequencyInBand) {
		return timings.getReleaseTime(lowestFrequencyInBand);
	}
	
	public final double getEffectiveMeasurementTime(NrMeasurementSettings settings, double lowestFrequencyInBand) {
		return timings.getEffectiveMeasurementTime(settings, lowestFrequencyInBand);
	}
	
	public final double getLowestFrequency() {
		return timings.getLowestFrequency();
	}

	public final int getAttackSamples(double lowestFrequencyInBand) {
		return getSamples(timings.getAttackTime(lowestFrequencyInBand));
	}

	public final int getMeasurementSamples(double lowestFrequencyInBand) {
		return getSamples(timings.getMeasurementTime(lowestFrequencyInBand));
	}

	public final int getReleaseSamples(double lowestFrequencyInBand) {
		return getSamples(timings.getReleaseTime(lowestFrequencyInBand));
	}
	
	public final int getEffectiveMeasurementSamples(NrMeasurementSettings settings, double lowestFrequencyInBand) {
		return getSamples(timings.getEffectiveMeasurementTime(settings, lowestFrequencyInBand));
	}

	public final int getSamples(final double time) {
		return Math.max(3, (int)(0.5 + sampleRate * time));
	}
}
