package org.emmef.config.options;

import org.emmef.config.options.internal.DefaultBuilder;

public class Options {
	public static Builder create(String programName) {
		return new DefaultBuilder().describedBy(programName);
	}
}
