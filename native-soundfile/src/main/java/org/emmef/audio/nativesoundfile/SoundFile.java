package org.emmef.audio.nativesoundfile;

import java.io.IOException;

import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.frame.FrameType;
import org.emmef.audio.frame.Whence;
import org.emmef.audio.nativesoundfile.SoundFileType.InfoStructure;
import org.emmef.audio.nodes.SoundSourceAndSink;
/**
 * Part of the SoundFile project 
 * @author michelf (original)
 * Created Aug 18, 2004
 * @author $Author: michelf $ (last modified)
 * $Revision: 1.3 $
 */
public class SoundFile implements SoundSourceAndSink<SoundFileType> {
	final static int WHENCE_SET = 1;
	final static int WHENCE_CURRENT = 2;
	final static int WHENCE_FROM_END = 3;
	private final static Object[] ioLock = new Object[0];
	
	public enum Mode { READONLY, WRITEONLY };

	public enum NativeWhence { 
		SET(SoundFile.WHENCE_SET), CURRENT(SoundFile.WHENCE_CURRENT), END(SoundFile.WHENCE_FROM_END);
		
		public final int value; 
		
		private NativeWhence(int value) {
			this.value = value;
		}
		
		static NativeWhence from(Whence whence) {
			switch (whence) {
			case SET:
				return NativeWhence.SET;
			case FROM_CURRENT:
				return NativeWhence.CURRENT;
			case FROM_END:
				return NativeWhence.END;
			}
			throw new IllegalArgumentException("Invalid argument for seek-whence: " + whence);
		}
	}	
	private final SoundFileType format;
	private final long frameCount;
	private final long handle;
	private final String fileName;
	private final Mode mode;
	private final boolean isSeekable;
	private boolean closed;
    
    public SoundFile(String fileName) throws IOException {
    	if (fileName == null) {
    		throw new IllegalArgumentException("Parameter 'filename' cannot be null");
    	}
    	SoundFileType.InfoStructure info = new SoundFileType.InfoStructure();
    	final long handle;
    	synchronized (ioLock) {
    		handle = openReadable(fileName, info);
		}
    	boolean finished = false;
    	if (handle == 0) {
    		throw new IllegalStateException("Invalid file descriptor");
    	}
    	try {
    		
        	this.handle = handle;
        	this.fileName = fileName;
        	this.mode = Mode.READONLY;
        	this.format = new SoundFileType(info);
        	this.frameCount = info.frames;
        	this.isSeekable = info.seekable;
        	finished = true;
    	}
    	finally {
    		if (!finished) {
    			close();
    		}
    	}
    }
    
    public SoundFile(String fileName, SoundMetrics<SoundFileType> formatInfo) throws IOException {
    	if (fileName == null) {
    		throw new IllegalArgumentException("Parameter 'filename' cannot be null");
    	}
    	if (formatInfo == null) {
    		throw new IllegalArgumentException();
    	}
    	final long handle;
    	InfoStructure info = formatInfo.getFormat().createInfoStructure();
    	synchronized (ioLock) {
    		handle = openWriteable(fileName, info);
		}
    	boolean finished = false;
    	if (handle == 0) {
    		throw new IllegalStateException("Invalid file descriptor");
    	} 
    	try {
	    	this.handle = handle;
	    	this.format = new SoundFileType(info);
	    	this.fileName = fileName;
	    	this.mode = Mode.WRITEONLY;
	    	this.isSeekable = info.seekable;
	    	this.frameCount = info.frames;
	    	finished = true;
    	}
    	finally {
    		if (!finished) {
    			close();
    		}
    	}
    }
    
    @Override
    public synchronized void close() throws IOException {
    	synchronized (ioLock) {
    		closed |= closeHandle(handle);
		}
    }
    
    @Override
    public FrameType getFrameType() {
    	return new FrameType(format.getChannels(), format.getSampleRate());
    }
    
    @Override
    public SoundFileType getBinaryFormat() {
    	return new SoundFileType(format.getFormat(), format.getChannels(), format.getSampleRate());
    }
    
    @Override
    public SoundMetrics<SoundFileType> createInfo() {
    	return new SoundMetrics<SoundFileType>(format.getChannels(), format, 0L, format.getSampleRate(), false, SoundFileType.class);
    }
    
    @Override
    public long getFrameCount() {
    	return frameCount;
    }
    
