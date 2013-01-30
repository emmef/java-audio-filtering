package org.emmef.audio.noisereduction;

import org.emmef.audio.noisereduction.NrDynamics.AbstractSubtractiveNoiseReduction;
import org.emmef.audio.noisereduction.NrDynamics.CubicNoiseReduction;
import org.emmef.audio.noisereduction.NrDynamics.ExpansionNoiseReduction;
import org.emmef.audio.noisereduction.NrDynamics.SquareNoiseReduction;
import org.emmef.logging.FormatLogger;
import org.emmef.logging.FormatLoggerFactory;

public interface NrDynamicsFactory {
	NrDynamics create(double noiseLevel);
	
	static class Expansion implements NrDynamicsFactory {
		private static final FormatLogger log = FormatLoggerFactory.getLogger(NrDynamicsFactory.class);
		public final double relativeThreshold;
		public final double expansionRatio;

		public Expansion(double thresholdDb, double expansionRatio) {
			NrDynamics.AbstractExpansionNoiseReduction.staticParameterCheckDb(thresholdDb, expansionRatio);
			relativeThreshold = NrDynamics.decibelToValue(thresholdDb);
			this.expansionRatio = expansionRatio;
		}
		
		@Override
		public NrDynamics create(double noiseLevel) {
			if (noiseLevel <= 0) {
				throw new IllegalArgumentException("Noise level must be positive");
			}
			if (Math.abs(expansionRatio - 2.0) < 0.05) {
				final SquareNoiseReduction squareNoiseReduction = new SquareNoiseReduction(relativeThreshold, noiseLevel);
				log.debug(squareNoiseReduction);
				return squareNoiseReduction;
			}
			if (Math.abs(expansionRatio - 3.0) < 0.05) {
				final CubicNoiseReduction cubicNoiseReduction = new CubicNoiseReduction(relativeThreshold, noiseLevel);
				log.debug(cubicNoiseReduction);
				return cubicNoiseReduction;
			}
			final ExpansionNoiseReduction expansionNoiseReduction = new ExpansionNoiseReduction(relativeThreshold, expansionRatio, noiseLevel);
			log.debug(expansionNoiseReduction);
			return expansionNoiseReduction;
		}
		
		@Override
		public String toString() {
			return String.format("Noise-dynamics(expansion): above-noiselevel=%1.1fdB; expansion-ratio=%1.2f", NrDynamics.valueToDecibel(relativeThreshold), expansionRatio);
		}
	}
	
	static class Subtraction implements NrDynamicsFactory {
		private static final FormatLogger log = FormatLoggerFactory.getLogger(Subtraction.class);
		public final double threshold;
		public final double subtractionRatio;

		public Subtraction(double factorDb, double subtractionRatio) {
			this.subtractionRatio = subtractionRatio;
			AbstractSubtractiveNoiseReduction.checkStaticParametersDb(factorDb, subtractionRatio);
			threshold = NrDynamics.decibelToValue(factorDb);
		}
		
		@Override
		public NrDynamics create(double noiseLevel) {
			if (Math.abs(subtractionRatio) < 0.05) {
				final NrDynamics.SubtractiveNoiseReduction subtractiveNoiseReduction = new NrDynamics.SubtractiveNoiseReduction(threshold, noiseLevel);
				log.debug(subtractiveNoiseReduction);
				return subtractiveNoiseReduction;
			}
			else {
				final NrDynamics.SubtractiveRatioNoiseReduction subtractiveRatioNoiseReduction = new NrDynamics.SubtractiveRatioNoiseReduction(threshold, subtractionRatio, noiseLevel);
				log.debug(subtractiveRatioNoiseReduction);
				return subtractiveRatioNoiseReduction;
			}
		}
		
		@Override
		public String toString() {
			return String.format("Noise-dynamics(subtraction): above-noiselevel=%1.1fdB; up-ratio=%1.2f", NrDynamics.valueToDecibel(threshold), subtractionRatio);
		}
	}
}
