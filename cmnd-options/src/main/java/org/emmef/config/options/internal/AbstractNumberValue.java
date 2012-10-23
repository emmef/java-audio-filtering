package org.emmef.config.options.internal;

import org.emmef.config.options.NumberValue;
import org.emmef.config.options.Parser;
import org.emmef.config.options.validators.NumericBound;

class AbstractNumberValue<T extends Number & Comparable<T>> extends AbstractValue<T> implements NumberValue<T> {
	
	public AbstractNumberValue(Class<T> type, Parser<T> parser, Mandatory mandatory, int number) {
		super(type, parser, mandatory, number);
	}

	@Override
	public NumberValue<T> inRange(T bound1, T bound2) {
		validatedBy(new NumericBound<T>(bound1, bound2, NumericBound.Type.BETWEEN));
		return this;
	}

	@Override
	public NumberValue<T> restrictTo(T bound1, T bound2) {
		validatedBy(new NumericBound<T>(bound1, bound2, NumericBound.Type.RESTRICTED));
		return this;
	}
}
