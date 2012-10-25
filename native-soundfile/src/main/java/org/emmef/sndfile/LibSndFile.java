package org.emmef.audio.nativesoundfile;

import java.io.IOException;

import org.emmef.audio.format.FormatConverter;
import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.audio.nodes.SoundSource;
import org.emmef.config.nativeloader.LoaderResult;
import org.emmef.config.nativeloader.NativeLoader;

public class LibSndFile {
	private static final FormatConverter<SndFileType, SoundFileType> CONVERTER = new FormatConverter<SndFileType, SoundFileType>() {
		@Override
		public SndFileType publish(SoundFileType internalFormat) {
			return new SndFileType(internalFormat);
		}

		@Override
		public SoundFileType intern(SndFileType publishedFormat) {
			return (SoundFileType)publishedFormat.getValue();
		}
	};


	public static final SoundSource<SndFileType> readFrom(String fileName) throws IOException {
		ensureLibraryLoaded();
		
		return NodeDelegates.delegateSource(new SoundFile(fileName), CONVERTER);
	}
	
	public static final SoundSink<SndFileType> writeTo(String fileName, SoundMetrics metrics, SndFileType type) throws IOException {
		ensureLibraryLoaded();
		
		return NodeDelegates.delegateSink(new SoundFile(fileName, metrics, CONVERTER.intern(type)), CONVERTER);
	}
	
	
	private static final void ensureLibraryLoaded() {
		LoaderResult result = NativeLoader.loadLibrary(LibSoundFile.class, "SoundFileNative");
		if (!result.success()) {
			throw new IllegalStateException("Was not able to load native library: " + result);
		}
	}
}
