package org.emmef.fileformat.riff.wave;

import java.io.File;
import java.io.IOException;

import org.emmef.audio.format.AudioFormat;
import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.frame.Whence;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.fileformat.iff.ContentChunk;
import org.emmef.fileformat.iff.InterchangeChunk;
import org.emmef.fileformat.iff.InvalidContentTypeIdentfierException;
import org.emmef.fileformat.iff.TypeChunk;
import org.emmef.fileformat.riff.RiffBuilderFactory;
import org.emmef.fileformat.riff.WaveBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WaveFileWriter implements SoundSink {
	private static final Logger log = LoggerFactory.getLogger(WaveFileWriter.class);
	
	private final TypeChunk riffWave;
	private final ContentChunk dataChunk;
	private final ContentChunk factChunk;
	private final AudioFormatChunk formatChunk;


	public WaveFileWriter(File file, AudioFormat format, int bufferSize) throws IOException, InvalidContentTypeIdentfierException {
		riffWave = RiffBuilderFactory.INSTANCE.createBuilder(false)
				.setContentType(RiffBuilderFactory.WAVE_CONTENT_TYPE)
				.build();
		formatChunk = AudioFormatChunk.fromFormat(riffWave, format);
		factChunk = InterchangeChunk.contentBuilder(WaveBuilderFactory.FACT_DEFINITION, false)
				.contentLength(4) // DWORD number of frames
				.sibling(formatChunk.getChunk())
				.build();
		dataChunk = InterchangeChunk.contentBuilder(WaveBuilderFactory.DATA_DEFINITION, false)
				.contentLength(0)
				.sibling(factChunk)
				.build();
		
		log.info("Chunks\n:{}\n{}\n{}\n{}\n", riffWave, formatChunk.getChunk(), factChunk, dataChunk);
		log.info("WAVE OUT AudioFormat " + AudioFormatChunks.fromChunks(formatChunk));
	}

	@Override
	public SoundMetrics getMetrics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long seekFrame(long framePosition, Whence whence) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public long writeFrames(double[] buffer) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long writeFrames(float[] buffer) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long writeFrames(double[] buffer, int frameCount) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long writeFrames(float[] buffer, int frameCount) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
