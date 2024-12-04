package org.emmef.audio.noisereduction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.emmef.audio.noisedetection.NrMeasurementSettings;

public class DefaultTimings implements Timings {
	public final List<Double> timeMeasurement;
	public final List<Double> timeAttack;
	public final List<Double> timeRelease;
	public final double largestTime;
	public final double lowestFrequency;
	public final double highestFrequency;
	private double logLowestFrequency;
	private double logFrequencyRange;

	public DefaultTimings(CrossoverInfo crossovers, List<Double> timeMeasurement, List<Double> timeAttack, List<Double> timeRelease) {
		if (crossovers == null) {
			throw new NullPointerException("crossovers");
		}
		if (timeMeasurement == null) {
			throw new NullPointerException("timeMeasurement");
		}
		if (timeAttack == null) {
			throw new NullPointerException("timeAttack");
		}
		if (timeRelease == null) {
			throw new NullPointerException("timeRelease");
		}
		if (timeMeasurement.isEmpty()) {
			throw new IllegalArgumentException("Need at least one measurement time");
		}
		if (timeAttack.isEmpty()) {
			throw new IllegalArgumentException("Need at least one attack time");
		}
		if (timeRelease.isEmpty()) {
			throw new IllegalArgumentException("Need at least one release time");
		}
		this.timeMeasurement = createReverseOrderedList(timeMeasurement);
		this.timeAttack = createReverseOrderedList(timeAttack);
		this.timeRelease = createReverseOrderedList(timeRelease);
		this.largestTime = Math.max(timeMeasurement.get(0), Math.max(timeAttack.get(0), timeRelease.get(0)));
		this.lowestFrequency = Math.min(20.0, 0.5 * crossovers.getLowestCrossover());
		this.highestFrequency = crossovers.getHighestCrossover();
		this.logLowestFrequency = Math.log(lowestFrequency);
		this.logFrequencyRange = Math.log(highestFrequency) - logLowestFrequency;
	}
	
	public final double getMeasurementTime(double lowestFrequencyInBand) {
		return getMeasurementTime(timeMeasurement, lowestFrequencyInBand);
	}
	
	public final double getAttackTime(double lowestFrequencyInBand) {
		return getMeasurementTime(timeAttack, lowestFrequencyInBand);
	}
	
	public final double getReleaseTime(double lowestFrequencyInBand) {
		return getMeasurementTime(timeRelease, lowestFrequencyInBand);
	}
	
	public double getLowestFrequency() {
		return lowestFrequency;
	}
	
	public final Timings withSampleRate(long sampleRate) {
		return new RatedTimings(this, sampleRate);
	}
	
	public final double getEffectiveMeasurementTime(NrMeasurementSettings settings, double lowestFrequencyInBand) {
		switch (settings.measureIrregularNoise) {
		case 1:
			return settings.rmsWin;
		case 2:
			return getMeasurementTime(lowestFrequencyInBand);
		case 3:
			return Math.min(settings.rmsWin, getMeasurementTime(lowestFrequencyInBand));
		default: 
			return Math.sqrt(settings.rmsWin * getMeasurementTime(lowestFrequencyInBand));
		}
	}
	
	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		Formatter f = new Formatter(text);
		f.format("Timings(range=[%1.0f-%1.0f]Hz", 
				lowestFrequency, highestFrequency);
		addTimings(f, timeMeasurement, "level-measurement");
		addTimings(f, timeAttack, "attack");
		addTimings(f, timeRelease, "release");
		f.flush();
		text.append(")");
		return text.toString();
	}
	
	private void addTimings(Formatter f, List<Double> range, String description) {
		if (range.size() == 1) {
			f.format("; %s=%1.3fs", description, range.get(0));
		}
		else {
			f.format("; %s=[%1.3f-%1.3f]s", description, range.get(1), range.get(0));
		}
	}

	private double getMeasurementTime(List<Double> range, double lowestFrequencyInBand) {
		double frequency = Math.min(Math.max(lowestFrequency, lowestFrequencyInBand), highestFrequency);
		double relativeLogFrequency = (Math.log(frequency) - logLowestFrequency) / logFrequencyRange;
		return range.get(0) * Math.exp(relativeLogFrequency * Math.log(range.get(1) / range.get(0)));
	}

	private static List<Double> createReverseOrderedList(Collection<Double> input) {
		SortedSet<Double> set = new TreeSet<Double>(input);
		return Collections.unmodifiableList(Arrays.asList(set.last(), set.first()));
	}
}
