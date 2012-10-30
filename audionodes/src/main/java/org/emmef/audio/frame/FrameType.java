package org.emmef.audio.frame;

import org.emmef.audio.utils.Numbers;

public class FrameType {
	public final int channels;
	public final long sampleRate;
	
	public static String toString(int channels, long samplerate) {
		String channelText;
		if (channels == 2) {
			channelText = "stereo";
		}
		else if (channels == 1) {
			channelText = "mono";
		}
		else {
			channelText = "" + channels + "-channel";
		}
		String rateText = sampleRateToString(samplerate, ".", "", " ", " Hz");
		
		return channelText + " " + rateText + "Hz";
	}

	public static String sampleRateToString(long number, String decimalPoint, String powerPrefix, String separator, String unit) {
		return Numbers.appendNumber(new StringBuilder(), number, decimalPoint, powerPrefix, separator, unit).toString();
	}

	public static void main(String[] args) {
		System.out.println(sampleRateToString(8000, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(22500, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(44100, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(48000, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(96000, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(192000, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(1040, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(100, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(1104, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(10040, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(10004, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(10010340, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(10101010L, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(10100000L, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(101010101L, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(1010101010L, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(10101010101L, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(101010101010L, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(1010101010101L, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(10101010101010L, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(101010101010101L, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(1010101010101010L, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(10101010101010101L, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(101010101010101010L, ".", "e+", " ", "Hz"));
		System.out.println(sampleRateToString(1010101010101010101L, ".", "e+", " ", "Hz"));
	}
	
	public FrameType(int channels, long rate) {
		if (channels <= 0) {
			throw new IllegalArgumentException("Channels must be > 0");
		}
		else if (rate <= 0) {
			throw new IllegalArgumentException("Rate must be > 0");
		}
		else {
			this.channels = channels;
			this.sampleRate = rate;
		}
	}
	
	public String toString() {
		return toString(channels, sampleRate);
	}
}
