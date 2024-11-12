package org.emmef.audio.filter.iir.butterworth;

import org.emmef.audio.filter.Filter;


public class Butterworth {
	public static Filter create(double frequency, int order, PassType type) {
		if (order < 1) {
			throw new IllegalArgumentException("Filter order must 1 or greater");
		}
		if (frequency > 0.5) {
			throw new IllegalArgumentException("Frequency must be between 0 and 0.5 (nycquist) frequency");
		}
		switch(order) {
		case 1:
			return createFirstOrder(frequency, type);
		case 2:
			return createSecondOrder(frequency, type);
		case 3:
			return createThirdOrder(frequency, type);
		case 4:
			return createFourthOrder(frequency, type);
		default:
			return new ButterworthNFilter.Factory(order, type, frequency).create();
		}
	}
	
	public static Filter createFirstOrder(double frequency, PassType type) {
		final double[] c;
		final double[] d;
		if (type == PassType.LOW_PASS) {
			c = Coefficients.lowPassButterworthCcoefficients(1, frequency);
			d = Coefficients.lowPassButterworthDcoefficients(1, frequency);
		}
		else {
			c = Coefficients.highPassButterworthCcoefficients(1, frequency);
			d = Coefficients.highPassButterworthDcoefficients(1, frequency);
		}
		
		return new FirstOrderFilter(c, d);
	}
	
	public static Filter createSecondOrder(double frequency, PassType type ) {
		final double[] c;
		final double[] d;
		if (type == PassType.LOW_PASS) {
			c = Coefficients.lowPassButterworthCcoefficients(2, frequency);
			d = Coefficients.lowPassButterworthDcoefficients(2, frequency);
		}
		else {
			c = Coefficients.highPassButterworthCcoefficients(2, frequency);
			d = Coefficients.highPassButterworthDcoefficients(2, frequency);
		}
		
		return new SecondOrderFilter(d, c);
	}
	
	public static Filter createThirdOrder(double frequency, PassType type) {
		final double[] c;
		final double[] d;
		if (type == PassType.LOW_PASS) {
			c = Coefficients.lowPassButterworthCcoefficients(3, frequency);
			d = Coefficients.lowPassButterworthDcoefficients(3, frequency);
		}
		else {
			c = Coefficients.highPassButterworthCcoefficients(3, frequency);
			d = Coefficients.highPassButterworthDcoefficients(3, frequency);
		}
		
		return new ThirdOrderFilter(d, c);
	}
	
	public static Filter createFourthOrder(double frequency, PassType type) {
		final double[] c;
		final double[] d;
		if (type == PassType.LOW_PASS) {
			c = Coefficients.lowPassButterworthCcoefficients(4, frequency);
			d = Coefficients.lowPassButterworthDcoefficients(4, frequency);
		}
		else {
			c = Coefficients.highPassButterworthCcoefficients(4, frequency);
			d = Coefficients.highPassButterworthDcoefficients(4, frequency);
		}
		
		return new FourthOrderFilter(c, d);
	}
	
	public static void main(String[] args) {
		final int cutOffCycles = 400;
		final int generateCycles = 40;

		final int iterations = 50;
		final int maxTime = generateCycles * (2 + Math.max(generateCycles, cutOffCycles) * iterations / generateCycles);
		
		final double fCutoff = 1.0 / cutOffCycles;
		final double preWarped = 2.0 * Math.tan(Math.PI * fCutoff / 2) / Math.PI;
		final ButterworthNFilter filter = new ButterworthNFilter.Factory(1, PassType.LOW_PASS, fCutoff).create();
		
		System.out.printf("f=%1.5f; warped=%1.5f\n", fCutoff, preWarped);
		final double xFactor = 2.0 * Math.PI / generateCycles; 
		
		int t = 0;
		for (int i = 0; i < maxTime; i++) {
			final double x = Math.cos(xFactor * t);
			filter.filter(x);
			if (t < generateCycles - 1) {
				t++;
			}
			else {
				t = 0;
			}
		}
		double max = 0;
		for (int i = 0; i < generateCycles; i++) {
			final double x = Math.cos(xFactor * t);
			final double out1 = Math.abs(filter.filter(x));
			if (out1 > max) {
				max = out1;
			}
			if (t < generateCycles - 1) {
				t++;
			}
			else {
				t = 0;
			}
		}
		System.out.printf("Amplitude of filter is %2.8f or %1.1g\n", max, 1.0 / max);
		filter.reset();
		int i = 0;
		filter.filter(1.0);
		double y = 0;
		for (i = 1; i <= cutOffCycles; i++) {
			y = filter.filter(0.0);
		}
		final int exponent = -2;
		double threshold = Math.exp(exponent * 2.0 * Math.PI);
		while (Math.abs(y) > threshold) {
			y++;
			y = filter.filter(0.0);
			i++;
		}
		System.out.println(Math.exp(- 2.0 * Math.PI));
		System.out.println("Time to reach threshold of e^" + exponent + "(" + threshold + ") was " + i + " periods; " + (1.0 * i / cutOffCycles) + " cutoff cycles");
//		for (int i = 0; i < 10000; i++) {
//			out = filter.filter(1);
//			final double err = Math.abs(out - previous) / (Math.abs(out) + Math.abs(previous));
//			if (err > 0.00001) {
//				previous = out;
//				System.out.println(out);
//			}
//		}
	}
}
