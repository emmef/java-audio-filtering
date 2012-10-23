package org.emmef.audio.filter.iir.butterworth;

import org.emmef.audio.filter.Filter;

public final class ThirdOrderFilter implements Filter {
	final double c0;
	final double c1;
	final double c2;
	final double c3;
	final double d1;
	final double d2;
	final double d3;
	double yN1 = 0.0;
	double yN2 = 0.0;
	double yN3 = 0.0;
	double xN1 = 0.0;
	double xN2 = 0.0;
	double xN3 = 0.0;

	ThirdOrderFilter(double[] d, double[] c) {
		c0 = c[0];
		c1 = c[1];
		c2 = c[2];
		c3 = c[3];
		d1 = d[1];
		d2 = d[2];
		d3 = d[3];
	}

	@Override
	public double filter(double xN) {
		final double yN = c0 * xN + c1 * xN1 + c2 * xN2 + c3 * xN3 - d1 * yN1 - d2 * yN2 - d3 * yN3;
		
		yN3 = yN2;
		yN2 = yN1;
		yN1 = yN;
		xN3 = xN2;
		xN2 = xN1;
		xN1 = xN;
		
		return yN;
	}

	@Override
	public void reset() {
		yN1 = yN2 = yN3 = 0.0;
		xN1 = xN2 = xN3 = 0.0;
	}
}