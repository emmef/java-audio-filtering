package org.emmef.audio.buckets;

public interface Detection {
	double addSample(double sample);

	void reset();

	boolean isWholeBucketScanned();

	int getBucketSize();

	double getMinimum();

	double getMaximum();

	double getValue();

	default int getHoldCount() { return 0; }
}
