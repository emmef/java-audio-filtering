package org.emmef.audio.format;

public interface FrameMetrics {
	/**
	 * Returns the sample rate (number of frames per second)
	 * @return a positive number
	 */
	long getSampleRate();

	/**
	 * Returns the number of channels. 
	 * <p>
	 * Each frame consists of this number of samples.
	 * @return a positive number
	 */
	int getChannels();
}
