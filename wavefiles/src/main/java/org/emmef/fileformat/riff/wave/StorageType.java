package org.emmef.fileformat.riff.wave;

enum StorageType {
	WAVE_FORMAT_PCM(0x0001), 
	WAVE_FORMAT_IEEE_FLOAT(0x0003), 
	WAVE_FORMAT_EXTENSIBLE(0xFFFE);
	
	private final int formatValue;

	private StorageType(int formatValue) {
		this.formatValue = formatValue;
	}
	
	public int getFormatValue() {
		return formatValue;
	}
	
	public static final StorageType valueOf(int formatValue) {
		StorageType[] values = values();
		for (int i = 0; i < values.length; i++) {
			StorageType type = values[i];
			if (type.formatValue == formatValue) {
				return type;
			}
		}
		throw new IllegalArgumentException("No format type for " + formatValue);
	}
}
