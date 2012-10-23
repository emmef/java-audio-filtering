package org.emmef.audio.noisereduction;

public abstract class NrDynamics {
	
	public abstract double amplification(double level);
	public abstract double amplificationForSquaredInput(double level);

	
	
	public static final class NoiseReductionInfo {
		public final double thresholdDb;
		public final double expansionRatio;
		public final double relativeThreshold;
	
		public NoiseReductionInfo(double thresholdDb, double expansionRatio) {
			if (thresholdDb <= 0) {
				throw new IllegalArgumentException("Theshold must be above noise level");
			}
			this.thresholdDb = thresholdDb;
			this.expansionRatio = expansionRatio;
			this.relativeThreshold = decibelToValue(thresholdDb);
		}
		
		NrDynamics createExpansion(double noiseLevel) {
			if (noiseLevel <= 0) {
				throw new IllegalArgumentException("Noise level must be positive");
			}
			if (Math.abs(expansionRatio - 2.0) < 0.05) {
				return new SquareNoiseReduction(relativeThreshold, noiseLevel);
			}
			if (Math.abs(expansionRatio - 3.0) < 0.05) {
				return new CubicNoiseReduction(relativeThreshold, noiseLevel);
			}
			return new ExpansionNoiseReduction(relativeThreshold, expansionRatio, noiseLevel);
		}
		
		NrDynamics createSubtractive(double noiseLevel) {
			return new SubtractiveNoiseReduction(relativeThreshold, noiseLevel);
		}
	}
	
	public static abstract class AbstractExpansionNoiseReduction extends NrDynamics {
		public final double relativeThreshold;
		public final double expansionRatio;
		public final double threshold;
		public final double threshold2;
		public final double divideByThreshold;
		public final double divideByThreshold2;
		
		public static void staticParameterCheck(double relativeThreshold, double expansionRatio) {
			if (relativeThreshold <= 1.0) {
				throw new IllegalArgumentException("Relative threshold for expansion must be greater than 1.0");
			}
			if (expansionRatio < 1.25) {
				throw new IllegalArgumentException("Expansion ratio must be at least 1.25");
			}
			if (expansionRatio > 4) {
				throw new IllegalArgumentException("Expansion ratio cannot be larger than 4");
			}
		}
		
		public static void staticParameterCheckDb(double dBThreshold, double expansionRatio) {
			if (dBThreshold < 0) {
				throw new IllegalArgumentException("Threshold dB for expansion must be greater than 0");
			}
			if (expansionRatio < 1.25) {
				throw new IllegalArgumentException("Expansion ratio must be at least 1.25");
			}
			if (expansionRatio > 4) {
				throw new IllegalArgumentException("Expansion ratio cannot be larger than 4");
			}
		}
		
		public AbstractExpansionNoiseReduction(double relativeThreshold, double expansionRatio, double noiseLevel) {
			if (relativeThreshold <= 1.0) {
				throw new IllegalArgumentException("Relative threshold for expansion must be greater than 1.0");
			}
			if (noiseLevel < Float.MIN_NORMAL) {
				throw new IllegalArgumentException("Noise level must be larger than " + Float.MIN_NORMAL);
			}
			if (expansionRatio < 1.25) {
				throw new IllegalArgumentException("Expansion ratio must be at least 1.25");
			}
			if (expansionRatio > 4) {
				throw new IllegalArgumentException("Expansion ratio cannot be larger than 4");
			}
			this.relativeThreshold = relativeThreshold;
			this.expansionRatio = expansionRatio;
			this.threshold = noiseLevel * relativeThreshold;
			this.threshold2 = threshold * threshold;
			this.divideByThreshold = 1.0 / threshold;
			this.divideByThreshold2 = 1.0 / threshold2;
		}
		
		@Override
		public String toString() {
			return String.format("%s(expansion; threshold=%1.1f; ratio=%1.1f)", getClass().getSimpleName(), relativeThreshold, expansionRatio);
		}
	}
	
	public static class ExpansionNoiseReduction extends AbstractExpansionNoiseReduction {
		public final double powerFactor;
		public final double powerFactor2;

		ExpansionNoiseReduction(double relativeThreshold, double expansionRatio, double noiseLevel) {
			super(relativeThreshold, expansionRatio, noiseLevel);
			this.powerFactor = expansionRatio - 1.0;
			this.powerFactor2 = 0.5*powerFactor;
		}
		
		@Override
		public double amplification(double level) {
			if (level > threshold) {
				return 1.0;
			}
			if (level < Float.MIN_NORMAL) {
				return 0.0;
			}
			return Math.exp(Math.log(level * divideByThreshold) * powerFactor);
		}
		
		@Override
		public double amplificationForSquaredInput(double level) {
			if (level > threshold2) {
				return 1.0;
			}
			if (level < Float.MIN_NORMAL) {
				return 0.0;
			}
			return Math.exp(Math.log(level * divideByThreshold2) * powerFactor2);
		}
	}
	
	public static class SquareNoiseReduction extends AbstractExpansionNoiseReduction {
		public SquareNoiseReduction(double relativeThreshold, double noiseLevel) {
			super(relativeThreshold, 2.0, noiseLevel);
		}
		
