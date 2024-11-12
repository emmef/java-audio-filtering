package org.emmef.config.options;

public interface Parser<T> {
	/**
	 * Parses the argument to the return type.
	 * @param argumentValue value of the (command-line) argument
	 * 
	 * @return the parsed value or <code>null</code> if it couldn't be parsed.
	 */
	T parse(String argumentValue);
}
