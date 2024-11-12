package org.emmef.config.options.parsers;


public abstract class LongIntParser extends OrdinalParser<Long> {
	public static final LongIntParser DECIMAL = new LongIntParser() {
		@Override
		protected int getRadix(String value) {
			return 10;
		}
	};
	
	public static final LongIntParser HEXADECIMAL = new LongIntParser() {
		protected int getRadix(String value) {
			return 16;
		}
	};
	
	public static final LongIntParser BOTH = new LongIntParser() {
		protected int getRadix(String value) {
			return value.startsWith("0x") ? 16 : 10;
		}
	};
	

	@Override
	public Long parse(String argumentValue) {
		try {
			return Long.parseLong(argumentValue, getRadix(argumentValue));
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
}