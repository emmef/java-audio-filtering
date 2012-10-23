package org.emmef.audio.noisereduction;

public final class Buffer {
	private final double[] data;
	private final byte[] markers; 

	public Buffer(int sampleCount) {
		this.data = new double[sampleCount];
		this.markers = new byte[sampleCount];
	}
	
	public double[] getSamples() {
		return data;
	}
	
	public byte[] getMarkers() {
		return markers;
	}
	
	public int length() {
		return data.length;
	}

	public void clear() {
		for (int i = 0; i < data.length; i++) {
			data[i] = 0.0;
			markers[i] = 0;
		}
	}
}
