package org.emmef.fileformat.riff.wave;

enum FormatType {
	WAVE_FORMAT_PCM(0x0001),
	WAVE_FORMAT_IEEE_FLOAT(0x0003),
	WAVE_FORMAT_EXTENSIBLE(0xFFFE);
	
	private final int formatValue;

	private FormatType(int formatValue) {
		this.formatValue = formatValue;
	}
	
	public int getFormatValue() {
		return formatValue;
	}
	
	public static final FormatType valueOf(int formatValue) {
		for (FormatType type : values()) {
			if (type.formatValue == formatValue) {
				return type;
			}
		}
		throw new IllegalArgumentException("No format type for " + formatValue);
	}
}
