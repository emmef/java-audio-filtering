package org.emmef.audio.buckets;

public class Integration {
	public static double MIN_FACTOR = 1.0 - 10 * Math.ulp(1.0);
	public static double MAX_FACTOR = 1.0 - MIN_FACTOR;
	public static double MAX_COUNT = -1.0 / Math.log(MIN_FACTOR);
	public static double MIN_COUNT = -1.0 / Math.log(MAX_FACTOR);

	public static double historyFromCount(double count) {
		return (count < MIN_COUNT)   ? 0.0
				: (count > MAX_COUNT) ? MAX_FACTOR
				: Math.exp(-1.0 / count);
	}

	public static double countFromHistory(double history) {
		return -1.0 / Math.log(history);
	}

	public static double inputFromHistory(double history, double scale) {
		return scale * (1.0 - history);
	}

	public static double inputFromHistory(double history) {
		return inputFromHistory(history, 1.0);
	}

	public static double scaleFromFactors(double history, double input) {
		return input / (1.0 - history);
	}

	public static double integrated(double history, double historyFactor, double input, double inputFactor) {
		return historyFactor * history + inputFactor * input;
	}

	public static final class Factors {
		double history;
		double input;

		public Factors() {
			this.history = 0.0;
			this.input = 1.0;
		}

		public Factors(double sampleCount, double scale) {
			this.history = historyFromCount(sampleCount);
			this.input = inputFromHistory(history, scale);
		}

		public Factors(double sampleCount) {
			this(sampleCount, 1.0);
		}

		public Factors(Factors factors) {
			this.history = factors.history;
			this.input = factors.input;
		}

		public double getHistory() {
			return history;
		}

		public double getInput() {
			return input;
		}

		public double getCount() {
			return countFromHistory(history);
		}

		void setCount(double count, double scale) {
			history = historyFromCount(count);
			input = inputFromHistory(history, scale);
		}

		void setScale(double scale) {
			input = inputFromHistory(history, scale);
		}

		double integrated(double historyValue, double inputValue) {
			return Integration.integrated(historyValue, this.history, inputValue, this.input);
		}
	}

	public static final class Integrator {
		private final Factors factors;
		private double value;

		public Integrator(Factors factors) {
			this.factors = factors;
			this.value = 0.0;
		}

		public Factors getFactors() {
			return factors;
		}

		public double integrate(double sample) {
			value = factors.integrated(value, sample);
			return value;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		void setCount(double count, double scale) {
			factors.setCount(count, scale);
		}

		void setScale(double scale) {
			factors.setScale(scale);
		}
	}

	public static double DOUBLE_INTEGRATOR_PROLONGATION_FACTOR = calculatePrologationFactor();

	public static final class DoubleIntegrator {

		private final Factors factors;
		private double value1 = 0.0;
		private double value2 = 0.0;

		public DoubleIntegrator(Factors factors) {
			this.factors = factors;
		}

		public Factors getFactors() {
			return factors;
		}

		public double integrate(double sample) {
			value1 = factors.integrated(value1, sample);
			value2 = factors.integrated(value2, value1);
			return value2;
		}

		public double getValue() {
			return value2;
		}

		public void setValue(double value) {
			this.value1 = value;
			this.value2 = value;
		}

		void setCount(double count, double scale) {
			factors.setCount(count, scale);
		}

		void setScale(double scale) {
			factors.setScale(scale);
		}
	}

	private static double calculatePrologationFactor() {
		final int COUNT = 10000;
		final double ONE_OVER_E = Math.exp(-1.0);
		Factors factors = new Factors(COUNT);
		DoubleIntegrator integrator = new DoubleIntegrator(factors);
		integrator.setValue(1.0);
		int samples = 0;
		while (integrator.getValue() > ONE_OVER_E) {
			integrator.integrate(0.0);
			samples++;
		}
		return (double) samples / COUNT;
	}

}
