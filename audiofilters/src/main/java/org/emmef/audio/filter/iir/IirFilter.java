package org.emmef.audio.filter.iir;

import org.emmef.audio.filter.Filter;

/**
* IIR Digital Filter.
* 
* <p>Copyright(c) 2010 Michel Fleur</p>
* 
* <p>An IIR filter is also known as a recursive digital filter because its output
* is a function of previous outputs as well as the input. If x[n] represents the
* n<sup>th</sup> input to the filter and y[n] is the n<sup>th</sup> output of the filter then a
* general iir filter is implemented as follows:</p>
* <pre>
*  y[n] = c0*x[n] + c1*x[n-1] + ... + cM*x[n-M] - ( d1*y[n-1] + d2*y[n-2] + ... + dN*y[n-N])</pre>
* <p>This means that the nth output is a linear function of the nth input, the
* previous M inputs, and the previous N outputs. The c and d coefficients are
* calculated to give the filter a specific frequency response. The number of
* coefficients, M and N, will vary depending on the type of filter.</p>
* 
* <p>This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.</p>
* <p>This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.</p>
* <p>A copy of the GNU General Public License is available on the internet at:</p>
* <p><a href="http://www.gnu.org/copyleft/gpl.html">http://www.gnu.org/copyleft/gpl.html</a></p>
* <p>or you can write to:</p>
* <p>The Free Software Foundation, Inc.<br />
*   675 Mass Ave<br />
*   Cambridge, MA 02139, USA</p>
* <p>You can contact Michel Fleur by e-mail:<br />
* <tt>the(dot)emmef(at)gmail(dot)com</tt></p>
*/
public class IirFilter implements Filter {
	private final double[] c;
	private final double[] d;
	private final double[] x;
	private final double[] y;
	private final int xPosMax;
	private final int xHistory;
	private final int yPosMax;
	private final int yHistory;
	private int xPos;
	private int yPos;

	public IirFilter(double[] c, double[] d) {
		if (c == null) {
			throw new NullPointerException("c");
		}
		if (d == null) {
			throw new NullPointerException("d");
		}
		this.c = c;
		this.d = d;
		this.x = new double[c.length - 2];
		this.y = new double[d.length - 1];
		this.xHistory = x.length;
		this.xPosMax = x.length - 1;
		this.yHistory = y.length;
		this.yPosMax = y.length - 1;
	}
	
	public void reset() {
		for (int i = 0; i < c.length; i++) {
			c[i] = 0.0;
		}
		for (int i = 0; i < d.length; i++) {
			d[i] = 0.0;
		}
	}
	
	public double filter(double xN) {
		// y[n] = c0*x[n] + c1*x[n-1] + ... + cM*x[n-M] - ( d1*y[n-1] + d2*y[n-2] + ... + dN*y[n-N])</pre>
		
		double yN = c[0] * xN;

		
		int nC;
		int nX;
		
		for (nC = 1, nX = xPos; nX < xHistory; nX++, nC++) {
			yN += c[nC] * x[nX];
		}
		for (nX = 0; nX < xPos; nX++, nC++) {
			yN += c[nC] * x[nX];
		}
		// Move circular history pointer
		if (xPos > 0) {
			xPos--;
		}
		else {
			xPos = xPosMax;
		}
		x[xPos] = xN;
		
		int nD = 1;
		int nY = yPos;
		for (nD = 0, nY = yPos; nY < yHistory; nY++, nD++) {
			yN -= d[nD] * y[nY];
		}
		for (nY = 0; nY < yPos; nY++, nD++) {
			yN -= d[nD] * y[nY];
		}
		if (yPos > 0) {
			yPos--;
		}
		else {
			yPos = yPosMax;
		}
		y[yPos] = yN;

		return yN;
	}
}
