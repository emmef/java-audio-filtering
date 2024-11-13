package org.emmef.audio.noisedetection;

import org.emmef.audio.noisereduction.ChainableFilter;

public interface NoiseLevelDetectionFilter extends ChainableFilter {
	double getNoiseLevel();
}
