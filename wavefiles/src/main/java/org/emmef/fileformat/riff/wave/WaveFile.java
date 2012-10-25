package org.emmef.fileformat.riff.wave;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.emmef.fileformat.riff.RiffChunk;
import org.emmef.fileformat.riff.RiffDataChunk;
import org.emmef.fileformat.riff.RiffRootRecord;
import org.emmef.fileformat.riff.RiffUtils;

public class WaveFile {
	public static final String WAVE_FILE_FORMAT_IDENTIFIER = "WAVE";
	public static final String WAVE_AUDIO_DATA_IDENTIFIER = "data";
	
	private final byte[] buffer;
	private final FileChannel channel;
	private final List<RiffChunk> chunks = new ArrayList<>();
	private final AudioFormatChunk formatChunk;
	private final RiffChunk dataChunk;
	
	public WaveFile(File file, int bufferSize) throws FileNotFoundException, IOException {
		RiffUtils.checkNotNull(file, "file");
		if (bufferSize < 16) {
			throw new IllegalArgumentException("Need a buffer of at least 16 bytes");
		}
		
		FileInputStream stream = new FileInputStream(file);
		
		this.buffer = new byte[bufferSize];
		
		RiffRootRecord root = RiffUtils.readRootUnsafe(stream, buffer);
		if (!WAVE_FILE_FORMAT_IDENTIFIER.equals(root.getIdentifier())) {
			throw new IllegalStateException("File is not a Wave file: format identifier=\"" + root.getIdentifier() + "\"");
		}
		
		RiffChunk dataChunk = null;
		AudioFormatChunk formatChunk = null;
		long offset = 0;
		while (dataChunk == null) {
			RiffChunk header = RiffUtils.readChunkHeader(stream, buffer, root, offset);
			if (WAVE_AUDIO_DATA_IDENTIFIER.equals(header.getIdentifier())) {
				dataChunk = header;
				break;
			}
			if (AudioFormatChunk.WAVE_AUDIO_FORMAT_IDENTIFIER.equals(header.getIdentifier())) {
				if (formatChunk != null) {
					throw new IllegalStateException("Second audio format chunk found at file offset " + header.getAbsoluteOffset());
				}
				formatChunk = new AudioFormatChunk(header, RiffUtils.readChunkData(stream, header, 40, 40));
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
		
		this.formatChunk = formatChunk;
		this.dataChunk = dataChunk;
		this.channel = stream.getChannel();
	}
	
	public void close() throws IOException {
		channel.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		WaveFile waveFile = new WaveFile(new File(args[0]), 1024);
		
		try {
			System.out.println(waveFile.formatChunk.getStorageType());
		}
		finally {
			waveFile.close();
		}
	}
}
