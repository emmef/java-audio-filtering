package org.emmef.audio.noisereduction;

import org.emmef.audio.noisedetection.NrMeasurementSettings;

public interface Timings {
	
	double getLowestFrequency();

	double getMeasurementTime(double lowestFrequencyInBand);

	double getAttackTime(double lowestFrequencyInBand);

	double getReleaseTime(double lowestFrequencyInBand);

	double getEffectiveMeasurementTime(NrMeasurementSettings settings, double lowestFrequencyInBand);
}
