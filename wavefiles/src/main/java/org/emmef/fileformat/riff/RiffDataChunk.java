package org.emmef.fileformat.riff;

import org.emmef.samples.serialization.Deserialize;
import org.emmef.samples.serialization.Serialize;

public class RiffDataChunk extends RiffChunk {
	private final byte[] buffer;

	public RiffDataChunk(RiffChunk source, byte[] buffer) {
		super(source);
		if (RiffUtils.checkNotNull(buffer, "buffer").length != source.getContentLength()) {
			throw new IllegalArgumentException("Length of buffer (" + buffer.length + ") must equal content length: " + source.getContentLength());
		}
		this.buffer = buffer;
	}
	
	protected final int getBufferSize() {
		return buffer.length;
	}
	
	protected final int getByteAt(int offset) {
		return 0xff & Deserialize.read8(buffer, offset);
	}
	
	protected final int getWordAt(int offset) {
		return 0xffff & Deserialize.read16LittleEndian(buffer, offset);
	}
	
	protected final long getDWordAt(int offset) {
		return 0xffffffffL & Deserialize.read32LittleEndian(buffer, offset);
	}
	
	protected final long getQWordAt(int offset) {
		return Deserialize.read64LittleEndian(buffer, offset);
	}
	
	protected final void setByteAt(int value, int offset) {
		Serialize.write08(value, buffer, offset);
	}
	
	protected final void setWordAt(int value, int offset) {
		Serialize.write16LittleEndian(value, buffer, offset);
	}
	
	protected final void setDWordAt(long value, int offset) {
		Serialize.write32LittleEndian((int)value, buffer, offset);
	}
	
	protected final void setWWordAt(long value, int offset) {
		Serialize.write64LittleEndian(value, buffer, offset);
	}
}
