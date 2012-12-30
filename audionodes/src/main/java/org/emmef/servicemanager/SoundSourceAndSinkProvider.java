package org.emmef.servicemanager;

import java.net.URI;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.audio.nodes.SoundSource;
// org.emmef.servicemanager.SoundSourceProvider
public interface SoundSourceAndSinkProvider {
	SoundSource createSource(URI sourceUri);
	SoundSink createSink(URI sourceUri, AudioFormat format);
	SoundSink createWithSameMetaData(SoundSource source, URI targetUri);
}
