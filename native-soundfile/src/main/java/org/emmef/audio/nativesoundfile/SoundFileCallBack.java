package org.emmef.audio.nativesoundfile;

import java.io.IOException;

public class SoundFileCallBack implements CallBack<double[]> {
	private final SoundFile soundFile;
	private final boolean close;

	public SoundFileCallBack(SoundFile soundFile, boolean close) {
		if (soundFile == null) {
			throw new IllegalArgumentException("Parameter 'file' cannot be null");
		}
		this.soundFile = soundFile;
		this.close = close;
	}

	public boolean callBack(double[] data) {
		final boolean result;
		try {
			if (soundFile.getMode() == SoundFile.Mode.READONLY) {
				result = action("Read", soundFile.readFrames(data)) > 0;
			}
			else {
				result = action("Wrote", soundFile.writeFrames(data)) > 0;
			}
		}
		catch(IOException exception) {
			throw new IllegalStateException(exception);
		}
		if (!result && close) {
			close();
		}
		
		return result;
	}
	
	private long action(String action, long result) {
		System.out.println(action + " " + result + " frames");
		return result;
	}
	
	public void close() {
		if (close) {
			try {
				soundFile.close();
			}
			catch (IOException exception) {
				new Throwable("IGNORED", exception).printStackTrace();
			}
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
	}
}
