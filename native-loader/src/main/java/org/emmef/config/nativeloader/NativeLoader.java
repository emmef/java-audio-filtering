package org.emmef.config.nativeloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.emmef.logging.FormatLogger;

public class NativeLoader {
	private static final FormatLogger log = FormatLogger.getLogger(NativeLoader.class);
	
	private static final Object STATIC_LOCK = new Object[0];
	private static final File FAILED_INITIALIZATION = new File(UUID.randomUUID().toString());
	public static final String LIB_ROOT_DIR = ".native-loader.";
	
	private static File temporaryFileDirectory;
	private static final SortedSet<String> loadedLibraryBaseNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	
	public static List<PlatformDependentLibraryName> DEFAULT_PLATFORMS =
			Collections.unmodifiableList(Arrays.asList(new PlatformDependentLibraryName[] {
					new PosixLibraryName("amd64"), new PosixLibraryName("i386"),
					new WindowsLibraryName("i386"), new WindowsLibraryName("amd64")
			}));
	
	
	public static final LoaderResult loadLibrary(Class<?> locator, String libraryBaseName) {
		return loadLibrary(locator, libraryBaseName, DEFAULT_PLATFORMS);
	}
	
	public static final LoaderResult loadLibrary(Class<?> locator, String libraryBaseName, Iterable<PlatformDependentLibraryName> platforms) {
		if (locator == null) {
			throw new NullPointerException("locator");
		}
		if (libraryBaseName == null) {
			throw new NullPointerException("libraryBaseName");
		}
		if (platforms == null) {
			throw new NullPointerException("platforms");
		}
		
		File rootDirectory;
		
		synchronized (STATIC_LOCK) {
			if (loadedLibraryBaseNames.contains(libraryBaseName)) {
				return LoaderResult.ALREADY_LOADED;
			}
			rootDirectory = ensureLibraryRootDirectoryUnsafe();
		
			File libraryDir = new File(rootDirectory, libraryBaseName);
			
			if (libraryDir.exists() || libraryDir.mkdir()) {
				libraryDir.deleteOnExit();
				byte[] buffer = new byte[10240];
				for (PlatformDependentLibraryName platform : platforms) {
					String libraryName = platform.getFileName(libraryBaseName);
					InputStream library = locator.getResourceAsStream(libraryName);
					if (library == null) {
						continue;
					}
					File libraryFile = new File(libraryDir, libraryName);
					try {
						copyStreamToFile(library, libraryFile, buffer);
						if (loadLibrary(libraryFile)) {
							loadedLibraryBaseNames.add(libraryBaseName);
							return LoaderResult.NEWLY_LOADED;
						}
					}
					catch (IOException e) {
						return LoaderResult.IO_FAILURE;
					}
					finally {
						libraryFile.delete();
					}
				}
			}
		}
		
		return LoaderResult.UNAVAILABLE_FOR_PLATFORM;
	}

	private static void copyStreamToFile(InputStream sourceStream, File targetFile, byte[] buffer) throws FileNotFoundException, IOException {
		try {
			if (sourceStream != null) {
				FileOutputStream out = new FileOutputStream(targetFile);
				try {
					
					int reads = sourceStream.read(buffer);
					while (reads > 0) {
						out.write(buffer, 0, reads);
						reads = sourceStream.read(buffer);
					}
					out.flush();
				}
				finally {
					out.close();
				}
			}
		}
		finally {
			sourceStream.close();
		}
	}
	
	private static boolean loadLibrary(File libraryFile) {
		String libraryPath = libraryFile.getAbsolutePath();
		if (libraryFile.exists() && libraryFile.canRead()) {
			try {
				System.load(libraryPath);
				log.info("Successfully loaded %s", libraryPath);
				return true;
			}
			catch (SecurityException e) {
				log.debug("Failed to load %s: %s", libraryPath, e);
			}
			catch (UnsatisfiedLinkError e) {
				log.debug("Failed to load %s: %s", libraryPath, e);
			}
		}
		log.debug("Couldn't find or read %s", libraryPath);
		return false;
	}
	
	private static final File ensureLibraryRootDirectoryUnsafe() {
		synchronized (STATIC_LOCK) {
			if (temporaryFileDirectory == null) {
				temporaryFileDirectory = createLibraryRootDirectory();
				if (temporaryFileDirectory == null) {
					temporaryFileDirectory = FAILED_INITIALIZATION;
				}
			}
			if (temporaryFileDirectory != FAILED_INITIALIZATION) {
				return temporaryFileDirectory;
			}
			throw new IllegalStateException("Unable to initialze " + NativeLoader.class.getName());
		}
	}
	
	private static final File createLibraryRootDirectory() {
		try {
			File temporaryFile = getTemporayFileDirectory();
	
			File libraryRootDir = createLibraryRootDirIn(temporaryFile);
			if (libraryRootDir != null) {
				return libraryRootDir;
			}
		}
		catch (IOException e) {
			log.warn("Couldn't create library load directory in temp directory");
		}
		
		return createLibraryRootDirIn(new File("."));
	}

	private static File createLibraryRootDirIn(File temporaryFileDirectory) {
		File rootDir = new File(temporaryFileDirectory, LIB_ROOT_DIR + UUID.randomUUID().toString());
		
		if (rootDir.mkdir()) {
			rootDir.deleteOnExit();
			return rootDir;
		}
		
		return null;
	}

	private static File getTemporayFileDirectory() throws IOException {
		File file = File.createTempFile("nativeLoader", null);
		File temporaryFileDirectory = file.getAbsoluteFile().getParentFile();
		file.delete();
		return temporaryFileDirectory;
	}
	
}
