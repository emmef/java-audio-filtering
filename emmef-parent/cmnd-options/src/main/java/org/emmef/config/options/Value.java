package org.emmef.config.options;

import java.util.List;

public interface Value<T> {
	Value<T> validatedBy(Validator<T> validator);
	Value<T> defaults(@SuppressWarnings("unchecked") T... defaults);
	Value<T> multiple();
	Value<T> describedBy(String description);
	Value<T> name(String description);
	
	T getValue();
	T getValue(int index);
	List<T> values();
	
	String getDescription();
}
