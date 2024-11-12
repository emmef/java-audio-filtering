package org.emmef.audio.filter.iir.butterworth;
/**
 * IIR Digital Filter Functions.
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
 * coefficients, M and N, will vary depending on the type of filter. There are
 * many different kinds of iir filters and many different ways to calculate the
 * coefficients. Listed below are filter types (currently only Butterworth
 * filters) and the functions that can be used to calculate the c and d
 * coefficients for lowpass, highpass, bandpass, and bandstop implementations of
 * the filter.</p>
 * 
 * <p>This code was originally written in C and translated to Java by Michel Fleur.
 * The original source files, including documentation, are included in the
 * project.</p>
 * 
 * <p>Recursive digital filter functions Copyright (C) 2007 Exstrom
 * Laboratories LLC</p>
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
 * 
 * <p>You can contact Exstrom Laboratories LLC via Email at:</p>
 * <p><tt>stefan(AT)exstrom(DOT)com</tt></p>
 * <p>or you can write to:</p>
 * <p>Exstrom Laboratories LLC<br />P.O. Box 7651 Longmont<br />CO 80501, USA</p>
 * 
 * <p>Translated into Java by Michel Fleur: <tt>the(dot)emmef(at)gmail(dot)com</tt></p>
 */
public class Coefficients {
	/**
	 * Multiplies a series of binomials together and returns the
	 * coefficients of the resulting polynomial.
	 * 
	 * <p>
	 * The multiplication has the following form:
	 * </p>
	 * 
	 * <pre>
	 *  (x+p[0])*(x+p[1])*...*(x+p[n-1])
	 * </pre>
	 * <p>
	 * The p[i] coefficients are assumed to be complex and are passed to the
	 * function as an array of doubles of length 2n.<br />
	 * The resulting polynomial has the following form:
	 * </p>
	 * 
	 * <pre>
	 *   x^n + a[0]*x^n-1 + a[1]*x^n-2 + ... +a[n-2]*x + a[n-1]
	 * </pre>
	 * <p>
	 * The a[i] coefficients can in general be complex but should in most cases
	 * turn out to be real. The a[i] coefficients are returned by the function
	 * as an array of doubles of length 2n.
	 * </p>
	 * 
	 * @param p
	 *            An array of doubles where p[2i] (i=0...n-1) is
	 *            assumed to be the real part of the coefficient of the
	 *            i<sup>th</sup> binomial and p[2i+1] is assumed to be the
	 *            imaginary part. The size of the array must be <code>2n</code>.
	 *            
	 * @return an array, containing the complex coefficients of the resulting polynomial
	 */
	public static final double[] binomialMultiply(double[] p) {
		if (p == null) {
			throw new NullPointerException("p");
		}
		if (p.length < 2) {
			throw new IllegalArgumentException("Input must have at least 2 elements");
		}
		if (p.length % 2 != 0) {
			throw new IllegalArgumentException("Input coefficents should exist of N complex numbers and thus the array size should be even");
		}
	    double [] a = new double[p.length];
	    final int n = p.length / 2;

	    for(int i = 0; i < n; ++i ) {
			for(int j = i; j > 0; --j ) {
			    a[2*j] += p[2*i] * a[2*(j-1)] - p[2*i+1] * a[2*(j-1)+1];
			    a[2*j+1] += p[2*i] * a[2*(j-1)+1] + p[2*i+1] * a[2*(j-1)];
			}
			a[0] += p[2*i];
			a[1] += p[2*i+1];
	    }
	    return a ;
	}
	
