package org.emmef.audio.noisereduction;

public abstract class NrDynamics {
	private final double noiseLevel;
	private final double threshold;
	private final double relativeThresholdDb;

	protected NrDynamics(double noiseLevel, double relativeThresholdDb) {
		if (noiseLevel < 1e-12) {
			throw new IllegalArgumentException("Noise level must be greater than 1e-12: " + noiseLevel);
		}
		if (relativeThresholdDb < -20 || relativeThresholdDb > 20) {
			throw new IllegalArgumentException("Relative threshold must be between -20dB and +20dB: " + relativeThresholdDb);
		}
		this.relativeThresholdDb = relativeThresholdDb;
		this.noiseLevel = noiseLevel;
		this.threshold = noiseLevel * decibelToValue(relativeThresholdDb);
	}

	public final double getNoiseLevel() {
		return noiseLevel;
	}

	public final double getRelativeThresholdInDb() {
		return relativeThresholdDb;
	}

	public final double getRelativeLevel(double level) {
		return level / threshold;
	}

	public final double amplification(double level) {
		return Math.max(0.0, Math.min(1.0, calculateAmplification(getRelativeLevel(level))));
	}

	public abstract double calculateAmplification(double relativeLevel);

	public abstract String getDescription();

	public abstract NrDynamics withNoiseLevel(double noiseLevel);

	public String toString(String paramName, double paramValue) {
		return String.format("%s(noiseLevel=%1.1fdB; relativeLevel=%1.1fdB; %s=%1.1f)", getDescription(), valueToDecibel(noiseLevel), getRelativeThresholdInDb(), paramName, paramValue);
	}

	public static class AbstractExpansionNoiseReduction extends NrDynamics {
		public final double expansionRatio;

		public AbstractExpansionNoiseReduction(double relativeThresholdInDb, double expansionRatio, double noiseLevel) {
			super(noiseLevel, relativeThresholdInDb);
			if (expansionRatio < 1.25) {
				throw new IllegalArgumentException("Expansion ratio must be at least 1.25");
			}
			if (expansionRatio > 4) {
				throw new IllegalArgumentException("Expansion ratio cannot be larger than 4");
			}
			this.expansionRatio = expansionRatio;
		}

		public AbstractExpansionNoiseReduction(double relativeThresholdInDb, double expansionRatio) {
			this(relativeThresholdInDb, expansionRatio, 1e-6);
		}

		@Override
		public AbstractExpansionNoiseReduction withNoiseLevel(double noiseLevel) {
			return new AbstractExpansionNoiseReduction(getRelativeThresholdInDb(), expansionRatio, noiseLevel);
		}

		@Override
		public String toString() {
			return super.toString("ratio", expansionRatio);
		}

		@Override
		public double calculateAmplification(double relativeLevel) {
			return Math.pow(Math.min(1.0, relativeLevel), expansionRatio);
		}

		@Override
		public String getDescription() {
			return "Expansion";
		}
	}

	public static class AbstractSubtractiveNoiseReduction extends NrDynamics {
		public final double subtractiveRatio;

		public AbstractSubtractiveNoiseReduction(double relativeThresholdInDb, double subtractiveRatio, double noiseLevel) {
			super(noiseLevel, relativeThresholdInDb);
			if (subtractiveRatio < 0.0 || subtractiveRatio > 0.9) {
				throw new IllegalArgumentException("Subraction ratio must be between 0 and 0.9");
			}
			this.subtractiveRatio = subtractiveRatio;
		}

		public AbstractSubtractiveNoiseReduction(double relativeThresholdInDb, double expansionRatio) {
			this(relativeThresholdInDb, expansionRatio, 1e-6);
		}

		@Override
		public AbstractSubtractiveNoiseReduction withNoiseLevel(double noiseLevel) {
			return new AbstractSubtractiveNoiseReduction(getRelativeThresholdInDb(), subtractiveRatio, noiseLevel);
		}

		@Override
		public String toString() {
			return super.toString("ratio", subtractiveRatio);
		}

		@Override
		public double calculateAmplification(double relativeLevel) {
			return (relativeLevel - Math.pow(Math.max(1.0, relativeLevel), subtractiveRatio)) / relativeLevel;
		}

		@Override
		public String getDescription() {
			return "Subtraction";
		}
	}

	public static double decibelToValue(double decibel) {
		return Math.pow(10, 0.05 * decibel);
	}

	public static double valueToDecibel(double value) {
		return 20.0 * Math.log10(Math.max(1e-300, value));
	}
}
