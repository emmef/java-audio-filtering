package org.emmef.sndfile;


public final class SubFormat {
	private final int format;
	private final String name;
	
	SubFormat(FormatInfo info) {
		this.format = info.format;
		this.name = info.name;
	}
	
	public int getFormat() {
		return format;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return Integer.toHexString(getFormat()) + '\t' + getName();
	}
}