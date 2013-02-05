package org.emmef.fileformat.riff.wave;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.audio.nodes.SoundSource;
import org.emmef.audio.servicemanager.SoundFormatUnsupportedException;
import org.emmef.audio.servicemanager.SoundSourceAndSinkProvider;
import org.emmef.audio.servicemanager.SoundUriUnsupportedException;
import org.emmef.fileformat.iff.InterchangeFormatException;
import org.emmef.logging.FormatLogger;

public class WaveFileProvider implements SoundSourceAndSinkProvider {
	private static final FormatLogger log = FormatLogger.getLogger(WaveFileProvider.class);
	
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
			return new WaveFileReader(
					getFileFromUri(sourceUri),
					getBufferSize(bufferHint));
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
		catch (InterchangeFormatException e) {
			throw new SoundUriUnsupportedException(e);
		}
		catch (RuntimeException e) {
			throw new SoundUriUnsupportedException(e);
		}
	}
	
	@Override
	public SoundSink createSink(URI sourceUri, AudioFormat format, int bufferHint) throws SoundUriUnsupportedException, SoundFormatUnsupportedException {
		checkUrlScheme(sourceUri);
		try {
			return new WaveFileWriter(
					getFileFromUri(sourceUri),
					format,
					getBufferSize(bufferHint));
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
		catch (InterchangeFormatException e) {
			throw new SoundUriUnsupportedException(e);
		}
		catch (RuntimeException e) {
			throw new SoundUriUnsupportedException(e);
		}
	}

	@Override
	public SoundSink createWithSameFormat(SoundSource source, URI targetUri) {
		return createSink(targetUri, source.getMetrics().getAudioFormat(), DEFAULT_BUFFER_SIZE);
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

	private static File getFileFromUri(URI uri) throws UnsupportedEncodingException {
		return new File(URLDecoder.decode(uri.getSchemeSpecificPart(), "UTF-8"));
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, InterchangeFormatException {
		String pathname = "/home/michel/Music/high-definition/Test_File_2_0_STEREO_PCM.wav";
		if (args.length > 0) {
			pathname = args[0];
		}
		
		try (
				WaveFileReader waveFile = new WaveFileReader(new File(pathname), 102400);
				WaveFileWriter waveFileWriter = new WaveFileWriter(new File("/tmp/output.wav"), waveFile.getAudioFormat(), 102400)) {
			
			System.out.println(waveFile.getAudioFormat());
			long frames = 0;
			int channels = waveFile.getAudioFormat().getChannels();
			double square[] = new double[channels];
			double[] buffer = new double[1048576 * channels];
			long readFrames;
			StringBuilder rmsValues = new StringBuilder();
			while ((readFrames = waveFile.readFrames(buffer)) != 0) {
				frames += readFrames;
				int index = 0;
				for (long i = 0; i < readFrames; i++) {
					for (int channel = 0; channel < channels; channel++) {
						double x = buffer[index++];
						square[channel] += x*x;
//						buffer[index++] += 0.5 * (Math.random() - 0.5);
					}
				}
				rmsValues.setLength(0);
				rmsValues.append("RMS values for each channel:");
				for (int channel = 0; channel < channels; channel++) {
					rmsValues.append(' ').append(Math.sqrt(square[channel] / readFrames));
				}
				log.info("After %d frames, %s", frames, rmsValues);
				
				waveFileWriter.writeFrames(buffer, (int)readFrames);
			}
		}
	}
}
