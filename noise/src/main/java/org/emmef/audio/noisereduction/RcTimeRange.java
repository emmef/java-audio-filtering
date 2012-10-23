package org.emmef.audio.noisereduction;

public class RcTimeRange extends RcTimeRangeDef {
	public final double highestFrequency;
	public final double sampleRate;

	public RcTimeRange(double minimumRc, double maximumRc, double quickMeasurementPeriods, double sampleRate) {
		super(minimumRc, maximumRc, quickMeasurementPeriods);
		this.sampleRate = sampleRate;
		this.highestFrequency = 0.5 * sampleRate;
	}

	public RcTimeRange(double minimumRc, double maximumRc, double quickMeasurementPeriods, double lowestFreq, double sampleRate) {
		super(minimumRc, maximumRc, quickMeasurementPeriods, lowestFreq);
		this.sampleRate = sampleRate;
		this.highestFrequency = 0.5 * sampleRate;
	}
	
	public double getRcTimeForFrequency(double f) {
		double frequency = Math.min(Math.max(lowestFrequency, f), highestFrequency);
		double relativeLogFrequency = (Math.log(frequency) - Math.log(lowestFrequency)) / (Math.log(highestFrequency) - Math.log(lowestFrequency));
		double rcTime = maximumRc * Math.exp(relativeLogFrequency * Math.log(minimumRc / maximumRc));
		
		return rcTime;
	}
	
	public double getQuickMeasureTimeForFrequency(double f) {
		return quickMeasurePeriods / f; 
	}
	
	public double getQuickDetectionTime(double f) {
		return Math.min(getRcTimeForFrequency(f), getQuickMeasureTimeForFrequency(f));
	}
	
	public int getQuickDetectionSamples(double f) {
		return (int)Math.round(getQuickDetectionTime(f) * sampleRate);
	}
	
	public int getRcSamplesForFrequency(double f) {
		return (int)(0.5 + getRcTimeForFrequency(f) * sampleRate);
	}
	
	
	public int getSamplesForMaximumRc() {
		return getSamplesForMaximumRc(sampleRate);
	}
	
	public int getSamplesForQuickMeasurement(double f, double periods) {
		return (int)(0.5 + periods * sampleRate / f);
	}
	
	public int getSamplesForQuickMeasurement(double f) {
		return (int)(0.5 + quickMeasurePeriods * sampleRate / f);
	}
}