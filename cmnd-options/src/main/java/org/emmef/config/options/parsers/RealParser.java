package org.emmef.config.options.parsers;

import org.emmef.config.options.Parser;

public class RealParser implements Parser<Double> {

	public static final Parser<Double> INSTANCE = new RealParser();

	@Override
	public Double parse(String argumentValue) {
		try {
			return Double.parseDouble(argumentValue);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
}
