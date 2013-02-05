package org.emmef.fileformat.riff.wave;

import static com.google.common.base.Preconditions.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.frame.Whence;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.fileformat.iff.ContentChunk;
import org.emmef.fileformat.iff.DefinitionInfo;
import org.emmef.fileformat.iff.InterchangeChunk;
import org.emmef.fileformat.iff.InvalidContentTypeIdentfierException;
import org.emmef.fileformat.iff.TypeChunk;
import org.emmef.fileformat.iff.parse.Parser;
import org.emmef.fileformat.riff.RiffBuilderFactory;
import org.emmef.fileformat.riff.WaveBuilderFactory;
import org.emmef.samples.codec.FrameWriter;
import org.emmef.samples.codec.SampleCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WaveFileWriter implements SoundSink {
	private static final Logger log = LoggerFactory.getLogger(WaveFileWriter.class);

	private final Object lock = new Object[0];
	private final TypeChunk riffWave;
	private final ContentChunk dataChunk;
	private final ContentChunk factChunk;
	private final AudioFormatChunk formatChunk;
	private final FileOutputStream stream;
	private final SampleCodec codec;
	private final FrameWriter frameWriter;
	private final AudioFormat audioFormat;
	private long maxFramePosition;
	private final File file;

	public WaveFileWriter(File file, AudioFormat audioFormat, int bufferSize) throws IOException, InvalidContentTypeIdentfierException {
		this.audioFormat = audioFormat;
		checkNotNull(file, "file");
		checkNotNull(audioFormat, "audioFormat");
		this.file = file;
		
		riffWave = RiffBuilderFactory.INSTANCE.createBuilder(false)
				.setContentType(RiffBuilderFactory.WAVE_CONTENT_TYPE)
				.build();
		formatChunk = AudioFormatChunk.fromFormat(riffWave, audioFormat);
		factChunk = InterchangeChunk.contentBuilder(WaveBuilderFactory.FACT_DEFINITION, false)
				.contentLength(4) // DWORD number of frames
				.sibling(formatChunk.getChunk())
				.build();
		dataChunk = InterchangeChunk.contentBuilder(WaveBuilderFactory.DATA_DEFINITION, false)
				.contentLength(0)
				.sibling(factChunk)
				.build();
		
		Parser.validateChain(Arrays.asList(riffWave, formatChunk.getChunk(), factChunk, dataChunk));
		
		codec = WaveFileUtil.selectCodec(audioFormat);
		
		boolean openSuccess = false;
		stream = new FileOutputStream(file);
		try {
			frameWriter = new FrameWriter(audioFormat.getChannels(), stream, codec, bufferSize, lock);
			unsafeCommit(true);
			
			openSuccess = true;
			log.debug("WAVE OUT \"{}\"; {}", file, AudioFormatChunks.fromChunks(formatChunk));
		}
		finally {
			if (!openSuccess) {
				log.error("Couldn't open for writing " + file);
				stream.close();
			}
		}
	}
	
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}
	
	@Override
	public SoundMetrics getMetrics() {
		return new SoundMetrics(audioFormat, frameWriter.getFramePosition(), false);
	}

	@Override
	public Object getMetaData() {
		return null;
	}

	@Override
	public long seekFrame(long framePosition, Whence whence) throws IOException {
		synchronized (lock) {
			long pos = unsafeCommit(false);
			long newPos;
			switch (whence) {
			case FROM_CURRENT:
				newPos = pos + framePosition;
				break;
			case FROM_END:
				newPos = maxFramePosition - framePosition;
				break;
			case SET:
				newPos = framePosition;
				break;
			default:
				throw new IllegalArgumentException("Unhandled " + Whence.class.getSimpleName() + " " + whence);
			}
			if (newPos > maxFramePosition) {
				throw new IllegalArgumentException("Position " + framePosition  + " " + whence + " is beyond end of output");
			}
			if (newPos < 0) {
				throw new IllegalArgumentException("Position " + framePosition  + " " + whence + " is before start of output");
			}
			long actualOffset = dataChunk.getOffset() + DefinitionInfo.CONTENT_RELATIVE_OFFSET + newPos * frameWriter.getBytesPerFrame();
			stream.getChannel().position(actualOffset);
		}
		return 0;
	}

	@Override
	public void close() throws IOException {
		synchronized (lock) {
			unsafeCommit(false);
			stream.close();
		}
	}
	
	public long commit(boolean truncate) throws IOException {
		synchronized (lock) {
			return unsafeCommit(truncate);
		}
	}

	@Override
	public long writeFrames(double[] source) throws IOException {
		return frameWriter.writeFrame(source, 0, source.length / audioFormat.getChannels());
	}

	@Override
	public long writeFrames(float[] source) throws IOException {
		return frameWriter.writeFrame(source, 0, source.length / audioFormat.getChannels());
	}

	@Override
	public long writeFrames(double[] source, int frameCount) throws IOException {
		return frameWriter.writeFrame(source, 0, frameCount);
	}

	@Override
	public long writeFrames(float[] source, int frameCount) throws IOException {
		return frameWriter.writeFrame(source, 0, frameCount);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(type=" + audioFormat + "; file=\"" + file.getAbsolutePath() + "\")";
	}

	private long unsafeCommit(boolean truncate) throws IOException {
		long framePosition = frameWriter.flush();
		FileChannel channel = stream.getChannel();
		if (truncate) {
			maxFramePosition = framePosition;
			channel.truncate(channel.position());
		}
		else {
			maxFramePosition = Math.max(maxFramePosition, framePosition);
		}
		dataChunk.setContentLength(maxFramePosition * audioFormat.getChannels() * codec.bytesPerSample());
		;
		channel.position(0);
		riffWave.write(stream);
		formatChunk.getChunk().write(stream);
		factChunk.setDWordAt((int)maxFramePosition, 0);
		dataChunk.write(stream);
		stream.flush();
		log.debug("Commit(maxPosition={}; data-length={}; riff-length={};fact-frames={}", maxFramePosition, dataChunk.getContentLength(), riffWave.getContentLength(), factChunk.getDWordAt(0));
		channel.position(dataChunk.getOffset() + DefinitionInfo.CONTENT_RELATIVE_OFFSET + framePosition * frameWriter.getBytesPerFrame());
		return framePosition;
	}
}
