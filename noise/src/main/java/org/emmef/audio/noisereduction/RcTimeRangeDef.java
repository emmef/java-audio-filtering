package org.emmef.audio.noisereduction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RcTimeRangeDef {
	public static final double RC_TIME_MINIMUM = 0.001;
	public static final double RC_TIME_MAXIMUM = 0.3;
	
	public static final double PERIODS_MINIMUM = 1.0;
	public static final double PERIODS_MAXIMUM = 2000.0;
	
	public final double minimumRc;
	public final double maximumRc;
	public final double lowestFrequency;
	public final double quickMeasurePeriods;

	protected RcTimeRangeDef(double minimumRc, double maximumRc, double quickMeasurePeriods, double lowestFrequency) {
		List<Double> values = new ArrayList<Double>();
		values.add(minimumRc);
		values.add(maximumRc);
		values.add(quickMeasurePeriods);
		Collections.sort(values);
		this.minimumRc = values.get(0);
		this.maximumRc = values.get(1);
		this.quickMeasurePeriods = values.get(2);
		if (minimumRc < RC_TIME_MINIMUM || minimumRc > RC_TIME_MAXIMUM) {
			throw new IllegalArgumentException("Minimum rc time (highest frequencies) must be between " + RC_TIME_MINIMUM + " and " + RC_TIME_MAXIMUM);
		}
		if (maximumRc < RC_TIME_MINIMUM || maximumRc > RC_TIME_MAXIMUM) {
			throw new IllegalArgumentException("Maximum rc time (highest frequencies) must be between " + RC_TIME_MINIMUM + " and " + RC_TIME_MAXIMUM);
		}
		if (quickMeasurePeriods < PERIODS_MINIMUM || quickMeasurePeriods > PERIODS_MAXIMUM) {
			throw new IllegalArgumentException("Quick measurement periods must be between " + PERIODS_MINIMUM + " and " + PERIODS_MAXIMUM);
		}
		this.lowestFrequency = lowestFrequency;
	}
	
	public RcTimeRangeDef(double minimumRc, double maximumRc, double quickMeasurementPeriods) {
		this(minimumRc, maximumRc, quickMeasurementPeriods, 1.0 / maximumRc);
	}
	
	@Override
	public String toString() {
		return String.format("Noise reduction follow speed: minimum=%1.3fs; maximum=%1.3fs; quick-measure-periods=%1.1f", minimumRc, maximumRc, quickMeasurePeriods);
	}
	
	public int getSamplesForMaximumRc(double sampleRate) {
		return (int)(0.5 + sampleRate * maximumRc);
	}
	
	public RcTimeRange withSampleRate(double sampleRate) {
		return new RcTimeRange(minimumRc, maximumRc, quickMeasurePeriods, lowestFrequency, sampleRate);
	}
}