package org.emmef.audio.nativesoundfile;

public final class MajorFormat {
	private final int format;
	private final String name;
	private final String extension;
	
	MajorFormat(FormatInfo info) {
		this.format = info.format;
		this.name = info.name;
		this.extension = info.extension;
	}
	
	public int getFormat() {
		return format;
	}
	
	public String getName() {
		return name;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public String toString() {
		return Integer.toHexString(getFormat()) + '\t' + getName() + '\t' + getExtension();
	}
}