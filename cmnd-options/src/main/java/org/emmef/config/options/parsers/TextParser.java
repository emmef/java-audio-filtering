package org.emmef.config.options.parsers;

import org.emmef.config.options.Parser;

public class TextParser implements Parser<String>{

	public static final Parser<String> INSTANCE = new TextParser();

	@Override
	public String parse(String argumentValue) {
		return argumentValue;
	}

}