    @Override
    public boolean isSeekable() {
    	return isSeekable;
    }
    
    @Override
    public long readFrames(double[] buffer) throws IOException {
    	checkReadable();
    	synchronized (ioLock) {
    		return readDouble(handle, buffer, format.getChannels(), buffer.length / format.getChannels());
		}
    }
    
    @Override
    public long readFrames(float[] buffer) throws IOException {
    	checkReadable();
    	synchronized (ioLock) {
    		return readFloat(handle, buffer, format.getChannels(), buffer.length / format.getChannels());
		}
    }
    
    @Override
    public long readFrames(double[] buffer, int frameCount) throws IOException {
    	checkReadable();
    	synchronized (ioLock) {
    		return readDouble(handle, buffer, format.getChannels(), frameCount);
		}
    }
    
    @Override
    public long readFrames(float[] buffer, int frameCount) throws IOException {
    	checkReadable();
    	synchronized (ioLock) {
    		return readFloat(handle, buffer, format.getChannels(), frameCount);
		}
    }
    
    @Override
    public long writeFrames(double[] buffer) throws IOException {
    	checkWritable();
    	synchronized (ioLock) {
    		return writeDouble(handle, buffer, format.getChannels(), buffer.length / format.getChannels());
		}
    }
    
    @Override
    public long writeFrames(float[] buffer) throws IOException {
    	checkWritable();
    	synchronized (ioLock) {
    		return writeFloat(handle, buffer, format.getChannels(), buffer.length / format.getChannels());
		}
    }
    
    @Override
    public long writeFrames(double[] buffer, int frameCount) throws IOException{
    	checkWritable();
    	synchronized (ioLock) {
    		return writeDouble(handle, buffer, format.getChannels(), frameCount);
		}
    }
    
    @Override
    public long writeFrames(float[] buffer, int frameCount) throws IOException {
    	checkWritable();
    	synchronized (ioLock) {
    		return writeFloat(handle, buffer, format.getChannels(), frameCount);
		}
    } 
    
    @Override
    public long seekFrame(long framePosition, Whence whence) throws IOException {
    	if (isSeekable) {
        	synchronized (ioLock) {
        		return seek(handle, framePosition, NativeWhence.from(whence));
    		}
    	}
    	else {
    		throw new IllegalStateException("File is not seekable");
    	}
    }
    
    public Mode getMode() {
    	return mode;
    }
    
    public void checkReadable() {
    	if (mode == Mode.WRITEONLY) {
    		throw new IllegalStateException("Write-only");
    	}
    }
    
    public void checkWritable() {
    	if (mode == Mode.READONLY) {
    		throw new IllegalStateException("Read-only");
    	}
    }

    @Override
    public int hashCode() {
    	return (closed ? -1 : 0) ^ (int)(handle >> 32) ^ (int)(handle & 0xFFFFFFFF);
    }
    
    @Override
    public boolean equals(Object o) {
    	return closed == ((SoundFile)o).closed && handle == ((SoundFile)o).handle;
    }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + "[" + getBinaryFormat() + "; " + mode + "; \"" + fileName + "\"]";
    }
    
    @Override
    protected void finalize() throws Throwable {
    	close();
    }
    
	private static native long openWriteable(String filename, SoundFileType.InfoStructure info) throws IOException;
	private static native long openReadable(String filename, SoundFileType.InfoStructure info) throws IOException;
	private static native boolean closeHandle(long handle) throws IOException;
	
	private static native long readDouble(long handle, double[] buffer, int channels, long count) throws IOException;
	private static native long readFloat(long handle, float[] buffer, int channels, long count) throws IOException;
	private static native long readInteger(long handle, int[] buffer, int channels, long count) throws IOException;
	private static native long readShort(long handle, short[] buffer, int channels, long count) throws IOException;
	private static native long writeDouble(long handle, double[] buffer, int channels, long count) throws IOException;
	private static native long writeFloat(long handle, float[] buffer, int channels, long count) throws IOException;
	private static native long writeInteger(long handle, int[] buffer, int channels, long count) throws IOException;
	private static native long writeShort(long handle, short[] buffer, int channels, long count) throws IOException;
	private static native long seek(long handle, long position, NativeWhence whence) throws IOException;
    
	static {
		System.loadLibrary("SoundFileNative");
	}
}