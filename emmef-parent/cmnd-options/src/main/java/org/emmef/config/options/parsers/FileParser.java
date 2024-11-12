package org.emmef.config.options.parsers;

import java.io.File;

import org.emmef.config.options.Parser;

public class FileParser implements Parser<File> {
	public static final FileParser INSTANCE = new FileParser();

	@Override
	public File parse(String argumentValue) {
		return new File(argumentValue);
	}
}
