package org.emmef.fileformat.riff.wave;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.audio.nodes.SoundSource;
import org.emmef.audio.servicemanager.SoundFormatUnsupportedException;
import org.emmef.audio.servicemanager.SoundSourceAndSinkProvider;
import org.emmef.audio.servicemanager.SoundUriUnsupportedException;

public class WaveFileProvider implements SoundSourceAndSinkProvider {
	public static final String WAVE_FILE_FORMAT_IDENTIFIER = "WAVE";
	public static final String WAVE_AUDIO_DATA_IDENTIFIER = "data";
	public static final String WAVE_AUDIO_FACT_IDENTIFIER = "fact";
	
	public static final int DEFAULT_BUFFER_SIZE = 1048576;
	public static final int MINIMUM_BUFFER_SIZE = 102400;
	public static final int MAXIMUM_BUFFER_SIZE = 104857600;

	@Override
	public String toString() {
		return getClass().getName();
	}
	
	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public SoundSource createSource(URI sourceUri, int bufferHint) throws SoundUriUnsupportedException {
		checkUrlScheme(sourceUri);
		try {
			return new WaveFileReader(new File(sourceUri.getSchemeSpecificPart()), getBufferSize(bufferHint));
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
		catch (RuntimeException e) {
			throw new SoundUriUnsupportedException(e);
		}
	}
	
	@Override
	public SoundSink createSink(URI sourceUri, AudioFormat format, int bufferHint) throws SoundUriUnsupportedException, SoundFormatUnsupportedException {
		checkUrlScheme(sourceUri);
		try {
			throw new SoundUriUnsupportedException("No support for writing yet.");
//			return new WaveFileWriter(new File(sourceUri.getSchemeSpecificPart()), format, getBufferSize(bufferHint));
		}
//		catch (IOException e) {
//			throw new IllegalStateException(e);
//		}
		catch (RuntimeException e) {
			throw new SoundUriUnsupportedException(e);
		}
	}

	@Override
	public SoundSink createWithSameFormat(SoundSource source, URI targetUri) {
		// TODO Auto-generated method stub
		return null;
	}


	static int getBufferSize(int bufferHint) {
		if (bufferHint > 0) {
			return Math.min(MINIMUM_BUFFER_SIZE, Math.max(MAXIMUM_BUFFER_SIZE, bufferHint));
		}
		
		return DEFAULT_BUFFER_SIZE;
	}

	private static void checkUrlScheme(URI sourceUri) {
		if (!"file".equals(sourceUri.getScheme())) {
			throw new SoundUriUnsupportedException(sourceUri.toString());
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String pathname = "test.wav";
		if (args.length > 0) {
			pathname = args[0];
		}
		
		WaveFileReader waveFile = new WaveFileReader(new File(pathname), 1024);
		
		try {
			System.out.println(waveFile.getAudioFormat());
		}
		finally {
			waveFile.close();
		}
	}
}
