package org.emmef.fileformat.riff.wave;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.frame.Whence;
import org.emmef.audio.nodes.SoundSource;
import org.emmef.fileformat.riff.AudioFactChunk;
import org.emmef.fileformat.riff.RiffChunk;
import org.emmef.fileformat.riff.RiffDataChunk;
import org.emmef.fileformat.riff.RiffRootRecord;
import org.emmef.fileformat.riff.RiffUtils;
import org.emmef.samples.codec.FrameReader;
import org.emmef.samples.codec.SampleCodec;
import org.emmef.samples.codec.SampleCodecs;

class WaveFileReader implements SoundSource {
	
	private final byte[] buffer;
	private final List<RiffChunk> chunks = new ArrayList<>();
	private final AudioFormat audioFormat;
	private final InputStream stream;
	private final FrameReader frameReader;
	private final long frameCount;
	
	WaveFileReader(File file, int bufferSize) throws FileNotFoundException, IOException {
		RiffUtils.checkNotNull(file, "file");
		if (bufferSize < 16) {
			throw new IllegalArgumentException("Need a buffer of at least 16 bytes");
		}
		
		stream = new FileInputStream(file);
		boolean ready = false;
		try {
			buffer = new byte[128];
			
			RiffRootRecord root = RiffUtils.readRootUnsafe(stream, buffer);
			if (!WaveFileProvider.WAVE_FILE_FORMAT_IDENTIFIER.equals(root.getIdentifier())) {
				throw new IllegalStateException("File is not a Wave file: format identifier=\"" + root.getIdentifier() + "\"");
			}
			
			RiffChunk dataChunk = null;
			AudioFactChunk factChunk = null;
			AudioFormatChunk formatChunk = null;
			long offset = 0;
			while (dataChunk == null) {
				RiffChunk header = RiffUtils.readChunkHeader(stream, buffer, root, offset);
				if (WaveFileProvider.WAVE_AUDIO_DATA_IDENTIFIER.equals(header.getIdentifier())) {
					dataChunk = header;
					// currently, we are at the brink of reading actual data
					break;
				}
				if (AudioFormatChunk.WAVE_AUDIO_FORMAT_IDENTIFIER.equals(header.getIdentifier())) {
					if (formatChunk != null) {
						throw new IllegalStateException("Second audio format chunk found at file offset " + header.getAbsoluteOffset());
					}
					formatChunk = new AudioFormatChunk(header, RiffUtils.readChunkData(stream, header, 40, 40));
				}
				else if (AudioFactChunk.CHUNK_ID.equals(header.getIdentifier())) {
					if (factChunk != null) {
						throw new IllegalStateException("Second audio fact chunk found at file offset " + header.getAbsoluteOffset());
					}
					factChunk = new AudioFactChunk(header, RiffUtils.readChunkData(stream, header, 4, 40));
				}
				else {
					chunks.add(new RiffDataChunk(header, RiffUtils.readChunkData(stream, header, 10240, 1024000)));
				}
				offset += header.getHeaderLength() + header.getContentLength();
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

	private long obtainValidatedNumberOfFrames(RiffChunk dataChunk, AudioFactChunk factChunk, AudioFormatChunk formatChunk, AudioFormat audioFormat) {
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
				throw new IllegalStateException("Number of frames in " + factChunk.getIdentifier() + "(" + factFrames + ") larger than number in " + dataChunk.getIdentifier() + "(" + frameCount + ")");
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
		return null; // no additional meta data necessary.
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