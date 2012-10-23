package org.emmef.audio.filter.iir.butterworth;

import org.emmef.audio.filter.Filter;

public final class FirstOrderFilter implements Filter {
	final double c0;
	final double c1;
	final double d1;
	double yN1 = 0.0;
	double xN1 = 0.0;

	FirstOrderFilter(double[] c, double[] d) {
		c0 = c[0];
		c1 = c[1];
		d1 = d[1];
	}

	@Override
	public double filter(double xN) {
		final double yN = c0 * xN + c1 * xN1 - d1 * yN1;
		
		yN1 = yN;
		xN1 = xN;
		
		return yN;
	}

	@Override
	public void reset() {
		yN1 = 0.0;
		xN1 = 0.0;
	}
}