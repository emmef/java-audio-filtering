package org.emmef.config.program;

import java.io.File;

import org.emmef.config.options.Validator;

public class ProgramUtils {
	public static final Validator<File> EXISTING_NORMAL_READABLE_FILE_FILTER = new Validator<File>() {

		public String toString() {
			return "Existing readable file";
		}

		public Validator.Result<File> check(File file) {
			if (!file.exists()) {
				return Result.create(null, "File does not exist: \"" + file.getAbsolutePath() + "\"");
			}
			if (!file.isFile()) {
				return Result.create(null, "File name represents a non-normal file (directory): \"" + file.getAbsolutePath() + "\"");
			}
			if (!file.canRead()) {
				return Result.create(null, "File is not readable: \"" + file.getAbsolutePath() + "\"");
			}
			
			return Result.create(file);
		}
	};
	
	public static Validator<File> EXISTING_WRITABLE_TARGET_DIRECTORY = new Validator<File>() {
		
		public Validator.Result<File> check(File file) {
			if (!file.exists()) {
				return Result.create(null, "Directory does not exist: \"" + file.getAbsolutePath() + "\"");
			}
			if (!file.isDirectory()) {
				return Result.create(null, "Directory name does not represent a directory: \"" + file.getAbsolutePath() + "\"");
			}
			if (!file.canWrite()) {
				return Result.create(null, "Directory is not writable: \"" + file.getAbsolutePath() + "\"");
			}
		
			return Result.create(file);
		}
		
		public String toString() {
			return "Existing writable directory";
		}
	}; 

	public static void main(Program program, String[] args) {
		if (program == null) {
			throw new NullPointerException("program");
		}
		if (args == null) {
			throw new NullPointerException("args");
		}
		try {
			program.run(args);
		}
		catch (Exception e) {
			System.out.println("*** EXCEPTION: " + e.getMessage() + "\n" + program.getSynopsis());
			e.printStackTrace();
		}
	}
	
	public static String checkFileName(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			throw new IllegalArgumentException("File does not exist: \"" + fileName + "\"");
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException("File name represents a non-normal file (directory): \"" + fileName + "\"");
		}
		if (!file.canRead()) {
			throw new IllegalArgumentException("File is not readable: \"" + fileName + "\"");
		}
	
		return fileName;
	}

	public static File checkTargetDirectory(String directoryName) {
		File file = new File(directoryName);
		if (!file.exists()) {
			throw new IllegalArgumentException("Directory does not exist: \"" + directoryName + "\"");
		}
		if (!file.isDirectory()) {
			throw new IllegalArgumentException("Directory name does not represent a directory: \"" + directoryName + "\"");
		}
		if (!file.canWrite()) {
			throw new IllegalArgumentException("Directory is not writable: \"" + directoryName + "\"");
		}
	
		return file;
	}

	public static int getInteger(String string, int min, int max) {
		final int value;
		try {
			value = Integer.valueOf(string);
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Argument should be an integer: \"" + string + "\"");
		}
		if (value < min || value > max) {
			throw new IllegalArgumentException("Value must be in [" + min + ".." + max + "]");
		}
		return value;
	}

	public static int getInteger(String string, int min, int max, int defaultValue) {
		final int value;
		try {
			value = Integer.valueOf(string);
		}
		catch (NumberFormatException e) {
			return defaultValue;
		}
		if (value < min || value > max) {
			return defaultValue;
		}
		return value;
	}

}
