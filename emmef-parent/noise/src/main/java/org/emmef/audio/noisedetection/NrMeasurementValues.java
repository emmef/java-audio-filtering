package org.emmef.audio.noisedetection;


public class NrMeasurementValues extends NrMeasurementSettings {
	public final double sampleRate;
	public final int rmsWinwSamples;
	public final int noiseWinwSamples;
	public final int skipWinwSamples;
	public final int skipStartSamples;
	public final int skipEndSamples;
	public final double minSnRatio;
	public final double maxSnRatio;

	public NrMeasurementValues(NrMeasurementSettings settings, double sampleRate) {
		super(
				settings.minSnRatioDb, settings.maxSnRatioDb, 
				settings.rmsWin, settings.noiseWin, settings.skipWin, 
				settings.skipStartSecs,	settings.skipEndSecs, settings.measureIrregularNoise, settings.frequencyScanning);
		
		if (sampleRate < 1) {
			throw new IllegalArgumentException("Samplerate must be at least 1");
		}
		this.sampleRate = sampleRate;
		this.rmsWinwSamples = samplesFromSeconds(rmsWin);
		this.noiseWinwSamples = samplesFromSeconds(noiseWin);
		this.skipWinwSamples = samplesFromSeconds(skipWin);
		this.skipStartSamples = samplesFromSeconds(skipStartSecs);
		this.skipEndSamples = samplesFromSeconds(skipEndSecs);
		this.minSnRatio = ratioFromDb(minSnRatioDb);
		this.maxSnRatio = ratioFromDb(maxSnRatioDb);
	}
	
	public int samplesFromSeconds(double seconds) {
		return (int)(0.5 + seconds * sampleRate);
	}
	
	public double ratioFromDb(double db) {
		return Math.pow(10, 0.05 * db);
	}
}
