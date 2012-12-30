package org.emmef.audio.format;

import static org.emmef.audio.format.AudioFormats.*;

import java.util.Set;

import org.emmef.audio.frame.FrameType;
import org.emmef.audio.utils.NumberFormats;

public class AudioFormat extends FrameType implements SampleType {
	private final SampleFormat format;
	private final int bytesPerSample;
	private final int bitsPerSample;
	private final long locationMask;
	private final double value0Dbf;

	AudioFormat(SampleFormat format, long sampleRate, int channels, int bytesPerSample, int bitsPerSample, long locationMask, double value0Dbf) {
		super(channels, sampleRate);
		this.format = format;
		this.bytesPerSample = bytesPerSample;
		this.bitsPerSample = bitsPerSample;
		this.locationMask = locationMask;
		this.value0Dbf = value0Dbf;
	}

	@Override
	public final SampleFormat getSampleFormat() {
		return format;
	}

	@Override
	public final int getBytesPerSample() {
		return bytesPerSample;
	}
	
	@Override
	public final int getValidBitsPerSample() {
		return bitsPerSample;
	}

	@Override
	public final long getLocationMask() {
		return locationMask;
	}
	
	@Override
	public final double getValue0Dbf() {
		return value0Dbf;
	}
	
	@Override
	public String toString() {
		StringBuilder text = new StringBuilder(getClass().getSimpleName());
		text.append('[');
		SampleFormat sampleFormat = getSampleFormat();
		int validBitsPerSample = getValidBitsPerSample();
		text.append(validBitsPerSample).append("-bit ").append(sampleFormat).append(' ');
		NumberFormats.appendEngineerNumber(text, getSampleRate(), ".", "e+", "", "Hz");
		long mask = getLocationMask();
		text.append(' ');
		if (mask == 0) {
			int ch = getChannels();
			if (ch == 1) {
				text.append("Mono");
			}
			else if (ch == 2) {
				text.append("Stereo");
			}
			text.append(ch).append("-channel");
		}
		else {
			text.append(SpeakerLocations.ofMask(mask).toString());
		}
		double value0Dbf2 = getValue0Dbf();
		if (sampleFormat == SampleFormat.FLOAT && value0Dbf2 != 1.0) {
			text.append(" scale=").append(value0Dbf2);
		}
		if (((validBitsPerSample + 7) >> 3) != getBytesPerSample()) {
			text.append(" aligned=").append(getBytesPerSample());
		}
		
		text.append(']');
		// TODO Auto-generated method stub
		return text.toString();
	}

	static class Builder implements AudioFormatChannelSetter, AudioFormatSampleRateSetter, AudioFormatBitDepthSetter {
		private final SampleFormat format;
		private long sampleRate;
		private int channels;
		private int bytesPerSample;
		private int bitsPerSample;
		private long locationMask;
		private double scale = -1.0;;

		Builder(SampleFormat format) {
			this.format = checkFormat(format);
		}

		@Override
		public AudioFormat bitDepth(int validBitsPerSample) {
			bytesPerSample = AudioFormats.getBytesPerSample(format, validBitsPerSample);
			bitsPerSample = validBitsPerSample;
			if (scale < 0) {
				if (format == SampleFormat.FLOAT) {
					scale = 1.0;
				}
				else {
					scale = 1L << (bitsPerSample >> 1) - 1;
				}
			}

			return new AudioFormat(format, sampleRate, channels, bytesPerSample, bitsPerSample, locationMask, scale);
		}

		@Override
		public AudioFormatBitDepthSetter rate(long sampleRate) {
			this.sampleRate = checkSampleRate(sampleRate);

			return this;
		}

		@Override
		public AudioFormatBitDepthSetter cd() {
			return rate(44100);
		}

		@Override
		public AudioFormatBitDepthSetter dvd() {
			return rate(48000);
		}

		@Override
		public AudioFormatSampleRateSetter channels(int channels) {
			this.locationMask = SpeakerLocations.ofChannels(channels).getMask();
			this.channels = channels;

			return this;
		}

		@Override
		public AudioFormatSampleRateSetter setLocations(Set<SpeakerLocation> locations) {
			checkLocations(locations);
			this.locationMask = SpeakerLocation.toMask(locations);
			this.channels = locations.size();

			return this;
		}
		@Override
		public AudioFormatSampleRateSetter locations(SpeakerLocations locations) {
			if (locations == null) {
				throw new NullPointerException("Locations cannot be null");
			}
			this.locationMask = locations.getMask();
			this.channels = locations.size();
			
			return this;
		}
		
		@Override
		public AudioFormatSampleRateSetter mask(long mask) {
			SpeakerLocation.checkMask(mask);
			this.channels = SpeakerLocation.getNumberOfChannels(mask);
			this.locationMask = mask;
			
			return this;
		}
		
		@Override
		public AudioFormatBitDepthSetter set0DbfValue(double zeroDbSampleValue) {
			if (zeroDbSampleValue < 1.0) {
				throw new IllegalArgumentException("Zero dB value must positive");
			}
			if (format != SampleFormat.FLOAT) {
				throw new IllegalStateException("Zero dB value can only be set for floating point samples");
			}
			if (scale >= 0) {
				throw new IllegalStateException("Zero dB value already set to " + scale);
			}
			scale = zeroDbSampleValue;
			
			return this;
		}
	}
}
