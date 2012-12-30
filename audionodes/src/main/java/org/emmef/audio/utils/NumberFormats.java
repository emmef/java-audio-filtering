package org.emmef.audio.utils;

public class NumberFormats {

	public static StringBuilder appendEngineerNumber(StringBuilder text, long number, String decimalPoint, String powerPrefix, String separator, String unit) {
		StringBuilder builder = text != null ? text : new StringBuilder();
		String units = "kMG";
		long nextPower = 1000;
		long divider = 1;
		int index = -1;
		while (nextPower < number) {
			nextPower *= 1000;
			divider *= 1000;
			index++;
		}
		
		long powerUnits = number / divider;
		builder.append(powerUnits);
		long remainder = number - divider * powerUnits;
		if (remainder != 0) {
			builder.append(decimalPoint);
			long multiplied = 10 * remainder;
			while (multiplied < divider) {
				builder.append('0');
				multiplied *= 10;
			}
			long divided = remainder / 10;
			long rest = remainder - divided * 10;
			while (rest == 0) {
				remainder = divided;
				divided = remainder / 10;
				rest = remainder - divided * 10;
			}
			builder.append(remainder);
		}
		if (index >= 0) {
			if (index < units.length()) {
				builder.append(separator);
				builder.append(units.charAt(index));
			}
			else {
				builder.append(powerPrefix).append(3 + 3*index);
				builder.append(separator);
			}
		}
		else {
			builder.append(separator);
		}
		builder.append(unit);
		
		return builder;
	}

}
