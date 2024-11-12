package org.emmef.config.nativeloader;

public class PosixLibraryName extends PlatformDependentLibraryName {
	private final String architecture;

	public PosixLibraryName(String architecture) {
		super("posix-" + architecture);
		if (architecture == null) {
			throw new NullPointerException("architecture");
		}
		this.architecture = architecture;
	}
	
	@Override
	protected String getFileNameUnsafe(String libraryBaseName) {
		return "lib" + libraryBaseName + "-" + architecture + ".so";
	}
}