	/**
	 * Multiplies a series of trinomials together and returns
	 * the coefficients of the resulting polynomial.
	 * 
	 * <p>The multiplication has the following form:</p>
	 * <pre>
	 *  (x^2 + b[0]x + c[0])*(x^2 + b[1]x + c[1])*...*(x^2 + b[n-1]x + c[n-1])</pre>
	 * <p>The b[i] and c[i] coefficients are assumed to be complex and are passed
	 * to the function as a pointers to arrays of doubles of length 2n. The real
	 * part of the coefficients are stored in the even numbered elements of the
	 * array and the imaginary parts are stored in the odd numbered elements.</p>
	 * 
	 * <p>The resulting polynomial has the following form:
	 * <pre>
	 *  x^2n + a[0]*x^2n-1 + a[1]*x^2n-2 + ... +a[2n-2]*x + a[2n-1]</pre>
	 * <p>The a[i] coefficients can in general be complex but should in most cases
	 * turn out to be real. The a[i] coefficients are returned by the function as
	 * a n array of doubles of length 4n. The real and imaginary parts are stored, 
	 * respectively, in the even and odd elements of the array.</p>
	 *
	 * @param b An array of doubles of length <code>2n</code>.
	 * @param c Pointer to an array of doubles of length <code>2n</code>.
	 * 
	 * @return an array of size <code>4n</code>, containing the coefficients of the resulting polynomial
	 */
	public static final double[] trinomial_mult(double[] b, double[] c) {
		if (b == null) {
			throw new NullPointerException("b");
		}
		if (c == null) {
			throw new NullPointerException("c");
		}
		final int inputLength = b.length;
		if (inputLength < 2) {
			throw new IllegalArgumentException("Input must have at least 2 elements");
		}
		if (inputLength % 2 != 0) {
			throw new IllegalArgumentException("Input must have an even number of elements");
		}
		if (c.length != inputLength) {
			throw new IllegalArgumentException("Both inputs must have same, even length");
		}
		final int n = inputLength / 2;
	    double[] a = new double[4 * n];

	    a[2] = c[0];
	    a[3] = c[1];
	    a[0] = b[0];
	    a[1] = b[1];
	  
	    for(int i = 1; i < n; ++i ) {
			a[2*(2*i+1)]   += c[2*i]*a[2*(2*i-1)]   - c[2*i+1]*a[2*(2*i-1)+1];
			a[2*(2*i+1)+1] += c[2*i]*a[2*(2*i-1)+1] + c[2*i+1]*a[2*(2*i-1)];
	
			for(int j = 2*i; j > 1; --j ) {
			    a[2*j]   += b[2*i] * a[2*(j-1)]   - b[2*i+1] * a[2*(j-1)+1] + 
				c[2*i] * a[2*(j-2)]   - c[2*i+1] * a[2*(j-2)+1];
			    a[2*j+1] += b[2*i] * a[2*(j-1)+1] + b[2*i+1] * a[2*(j-1)] +
				c[2*i] * a[2*(j-2)+1] + c[2*i+1] * a[2*(j-2)];
			}
	
			a[2] += b[2*i] * a[0] - b[2*i+1] * a[1] + c[2*i];
			a[3] += b[2*i] * a[1] + b[2*i+1] * a[0] + c[2*i+1];
			a[0] += b[2*i];
			a[1] += b[2*i+1];
	    }

	    return a;
	}
	
	/**
	 * Calculates the d coefficients for a Butterworth low-pass filter.
	 * 
	 * @param n the filter order, which must be one or greater
	 * @param fcf frequency as a fraction of the sample frequency. Must be between 0 and 0.5  
	 * @return The coefficients as an array of doubles.
	 */
	public static double[] lowPassButterworthDcoefficients(int n, double fcf) {
		if (n < 1) {
			throw new IllegalArgumentException("Order of filter must be at least 1");
		}
		if (fcf >= 0.5) {
			throw new IllegalArgumentException("Frequency as part of the sample frequency, must be between 0 and 0.5");
		}
		
	    final double[] rcof = new double[2 * n];     // binomial coefficients
	    final double theta = Math.PI * fcf * 2.0;
	    final double st = Math.sin(theta);
	    final double ct = Math.cos(theta);

	    for(int k = 0; k < n; ++k ) {
			final double parg = Math.PI * (double)(2 * k + 1)/(double)(2 * n);
			final double sparg = Math.sin(parg);
			final double cparg = Math.cos(parg);
			final double a = 1.0 + st * sparg;
			rcof[2*k] = -ct / a;
			rcof[2*k+1] = -st * cparg / a;
	    }

	    final double[] dcof = binomialMultiply(rcof);

	    dcof[1] = dcof[0];
	    dcof[0] = 1.0;
	    for(int k = 3; k <= n; ++k ) {
	        dcof[k] = dcof[2 * k - 2];
	    } 
	    
	    return dcof;
	}
	
	/**
	 * Calculates the d coefficients for a Butterworth high-pass filter.
	 * 
	 * @param n the filter order, which must be one or greater
	 * @param fcf frequency as a fraction of the sample frequency. Must be between 0 and 0.5
	 * @return The coefficients as an array of doubles.
	 */
	public static double[] highPassButterworthDcoefficients(int n, double fcf) {
		return lowPassButterworthDcoefficients(n, fcf);
	}
	
	/**
	 * Calculates the unscaled c coefficients for a Butterworth low pass filter. 
	 * 
	 * @param n the filter order, which must be one or greater
	 * @return the coefficients as an array of integers.
	*/
	public static int[] unscaledLowPassButterworthCcoefficients(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("Filter order must be at least 1");
		}
	    int[] ccof = new int[n + 1];

	    ccof[0] = 1;
	    ccof[1] = n;
	    
	    int m = n/2;
	    for(int i=2; i <= m; ++i)
	    {
	        ccof[i] = (n-i+1)*ccof[i-1]/i;
	        ccof[n-i]= ccof[i];
	    }
	    ccof[n-1] = n;
	    ccof[n] = 1;

