package org.emmef.audio.frame;

public class FrameType {
	public final int channels;
	public final int samplerate;
	
	public static String toString(int channels, int samplerate) {
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
		String rateText;
		if (samplerate < 10000) {
			rateText = Integer.toString(samplerate);
		}
		else {
			int kHerz = samplerate / 1000;
			int hz = samplerate % 1000;
			if (hz == 0) {
				rateText = Integer.toString(kHerz);
			}
			else {				
				rateText = Integer.toString(kHerz) + ".";
				if (hz % 100 == 0) {
					rateText += hz / 100;
				}
				else if (hz % 10 == 0) {
					if (hz > 100) {
						rateText += hz / 10;
					}
					else {
						rateText += "0" + hz / 10;
					}
				}
				else if (hz > 100) {
					rateText += hz;
				}
				else if (hz > 10) {
					rateText += "0" + hz;
				}
				else {
					rateText += hz;
				}
			}
			rateText += "k";
		}
		
		return channelText + " " + rateText + "Hz";
	}
	
	public FrameType(int channels, int rate) {
		if (channels <= 0) {
			throw new IllegalArgumentException("Channels must be > 0");
		}
		else if (rate <= 0) {
			throw new IllegalArgumentException("Rate must be > 0");
		}
		else {
			this.channels = channels;
			this.samplerate = rate;
		}
	}
	
	public String toString() {
		return toString(channels, samplerate);
	}
}
