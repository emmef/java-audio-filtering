package org.emmef.config.nativeloader;

public abstract class PlatformDependentLibraryName {
	private final String platformDescription;

	public PlatformDependentLibraryName(String platformDescription) {
		if (platformDescription == null) {
			throw new NullPointerException("platformDescription");
		}
		this.platformDescription = platformDescription;
	}
	
	/**
	 * Returns the non-{@code null} file name of the library.
	 * <p>
	 * The file name depends on the base name returned by
	 * {@link #getLibraryBaseName()} in a platform dependent manner.
	 * 
	 * @return a non-{@code null} {@link String}
	 */
	public final String getFileName(String libraryBaseName) {
		if (libraryBaseName == null) {
			throw new NullPointerException("libraryBaseName");
		}
		String original = getFileNameUnsafe(libraryBaseName);
		if (original != null) {
			return original;
		}
		
		throw new IllegalStateException("Library file name for \"" + libraryBaseName + "\" on platform \"" + platformDescription + "\" should not be null");
	}
	
	
	/**
	 * Returns the non-{@code null} platform description.
	 * <p>
	 * The file name depends on the base name returned by
	 * {@link #getLibraryBaseName()} in a platform dependent manner.
	 * 
	 * @return a non-{@code null} {@link String}
	 */
	public final String getPlatformDescription() {
		return platformDescription;
	}
	
	protected abstract String getFileNameUnsafe(String libraryBaseName2);
}