		@Override
		public double amplification(double level) {
			if (level > threshold) {
				return 1.0;
			}
			if (level < Float.MIN_NORMAL) {
				return 0.0;
			}
			return level * divideByThreshold;
		}
		
		@Override
		public double amplificationForSquaredInput(double level) {
			if (level > threshold2) {
				return 1.0;
			}
			if (level < Float.MIN_NORMAL) {
				return 0.0;
			}
			return Math.sqrt(level * divideByThreshold2);
		}
	}
	
	public static class CubicNoiseReduction extends AbstractExpansionNoiseReduction {
		public CubicNoiseReduction(double relativeThreshold, double noiseLevel) {
			super(relativeThreshold, 2.0, noiseLevel);
		}
		
		@Override
		public double amplification(double level) {
			if (level > threshold) {
				return 1.0;
			}
			if (level < Float.MIN_NORMAL) {
				return 0.0;
			}
			final double d = level * divideByThreshold;
			return d * d;
		}
		
		@Override
		public double amplificationForSquaredInput(double level) {
			if (level > threshold2) {
				return 1.0;
			}
			if (level < Float.MIN_NORMAL) {
				return 0.0;
			}
			return level * divideByThreshold2;
		}
	}

	public abstract static class AbstractSubtractiveNoiseReduction extends NrDynamics {
		public final double relativeThreshold;
		public final double noiseLevel;
		public final double threshold;
		public final double threshold2;
		public final double subtractiveRatio;
		public final double divideByThreshold;
		public final double divideByThreshold2;

		public static void checkStaticParameters(double relativeThreshold, double subtractionRatio) {
			if (relativeThreshold < 0.1 || relativeThreshold > 10.0) {
				throw new IllegalArgumentException("Relative threshold for subtraction must be between 0.1 and 10.0");
			}
			if (subtractionRatio < 0.0 || subtractionRatio > 0.9) {
				throw new IllegalArgumentException("Subraction ratio must be between 0 and 0.9");
			}
		}

		public static void checkStaticParametersDb(double relativeThreshold, double subtractionRatio) {
			if (relativeThreshold < -20 || relativeThreshold > 20) {
				throw new IllegalArgumentException("Relative threshold in dB for subtraction must be between -20 and +20");
			}
			if (subtractionRatio < 0.0 || subtractionRatio > 0.9) {
				throw new IllegalArgumentException("Subraction ratio must be between 0 and 0.9");
			}
		}
		
		public AbstractSubtractiveNoiseReduction(double relativeThreshold, double subtractiveRatio, double noiseLevel) {
			checkStaticParameters(relativeThreshold, subtractiveRatio);
			if (noiseLevel < Float.MIN_NORMAL) {
				throw new IllegalArgumentException("Noise level must be larger than " + Float.MIN_NORMAL);
			}
			this.subtractiveRatio = subtractiveRatio;
			this.relativeThreshold = relativeThreshold;
			this.noiseLevel = noiseLevel;
			this.threshold = relativeThreshold * noiseLevel;
			this.threshold2 = threshold * threshold;
			this.divideByThreshold = 1.0 / threshold;
			this.divideByThreshold2 = 1.0 / threshold2;
		}
		
		@Override
		public String toString() {
			return String.format("NoiseReduction(subtractive; factor=%1.1f; ratio=%1.1f)", relativeThreshold, subtractiveRatio);
		}		
	}
	
	public static class SubtractiveNoiseReduction extends AbstractSubtractiveNoiseReduction {
		
		public SubtractiveNoiseReduction(double relativeThreshold, double noiseLevel) {
			super(relativeThreshold, 0.0, noiseLevel);
		}
		
		@Override
		public double amplification(double level) {
			if (level < threshold) {
				return 0;
			}
			return (level - threshold) / level;
		}
		
		@Override
		public double amplificationForSquaredInput(double level) {
			if (level < threshold2) {
				return 0;
			}
			final double sqrtLevel = Math.sqrt(level);
			return (sqrtLevel - threshold) / sqrtLevel;
		}
	}

	public static class SubtractiveRatioNoiseReduction extends AbstractSubtractiveNoiseReduction {
		public SubtractiveRatioNoiseReduction(double relativeThreshold, double subtractionRatio, double noiseLevel) {
			super(relativeThreshold, subtractionRatio, noiseLevel);
		}
		
		@Override
		public double amplification(double level) {
			if (level < threshold) {
				return 0;
			}
			
			double relative = threshold * Math.pow(level / threshold, subtractiveRatio);
			
			return (level - relative) / level;
		}
		
		@Override
		public double amplificationForSquaredInput(double squaredLevel) {
			if (squaredLevel < threshold2) {
				return 0;
			}
			final double level = Math.sqrt(squaredLevel);
			double relative = threshold * Math.pow(level / threshold2, subtractiveRatio);
			
			return (level - relative) / level;
		}
	}


	public static double decibelToValue(double decibel) {
		return Math.pow(10, 0.05 * decibel);
	}

	public static double valueToDecibel(double value) {
		return 20.0 * Math.log10(Math.max(1e-300, value));
	}
}
