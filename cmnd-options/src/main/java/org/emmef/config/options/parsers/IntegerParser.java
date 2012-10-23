package org.emmef.config.options.parsers;


public abstract class IntegerParser extends OrdinalParser<Integer>  {
	public static final IntegerParser DECIMAL = new IntegerParser() {
		@Override
		protected int getRadix(String value) {
			return 10;
		}
	};
	
	public static final IntegerParser HEXADECIMAL = new IntegerParser() {
		protected int getRadix(String value) {
			return 16;
		}
	};
	
	public static final IntegerParser BOTH = new IntegerParser() {
		protected int getRadix(String value) {
			return value.startsWith("0x") ? 16 : 10;
		}
	};
	
	@Override
	public Integer parse(String argumentValue) {
		try {
			return Integer.parseInt(argumentValue, getRadix(argumentValue));
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
}
