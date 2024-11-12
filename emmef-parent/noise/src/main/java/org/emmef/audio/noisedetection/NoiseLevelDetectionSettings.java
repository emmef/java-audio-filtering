package org.emmef.audio.noisedetection;

public class NoiseLevelDetectionSettings {
	public final double noiseWindow;
	public final double maxRmsWindow;
	public final double minRatio;
	public final double maxRatio;
	public final int noiseBucketSize;
	public final int maxRmsBucketSize;
	public final int sampleRate;

	public static NoiseLevelDetectionSettings create(int sampleRate, double noiseWindow, double maxRmsWindow, double minRatio, double maxRatio) {
		if (sampleRate < 1) {
			throw new IllegalArgumentException("SampleRate must be positive");
		}
		if (noiseWindow < 0) {
			throw new IllegalArgumentException("Noise window must be positive");
		}
		final double noiseBucketSize = 0.5 + noiseWindow * sampleRate;
		if (noiseBucketSize < 1) {
			throw new IllegalArgumentException("Noise window gives a bucket size of 0 at this samplerate, which is not allowed");
		}
		if (noiseBucketSize > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Noise bucket too large");
		}
		if (maxRmsWindow < 0) {
			throw new IllegalArgumentException("RMS window must be positive");
		}
		final double maxRmsBucketSize = 0.5 + maxRmsWindow * sampleRate;
		if (maxRmsBucketSize < 1) {
			throw new IllegalArgumentException("RMS window gives a bucket size of 0 at this samplerate, which is not allowed");
		}
		if (maxRmsBucketSize > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("RMS bucket too large");
		}
		
		final double normalizedMinRatio = normalizedRatio(minRatio, Float.MAX_VALUE);
		final double normalizedMaxRatio = normalizedRatio(maxRatio, Float.MAX_VALUE);
		
		return new NoiseLevelDetectionSettings(sampleRate, noiseWindow, maxRmsWindow, (int)noiseBucketSize, (int)maxRmsBucketSize,
				normalizedMinRatio, normalizedMaxRatio);
	}

	public NoiseLevelDetectionSettings withNoiseWindow(double noiseWindow) {
		if (noiseWindow < 0) {
			throw new IllegalArgumentException("Noise window must be positive");
		}
		final double noiseBucketSize = 0.5 + noiseWindow * sampleRate;
		if (noiseBucketSize > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Noise bucket too large");
		}
		
		return new NoiseLevelDetectionSettings(sampleRate, noiseWindow, maxRmsWindow, (int)noiseBucketSize, maxRmsBucketSize, minRatio, maxRatio);
	}

	private NoiseLevelDetectionSettings(int sampleRate, double noiseWindow, double maxRmsWindow,
			final int noiseBucketSize, final int maxRmsBucketSize,
			final double normalizedMinRatio, final double normalizedMaxRatio) {
		this.sampleRate = sampleRate;
		this.noiseWindow = noiseWindow;
		this.maxRmsWindow = maxRmsWindow;
		this.noiseBucketSize = (int) noiseBucketSize;
		this.maxRmsBucketSize = (int) maxRmsBucketSize;
		if (normalizedMaxRatio > normalizedMinRatio) {
			this.minRatio = normalizedMinRatio;
			this.maxRatio = normalizedMaxRatio;
		}
		else {
			this.minRatio = normalizedMaxRatio;
			this.maxRatio = normalizedMinRatio;
		}
	}
	
	
	/**
	 * Returns a positive number, equal or largen than 1.0 , based on the given ratio. 
	 * 
	 * <p>If the ratio is negative or smaller than 1.0, the method will return the negative or reciprokal value or both.</p>
	 *  
	 * @param ratio
	 */
	public static double normalizedRatio(double ratio, double maximumRatio) {
		if (maximumRatio < 1.0 || maximumRatio > Float.MAX_VALUE) {
			throw new IllegalArgumentException("Invalid maximum ratio " + maximumRatio);
		}
		final double result = Math.abs(ratio);
		if (result >= 1.0) {
			if (result > maximumRatio) {
				return maximumRatio;
			}
			return result;
		}
		double minValue = 1.0 / maximumRatio;
		if (result < minValue) {
			return maximumRatio;
		}
		
		return 1.0 / result;
	}

}
