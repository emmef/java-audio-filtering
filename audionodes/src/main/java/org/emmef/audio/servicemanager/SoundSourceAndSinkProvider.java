package org.emmef.audio.servicemanager;

import java.net.URI;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.audio.nodes.SoundSource;

public interface SoundSourceAndSinkProvider {
	SoundSource createSource(URI sourceUri) throws SoundUriUnsupportedException;
	SoundSink createSink(URI sourceUri, AudioFormat format) throws SoundUriUnsupportedException, SoundFormatUnsupportedException;
	SoundSink createWithSameFormat(SoundSource source, URI targetUri);
}
