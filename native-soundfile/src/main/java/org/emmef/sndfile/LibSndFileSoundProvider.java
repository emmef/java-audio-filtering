package org.emmef.sndfile;

import java.io.IOException;
import java.net.URI;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.audio.nodes.SoundSource;
import org.emmef.servicemanager.SoundSourceAndSinkProvider;

public class LibSndFileSoundProvider implements SoundSourceAndSinkProvider {

	@Override
	public SoundSource createSource(URI sourceUri) {
		if (!"file".equals(sourceUri.getScheme())) {
			return null;
		}
		try {
			return LibSndFile.readFrom(sourceUri.getSchemeSpecificPart());
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
		catch (RuntimeException e) {
			return null;
		}
	}

	@Override
	public SoundSink createSink(URI sourceUri, AudioFormat format) {
		return null;
	}

	@Override
	public SoundSink createWithSameMetaData(SoundSource source, URI targetUri) {
		if (!"file".equals(targetUri.getScheme())) {
			return null;
		}
		try {
			return LibSndFile.writeTo(targetUri.getSchemeSpecificPart(), source.getMetrics(), source.getMetaData());
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
		catch (RuntimeException e) {
			return null;
		}
	}

}
