package org.emmef.audio.filter.iir.butterworth;

import org.emmef.audio.filter.Filter;

public final class ButterworthNFilter implements Filter {
	private final double[] d;
	private final double[] c;
	private final int order;
	private final double[] y;
	private final double[] x;
	private final PassType type;

	private ButterworthNFilter(int order, double[] c, double[] d, PassType type) {
		this.d = d;
		this.c = c;
		this.order = order;
		this.type = type;
		this.y = new double[order];
		this.x = new double[order];
	}

	@Override
	public double filter(double xN0) {
		double Y = 0;
		double X = xN0;
		double yN0 = 0.0;
		for (int i = 0, j = 1; i < order; i++, j++) {
			final double xN1 = x[i];
			final double yN1 = y[i];
			x[i] = X;
			X = xN1;
			y[i] = Y;
			Y = yN1;
			yN0 += c[j] * xN1 - d[j] * yN1;
		}
		yN0 += c[0] * xN0;
		y[0] = yN0;
		
		return yN0;
	}
	
	
	public void reset() {
		for (int i = 0; i < x.length; i++) {
			x[i] = 0;
		}
		for (int i = 0; i < y.length; i++) {
			y[i] = 0;
		}
	}
	
	public PassType getType() {
		return type;
	}
	
	public static class Factory {
		private final double[] cs;
		private final double[] ds;
		private final int order;
		private final PassType type;

		public Factory(int order, PassType lowPass, double frequency) {
			if (order < 1) {
				throw new IllegalArgumentException("Order should be at least 1");
			}
			if (frequency >= 0.5) {
				throw new IllegalArgumentException("Frequency should not be equal or higher than Nyquist Frequency (=sampleRate/2)");
			}
			this.order = order;
			this.type = lowPass;
			if (lowPass == PassType.LOW_PASS) {
				cs = Coefficients.lowPassButterworthCcoefficients(order, frequency);
				ds = Coefficients.lowPassButterworthDcoefficients(order, frequency);
			}
			else {
				cs = Coefficients.highPassButterworthCcoefficients(order, frequency);
				ds = Coefficients.highPassButterworthDcoefficients(order, frequency);
			}
		}
		
		public PassType getType() {
			return type;
		}
		
		public int getOrder() {
			return order;
		}
		
		public ButterworthNFilter create() {
			return new ButterworthNFilter(order, cs, ds, type);
		}
	}
}