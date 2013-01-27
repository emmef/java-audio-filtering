package org.emmef.audio.noisereduction;

import java.net.URI;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.audio.nodes.SoundSource;
import org.emmef.audio.servicemanager.SoundSourceAndSinkManager;

public class SourceAndSinkProvider {
	private static final SoundSourceAndSinkManager manager = SoundSourceAndSinkManager.getInstance();
	
	static {
		manager.loadFromServiceLoader();
		StringBuilder builder = new StringBuilder("Source & Sink Providers:\n");
		System.out.println(manager.appendProviders(builder).toString());
	}

	public static SoundSourceAndSinkManager getInstance() {
		return manager;
	}
	
	public static SoundSource createSource(URI sourceUri) {
		return manager.createSource(sourceUri, 0);
	}

	public static SoundSink createSink(URI sourceUri, AudioFormat format) {
		return manager.createSink(sourceUri, format, 0);
	}

	public static SoundSink createWithSameMetaData(SoundSource source, URI targetUri) {
		return manager.createWithSameFormat(source, targetUri);
	}
}
