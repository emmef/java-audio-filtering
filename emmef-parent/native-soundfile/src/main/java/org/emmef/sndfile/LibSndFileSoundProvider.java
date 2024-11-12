package org.emmef.sndfile;

import java.io.IOException;
import java.net.URI;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.audio.nodes.SoundSource;
import org.emmef.audio.servicemanager.SoundFormatUnsupportedException;
import org.emmef.audio.servicemanager.SoundSourceAndSinkProvider;
import org.emmef.audio.servicemanager.SoundUriUnsupportedException;

public class LibSndFileSoundProvider implements SoundSourceAndSinkProvider {
	
	@Override
	public int getPriority() {
		return Integer.MIN_VALUE;
	}
	
	@Override
	public SoundSource createSource(URI sourceUri, int bufferHint) {
		if (!"file".equals(sourceUri.getScheme())) {
			throw new SoundUriUnsupportedException(sourceUri.toString());
		}
		try {
			return LibSndFile.readFrom(sourceUri.getSchemeSpecificPart());
		}
		catch (IOException e) {
			if (e.getMessage().contains("unknown format")) {
				throw new SoundFormatUnsupportedException(e);
			}
			throw new IllegalStateException(e);
		}
		catch (RuntimeException e) {
			return null;
		}
	}

	@Override
	public SoundSink createSink(URI sourceUri, AudioFormat format, int bufferHint) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SoundSink createWithSameFormat(SoundSource source, URI targetUri) {
		if (!"file".equals(targetUri.getScheme())) {
			throw new SoundUriUnsupportedException(targetUri.toString());
		}
		try {
			return LibSndFile.writeTo(targetUri.getSchemeSpecificPart(), source.getMetrics(), source.getMetaData());
		}
		catch (IOException e) {
			if (e.getMessage().contains("unknown format")) {
				throw new SoundFormatUnsupportedException(e);
			}
			throw new IllegalStateException(e);
		}
		catch (RuntimeException e) {
			return null;
		}
	}

}
