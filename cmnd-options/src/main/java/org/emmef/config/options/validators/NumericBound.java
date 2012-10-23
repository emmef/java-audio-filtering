package org.emmef.config.options.validators;

import org.emmef.config.options.Validator;

public class NumericBound<T extends Number & Comparable<T>> implements Validator<T> {
	public static enum Type { RESTRICTED, BETWEEN }

	private T minimum;
	private T maximum;
	private final Type type; 
	
	public NumericBound(T bound1, T bound2, Type type) {
		if (bound1 == null) {
			throw new NullPointerException("bound1");
		}
		if (bound2 == null) {
			throw new NullPointerException("bound2");
		}
		if (type == null) {
			throw new NullPointerException("type");
		}
		if (bound1.compareTo(bound2) < 0) {
			this.minimum = bound1;
			this.maximum = bound2; 
		}
		else {
			this.minimum = bound2;
			this.maximum = bound1; 
		}		
		this.type = type;
	}

	@Override
	public Validator.Result<T> check(T value) {
		if (value == null) {
			throw new NullPointerException("value");
		}
		if (value.compareTo(minimum) < 0) {
			return Validator.Result.create(type == Type.BETWEEN ? minimum : null, "Value (" + value + ") less than minimum (" + minimum + ")"); 
		}
		if (value.compareTo(maximum) > 0) {
			return Validator.Result.create(type == Type.BETWEEN ? maximum : null, "Value (" + value + ") greater than maximum (" + maximum + ")"); 
		}
		
		return Validator.Result.create(value);
	}
	
	@Override
	public String toString() {
		return type + "[" + minimum + ", " + maximum + "]";
	}

}
