package org.emmef.config.options.parsers;

import org.emmef.config.options.Parser;

public abstract class OrdinalParser<T extends Number> implements Parser<T> {

	protected abstract int getRadix(String value);
}
