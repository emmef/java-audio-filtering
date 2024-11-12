package org.emmef.config.options;

public interface Builder extends SwitchBuilder {
	ValueBuilder mandatory();
	void parse(String... commandline);
	String getCommandLineSummary();
	String getSynopsis();
}
