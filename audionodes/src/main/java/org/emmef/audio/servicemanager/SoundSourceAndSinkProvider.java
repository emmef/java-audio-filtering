package org.emmef.audio.servicemanager;

import java.net.URI;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.audio.nodes.SoundSource;

public interface SoundSourceAndSinkProvider {
	SoundSource createSource(URI sourceUri, int bufferHint) throws SoundUriUnsupportedException;
	SoundSink createSink(URI sourceUri, AudioFormat format, int bufferHint) throws SoundUriUnsupportedException, SoundFormatUnsupportedException;
	SoundSink createWithSameFormat(SoundSource source, URI targetUri);
	/**
	 * Returns the priority.
	 * 
	 * The higher the priority, the earlier the provider is loaded.
	 * 
	 * @return an integer.
	 */
	int getPriority();
}
