package org.emmef.audio.noisereduction;

import java.util.Formatter;
import java.util.SortedSet;
import java.util.TreeSet;

public class Terts {
		public static final double TERTZ = Math.pow(2.0, 1.0 / 3.0);
	
	public static void main(String[] args) {
		double startingPoint = 4500;
		if (args.length > 1) {
			try {
				startingPoint = Double.parseDouble(args[0]);
			}
			catch (NumberFormatException e) {
				// 
			}
		}
		SortedSet<Double> values = new TreeSet<Double>();
		values.add(startingPoint);
		double walk = startingPoint / TERTZ;
		while (walk > 20.0) {
			values.add(walk);
			walk /= TERTZ;
		}
		walk = startingPoint * TERTZ;
		while (walk < 20000) {
			values.add(walk);
			walk *= TERTZ;
		}
		StringBuilder text = new StringBuilder();
		Formatter f = new Formatter(text);
		text.append("Frequencies:");
		for (final Double value : values) {
			f.format(" %1.0f", value);
		}
		f.flush();
		System.out.println(text);
	}
}