	    return ccof;
	}
	
	/**
	 * Calculates the scaled c coefficients for a Butterworth low pass filter. 
	 *
	 * <p>Using these values, the maximum filter response has a value of 1.0.</p>
	 *   
	 * @param n the filter order, which must be one or greater
	 * @param fcf frequency as a fraction of the sample frequency. Must be between 0 and 0.5
	 * 
	 * @return the coefficients as an array of integers.
	*/
	public static double[] lowPassButterworthCcoefficients(int n, double fcf) {
		final int[] unscaled = unscaledLowPassButterworthCcoefficients(n);
		final double scale = lowPassButterworthScalingFactor(n, fcf);
		final int length = unscaled.length;
		final double[] c = new double[length];
		for (int i = 0; i < length; i++) {
			c[i] = scale * unscaled[i];
		}
		
		return c;
	}
	
	
	/**
	 * Calculates the c coefficients for a Butterworth High pass filter. 
	 * 
	 * @param n the filter order, which must be one or greater
	 * @return the coefficients as an array of integers.
	*/
	public static int[] unscaledHighPassButterworthCcoefficients(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("Filter order must be at least 1");
		}
		int[] ccof = unscaledLowPassButterworthCcoefficients(n);
		
	    for(int i = 0; i <= n; ++i) {
	       	if(i % 2 != 0) {
	       		ccof[i] = -ccof[i];
	       	}
	    }

	    return ccof;
	}	
	
	/**
	 * Calculates the scaled c coefficients for a Butterworth high pass filter. 
	 *
	 * <p>Using these values, the maximum filter response has a value of 1.0.</p>
	 *   
	 * @param n the filter order, which must be one or greater
	 * @param fcf frequency as a fraction of the sample frequency. Must be between 0 and 0.5
	 * @return the coefficients as an array of integers.
	*/
	public static double[] highPassButterworthCcoefficients(int n, double fcf) {
		final int[] unscaled = unscaledHighPassButterworthCcoefficients(n);
		final double scale = highPassButterworthScalingFactor(n, fcf);
		final int length = unscaled.length;
		final double[] c = new double[length];
		for (int i = 0; i < length; i++) {
			c[i] = scale * unscaled[i];
		}
		
		return c;
	}

	/**
	 * Calculates the scaling factor for a Butterworth low pass filter.
	 * 
	 * <p>The scaling factor is what the c coefficients must be multiplied by so 
	 * that the filter response has a maximum value of 1.</p>
	 * 
	 * @param n the filter order, which must be at least 1
	 * @param fcf frequency as a fraction of the sample frequency. Must be between 0 and 0.5
	 * 
	 * @return the scaling factor
	*/
	public static double lowPassButterworthScalingFactor(int n, double fcf) {
		if (n < 1) {
			throw new IllegalArgumentException("Order of filter must be at least 1");
		}
		if (fcf >= 0.5) {
			throw new IllegalArgumentException("Frequency as part of the sample frequency, must be between 0 and 0.5");
		}
	    final double omega = Math.PI * fcf * 2.0;
	    double fomega = Math.sin(omega); // function of omega
	    final double parg0 = Math.PI / (double)(2*n); // zeroth pole angle

	    double sf = 1.0; // scaling factor

	    final int m = n / 2;         // loop variables
	    for(int k = 0; k < m; ++k) {
	        sf *= 1.0 + fomega * Math.sin((double)(2*k+1)*parg0);
	    }

	    fomega = Math.sin(omega / 2.0);

	    if( n % 2 != 0) {
	    	sf *= fomega + Math.cos(omega / 2.0);
	    }
	    sf = Math.pow( fomega, n ) / sf;

	    return(sf);
	}

	/**
	 * Calculates the scaling factor for a Butterworth high pass filter.
	 * 
	 * <p>The scaling factor is what the c coefficients must be multiplied by so 
	 * that the filter response has a maximum value of 1.</p>
	 * 
	 * @param n the filter order, which must be at least 1
	 * @param fcf frequency as a fraction of the sample frequency. Must be between 0 and 0.5
	 * 
	 * @return the scaling factor
	*/
	public static double highPassButterworthScalingFactor(int n, double fcf) {
		if (n < 1) {
			throw new IllegalArgumentException("Order of filter must be at least 1");
		}
		if (fcf >= 0.5) {
			throw new IllegalArgumentException("Frequency as part of the sample frequency, must be between 0 and 0.5");
		}
	    final double omega = Math.PI * fcf * 2.0;
	    double fomega = Math.sin(omega); // function of omega
	    final double parg0 = Math.PI / (double)(2*n); // zeroth pole angle

	    double sf = 1.0; // scaling factor
	    
	    final int m = n / 2;         // loop variables
	    for(int k = 0; k < m; ++k) {
	        sf *= 1.0 + fomega * Math.sin((double)(2*k+1)*parg0);
	    }

	    fomega = Math.cos(omega / 2.0);

	    if( n % 2 != 0) { 
	    	sf *= fomega + Math.sin(omega / 2.0);
	    }
	    sf = Math.pow( fomega, n ) / sf;

	    return sf;
	}	
	
	public static double preWarpFractionOfPi(double fractionOfPi) {
		return Math.atan(fractionOfPi * Math.PI) / Math.PI;
	}
	
	public static double preWarpAsFractionOfPi(double frequency, double sampleRate) {
		return preWarpFractionOfPi(frequency / sampleRate) / Math.PI;
	}
}
