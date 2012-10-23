package org.emmef.audio.nodes;

import org.emmef.audio.format.OpaqueFormat;

public interface SoundSourceAndSink<T extends OpaqueFormat> extends SoundSource<T>, SoundSink<T> {
	// combining interface
}
