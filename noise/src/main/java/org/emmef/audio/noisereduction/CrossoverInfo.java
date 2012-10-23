package org.emmef.audio.noisereduction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.emmef.audio.noisereduction.BandSplitFilterSet.Direction;

public class CrossoverInfo {
	public final int filterOrder;
	public final Direction direction;
	public final List<Double> crossovers;

	public CrossoverInfo(Direction direction, int filterOrder, Collection<Double> crossovers) { 
		this.direction = direction;
		if (direction == null) {
			throw new NullPointerException("direction");
		}
		if (crossovers == null) {
			throw new NullPointerException("crossovers");
		}
		if (filterOrder < 1) {
			this.filterOrder = 1; 
		}
		else if (filterOrder > 4) {
			this.filterOrder = 4;
		}
		else {
			this.filterOrder = filterOrder;
		}
		SortedSet<Double> ordered = new TreeSet<Double>(crossovers);
		if (ordered.isEmpty()) {
			ordered.add(4500.0); // the original DNL frequency by Philips :-)  
		}
		List<Double> list = new ArrayList<Double>(ordered);
		this.crossovers = Collections.unmodifiableList(list);
	}
	
	@Override
	public String toString() {
		return "Crossover processing {direction=" + direction + "; filter-order=2x" + filterOrder + "; frequencies=" + crossovers + "}"; 
	}
	
	public final double getLowestCrossover() {
		return crossovers.get(0);
	}
	
	public final double getHighestCrossover() {
		return crossovers.get(crossovers.size() - 1);
	}
	
	public final int size() {
		return crossovers.size();
	}
	
	public final double get(int i) {
		return crossovers.get(i);
	}
}
