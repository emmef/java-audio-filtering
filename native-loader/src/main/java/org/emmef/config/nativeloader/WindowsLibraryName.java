package org.emmef.config.nativeloader;

public class WindowsLibraryName extends PlatformDependentLibraryName {
	private final String architecture;

	public WindowsLibraryName(String architecture) {
		super("Windows-" + architecture);
		if (architecture == null) {
			throw new NullPointerException("architecture");
		}
		this.architecture = architecture;
	}
	
	@Override
	protected String getFileNameUnsafe(String libraryBaseName) {
		return libraryBaseName + "-" + architecture + ".dll";
	}
}
