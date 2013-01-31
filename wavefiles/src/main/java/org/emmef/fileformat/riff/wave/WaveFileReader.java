package org.emmef.fileformat.riff.wave;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.frame.Whence;
import org.emmef.audio.nodes.SoundSource;
import org.emmef.fileformat.iff.ContentChunk;
import org.emmef.fileformat.iff.InterchangeChunk;
import org.emmef.fileformat.iff.InterchangeFormatException;
import org.emmef.fileformat.iff.parse.Parser;
import org.emmef.fileformat.riff.RiffTypeFactory;
import org.emmef.fileformat.riff.WaveBuilderFactory;
import org.emmef.logging.FormatLogger;
import org.emmef.samples.codec.FrameReader;
import org.emmef.samples.codec.SampleCodec;
import org.emmef.samples.codec.SampleCodecs;
import org.emmef.utils.Preconditions;

class WaveFileReader implements SoundSource, AutoCloseable {
	private static final FormatLogger log = FormatLogger.getLogger(WaveFileReader.class);
	private final AudioFormat audioFormat;
	private final InputStream stream;
	private final FrameReader frameReader;
	private final long frameCount;
	private final List<InterchangeChunk> readChunks;
	
	WaveFileReader(File file, int bufferSize) throws FileNotFoundException, IOException, InterchangeFormatException {
		Preconditions.checkNotNull(file, "file");
		
		stream = new FileInputStream(file);
		boolean ready = false;
		try {
			AudioFormatChunk formatChunk = null;
			AudioFactChunk factChunk = null;
			ContentChunk dataChunk = null;
			
			readChunks = Parser.readChunks(RiffTypeFactory.INSTACE, stream);
			
			for (InterchangeChunk chunk : readChunks) {
				log.debug("CHUNK %s", chunk);
				String chunkId = chunk.getDefinition().getIdentifier();
				if (WaveBuilderFactory.FMT_DEFINITION.getIdentifier().equals(chunkId)) {
					formatChunk = new AudioFormatChunk((ContentChunk)chunk);
				}
				if (chunkId.equals(WaveBuilderFactory.FACT_DEFINITION.getIdentifier())) {
					factChunk = new AudioFactChunk((ContentChunk)chunk);
				}
				else if (chunkId.equals(WaveBuilderFactory.DATA_DEFINITION.getIdentifier())) {
					dataChunk = (ContentChunk)chunk;
				}
			}
			if (dataChunk == null) {
				throw new IllegalArgumentException("Missing audio data chunk!");
			}
			if (formatChunk == null) {
				throw new IllegalArgumentException("Missing audio format chunk!");
			}
			
			audioFormat = AudioFormatChunks.fromChunks(formatChunk);
			frameCount = obtainValidatedNumberOfFrames(dataChunk, factChunk, formatChunk, audioFormat);
			frameReader = new FrameReader(audioFormat.getChannels(), bufferSize, stream, frameCount, selectDecode(formatChunk, audioFormat));
			ready = true;
		}
		finally {
			if (!ready) {
				stream.close();
			}
		}
	}

	private long obtainValidatedNumberOfFrames(ContentChunk dataChunk, AudioFactChunk factChunk, AudioFormatChunk formatChunk, AudioFormat audioFormat) {
		long frameCount;
			int bytesPerFrame;
			int bytesPerSample = audioFormat.getBytesPerSample();
			bytesPerFrame = audioFormat.getChannels() * bytesPerSample;
			if (formatChunk.getBytesPerFrame() != bytesPerFrame) {
				throw new IllegalStateException("Bytes per frame(" + bytesPerFrame + "), based on channels(" + audioFormat.channels + ") and bytes per sample (" + bytesPerSample + ") not same as in format chunk: " + formatChunk.getBytesPerFrame());
			}
			long dataLength = dataChunk.getContentLength();
			frameCount = dataLength / bytesPerFrame;
		if (factChunk != null) {
			long factFrames = factChunk.getSamplePerChannel();
			if (factFrames > frameCount) {
				throw new IllegalStateException("Number of frames in " + factChunk.getIdentifier() + "(" + factFrames + ") larger than number in " + dataChunk.getDefinition().getIdentifier() + "(" + frameCount + ")");
			}
			frameCount = factFrames;
		}
		return frameCount;
	}
	
	private static SampleCodec selectDecode(AudioFormatChunk formatChunk, AudioFormat audioFormat) {
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

	@Override
	public void close() throws IOException {
		stream.close();
	}
	
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}
	
	public FrameReader getFrameReader() {
		return frameReader;
	}

	@Override
	public SoundMetrics getMetrics() {
		return new SoundMetrics(audioFormat, frameCount, false);
	}

	@Override
	public Object getMetaData() {
		return readChunks;
	}

	@Override
	public long seekFrame(long framePosition, Whence whence) throws IOException {
		throw new UnsupportedOperationException("Seeking not yet supported");
	}

	@Override
	public long readFrames(double[] buffer) throws IOException {
		return readFrames(buffer, buffer.length / frameReader.getBytesPerFrame());
	}

	@Override
	public long readFrames(float[] buffer) throws IOException {
		return readFrames(buffer, buffer.length / frameReader.getBytesPerFrame());
	}

	@Override
	public long readFrames(double[] buffer, int frameCount)throws IOException {
		return frameReader.read(buffer, 0, frameCount);
	}

	@Override
	public long readFrames(float[] buffer, int frameCount) throws IOException {
		return frameReader.read(buffer, 0, frameCount);
	}
}