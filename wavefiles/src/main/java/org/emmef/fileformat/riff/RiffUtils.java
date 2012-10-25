package org.emmef.fileformat.riff;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.emmef.logging.Logger;

public class RiffUtils {
	public static final Charset RIFF_CHUNK_ID_CHARSET = Charset.forName("US-ASCII");

	public static String checkValidRiffChunkIdentifier(String chunkIdentifier) {
		RiffUtils.checkNotNull(chunkIdentifier, "chunkIdentifier");
		if (chunkIdentifier.length() != 4) {
			throw new IllegalArgumentException("chunkIdentifier length must be 4, not " + chunkIdentifier.length());
		}
		for (int i = 0; i < 4; i++) {
			char character = chunkIdentifier.charAt(i);
			if (!isValidChunkIdentifierCharacter(character)) {
				throw new IllegalArgumentException("Invalid character[" + i +"] in riff chunk identifier: " + Integer.toHexString(character));
			}
		}
		
		return chunkIdentifier;
	}
	
	public static String createValidRiffChunkIdentifier(char[] chunkIdentifier) {
		RiffUtils.checkNotNull(chunkIdentifier, "chunkIdentifier");
		if (chunkIdentifier.length < 4) {
			throw new IllegalArgumentException("chunkIdentifier length must be 4, not " + chunkIdentifier.length);
		}
		for (int i = 0; i < 4; i++) {
			char character = chunkIdentifier[i];
			if (isValidChunkIdentifierCharacter(character)) {
				throw new IllegalArgumentException("Invalid character[" + i +"] in riff chunk identifier: " + Integer.toHexString(character));
			}
		}
		
		return String.copyValueOf(chunkIdentifier, 0, 4);
	}
	
	public static String createValidRiffChunkIdentifier(byte[] chunkIdentifier) {
		RiffUtils.checkNotNull(chunkIdentifier, "chunkIdentifier");
		
		if (chunkIdentifier.length < 4) {
			throw new IllegalArgumentException("chunkIdentifier length must be 4, not " + chunkIdentifier.length);
		}
		
		for (int i = 0; i < 4; i++) {
			char character = (char)chunkIdentifier[i];
			if (isValidChunkIdentifierCharacter(character)) {
				throw new IllegalArgumentException("Invalid character[" + i +"] in riff chunk identifier: " + Integer.toHexString(character));
			}
		}
		
		return new String(chunkIdentifier, 0, 4, RIFF_CHUNK_ID_CHARSET);
	}

	public static boolean isValidChunkIdentifierCharacter(char character) {
		return character == 0 || character >= ' ' && character <= (char)126;
	}
	
	public static boolean isValidChunkIdentifierCharacter(byte character) {
		return character >= ' ' && character <= (byte)126;
	}
	
	public static RiffRootRecord readRootUnsafe(InputStream stream, byte[] buffer) throws IOException {
		String readChunkId = readChunkId(stream, buffer);
		if (!readChunkId.equals(RiffRootRecord.RIFF_IDENTIFIER)) {
			throw new IllegalStateException("Not a RIFF file: id=" + readChunkId);
		}
		long readChunkLength = readChunkLength(stream, buffer);
		String readIdentifier = readChunkId(stream, buffer);
		
		return new RiffRootRecord(readIdentifier, readChunkLength);
	}
	
	public static RiffChunk readChunkHeader(InputStream stream, byte[] buffer, RiffRecord parent, long relativeOffset) throws IOException {
		String readIdentifier = readChunkId(stream, buffer);
		long readChunkLength = readChunkLength(stream, buffer);
		
		return new RiffChunk(parent, readIdentifier, relativeOffset, readChunkLength);
	}
	
	public static byte[] readChunkData(InputStream stream, RiffChunk chunk, int warnLength, int maxLength) throws IOException {
		long contentLength = chunk.getContentLength();
		if (contentLength > warnLength) {
			Logger.getDefault().warn("Large size for cached chunk \"%s\": %d", chunk.getIdentifier(), contentLength);
		}
		if (contentLength > maxLength) {
			throw new IllegalArgumentException("Size for cached chunk \"" + chunk.getIdentifier() + "\" too large: " + contentLength);
		}
		int reads = (int)contentLength;
		byte[] buffer = new byte[reads];
		readBytes(stream, buffer, reads);
		
		return buffer;
	}	
	
	public static long skiprecord(InputStream stream, byte[] buffer, RiffRecord record) throws IOException {
		checkNotNull(record, "record");
		
		long skips = record.getSkipToNext();
		long read = 0;
		
		while (read < skips) {
			long yetToSkip = skips - read;
			int toRead = (int)Math.min(yetToSkip, buffer.length); 
			readBytes(stream, buffer, toRead);
			read += toRead;
		}
		
		return skips;
	}

	public static String readChunkId(InputStream stream, byte[] buffer) throws IOException {
		readBytes(stream, buffer, 4);
		char[] characters = new char[4];
		for (int i = 0; i < 4; i++) {
			if (isValidChunkIdentifierCharacter(buffer[i])) {
				characters[i] = (char)buffer[i];
			}
		}
		
		return new String(characters);
	}
	
	public static long readChunkLength(InputStream stream, byte[] buffer) throws IOException {
		return readFourByteUnsigned(stream, buffer);
	}

	public static long readFourByteUnsigned(InputStream stream, byte[] buffer) throws IOException {
		readBytes(stream, buffer, 4);
		
		long result = 0xff & buffer[3];
		result <<= 8;
		result += 0xff & buffer[2];
		result <<= 8;
		result += 0xff & buffer[1];
		result <<= 8;
		result += 0xff & buffer[0];
		
		return result;
	}
	
	public static int readTwoByteUnsigned(InputStream stream, byte[] buffer) throws IOException {
		readBytes(stream, buffer, 2);
		
		return buffer[0] | (buffer[1] << 8);
	}
	
	public static int readByteUnsigned(InputStream stream) throws IOException {
		checkNotNull(stream, "stream");
		int value = stream.read();
		if (value < 1) {
			throw new IOException("Unexpected end of input (Couldn't read byte)");
		}
		
		return value & 0xff;
	}

	public static void readBytes(InputStream stream, byte[] buffer, int count) throws IOException {
		checkNotNull(stream, "stream");
		checkNotNull(buffer, "buffer");
		
		if (buffer.length >= count) {
			if (stream.read(buffer, 0, count) < count) {
				throw new IOException("Unexpected end of input (Couldn't read " + count + " bytes)");
			}
		}
	}
	
	public static <T> T checkNotNull(T object, String errorMessage) {
		if (object != null) {
			return object;
		}
		throw new NullPointerException(errorMessage);
	}
}
