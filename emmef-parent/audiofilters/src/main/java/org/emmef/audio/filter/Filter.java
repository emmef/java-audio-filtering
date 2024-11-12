package org.emmef.audio.filter;

public interface Filter {
	double filter(double input);
	void reset();
}
