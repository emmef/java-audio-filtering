package org.emmef.config.options;

public interface NumberValue<T extends Number & Comparable<T>> extends Value<T> {
	NumberValue<T> restrictTo(T bound1, T bound2);
	NumberValue<T> inRange(T bound1, T bound2);
}
