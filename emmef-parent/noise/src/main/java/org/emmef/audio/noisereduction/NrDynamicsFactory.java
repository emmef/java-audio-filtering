package org.emmef.audio.noisereduction;

import org.emmef.audio.noisereduction.NrDynamics.AbstractSubtractiveNoiseReduction;
import org.emmef.logging.FormatLogger;

import javax.annotation.Nonnull;

public abstract class NrDynamicsFactory {
	private final NrDynamics noiseReduction;

	protected NrDynamicsFactory(@Nonnull NrDynamics noiseReduction) {
		this.noiseReduction = noiseReduction;
	}

	public final NrDynamics create(double noiseLevel) {
		return noiseReduction.withNoiseLevel(noiseLevel);
	};

	protected NrDynamics getNoiseReduction() {
		return noiseReduction;
	}

	@Override
	public String toString() {
		return String.format("Factory(noise level to be set) for %s", getNoiseReduction().toString());
	}

	public static class Expansion extends NrDynamicsFactory {
		private static final FormatLogger log = FormatLogger.getLogger(Expansion.class);

		public Expansion(double thresholdDb, double expansionRatio) {
			super(new NrDynamics.AbstractExpansionNoiseReduction(thresholdDb, expansionRatio));
		}
	}
	
	public static class Subtraction extends NrDynamicsFactory {
		private static final FormatLogger log = FormatLogger.getLogger(Subtraction.class);

		public Subtraction(double factorDb, double subtractionRatio) {
			super(new AbstractSubtractiveNoiseReduction(factorDb, subtractionRatio));
		}
	}
}
