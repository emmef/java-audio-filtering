package org.emmef.config.options.parsers;

import org.emmef.config.options.Parser;

public class BooleanParser implements Parser<Boolean> {
	public static final BooleanParser INSTANCE = new BooleanParser();
	
	@Override
	public Boolean parse(String argumentValue) {
		return Boolean.valueOf(argumentValue) || "1".equals(argumentValue);
	}

}
