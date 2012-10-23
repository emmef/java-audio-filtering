package org.emmef.config.options;

import java.io.File;

public interface ValueBuilder {
	Value<String> text();
	Value<Boolean> flag();
	Value<File> file();
	
	NumberValue<Double> real();
	NumberValue<Integer> integer();
	NumberValue<Long> longint();
	
	<T> Value<T> value(Class<T> type, Parser<T> parser); 
	<T extends Number & Comparable<T>> NumberValue<T> number(Class<T> type, Parser<T> value);
	
	ValueBuilder mandatory();
}
