package org.emmef.audio.noisedetection;

import java.util.Formatter;


public class NrMeasurementSettings {
	public final int minSnRatioDb;
	public final int maxSnRatioDb;
	public final double rmsWin;
	public final double noiseWin;
	public final double skipWin;
	public final double skipStartSecs;
	public final double skipEndSecs;
	public final int measureIrregularNoise;

	public NrMeasurementSettings(int minSnRatioDb, int maxSnRatioDb, double rmsWin, double noiseWin, double skipWin, double skipStartSecs, double skipEndSecs, int measureIrregularNoise) {
		this.minSnRatioDb = minSnRatioDb;
		this.maxSnRatioDb = maxSnRatioDb;
		this.rmsWin = rmsWin;
		this.noiseWin = noiseWin;
		this.skipWin = skipWin;
		this.skipStartSecs = skipStartSecs;
		this.skipEndSecs = skipEndSecs;
		this.measureIrregularNoise = measureIrregularNoise;
	}
	
	public NrMeasurementValues withSampleRate(double sampleRate) {
		return new NrMeasurementValues(this, sampleRate);
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder("Noise measurement parameters\n");
		try (Formatter f = new Formatter(output)) {
			f.format(" %-25s: %ddB\n", "Minimum accepted S/N ratio", minSnRatioDb);
			f.format(" %-25s: %ddB\n", "Maximum accepted S/N ratio", maxSnRatioDb);
			f.format(" %-25s: Max-RMS=%1.3fs; Noise=%1.3fs; Skip(not accepte)=%1.3fs\n", "Window sizes", rmsWin, noiseWin, skipWin);
			f.format(" %-25s: From start=%1.3fs); from end=%1.3fs\n", "Skip seconds", skipStartSecs, skipEndSecs);
			f.format(" %-25s: %d", "Irregular noise option", measureIrregularNoise);
			f.flush();
			
			return output.toString();
		}
	}
}
