package org.emmef.fileformat.riff.wave;

import org.emmef.audio.format.AudioFormat;
import org.emmef.samples.codec.SampleCodec;
import org.emmef.samples.codec.SampleCodecs;

public class WaveFileUtil {

	public static SampleCodec selectCodec(AudioFormat audioFormat) {
		switch (audioFormat.getSampleFormat()) {
		case FLOAT:
			switch (audioFormat.getBytesPerSample()) {
			case 8 :
				return SampleCodecs.DOUBLE;
			case 4:
				if (audioFormat.getValue0Dbf() == 1.0) {
					return SampleCodecs.FLOAT;
				}
				else {
					return SampleCodecs.FLOAT_COOLEDIT;
				}
			}
		case PCM:
			if (audioFormat.getValidBitsPerSample() == 8 * audioFormat.getBytesPerSample()) {
				switch (audioFormat.getValidBitsPerSample()) {
				case 8:
					return SampleCodecs.UNSIGNED_8;
				case 16:
					return SampleCodecs.SIGNED_16;
				case 24:
					return SampleCodecs.PACKED_24;
				case 32:
					return SampleCodecs.SIGNED_32;
				case 64:
					return SampleCodecs.SIGNED_64;
				default:
					throw new IllegalStateException("Unsupported interger format (bytes=" + audioFormat.getBytesPerSample() + ")");
				}
			}
			if (audioFormat.getValidBitsPerSample() == 24 && audioFormat.getBytesPerSample() == 4) {
				return SampleCodecs.POST_PADDED_24;
			}
			break;
		}
		throw new IllegalStateException("Unsupported format " + audioFormat.getSampleFormat());
	}
}
