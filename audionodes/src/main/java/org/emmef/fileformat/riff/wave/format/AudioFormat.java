package org.emmef.fileformat.riff.wave.format;

import static org.emmef.fileformat.riff.wave.format.AudioFormats.checkFormat;
import static org.emmef.fileformat.riff.wave.format.AudioFormats.checkLocations;
import static org.emmef.fileformat.riff.wave.format.AudioFormats.checkSampleRate;

import java.util.Set;

import org.emmef.audio.frame.FrameType;

public final class AudioFormat extends FrameType {
	private final SampleFormat format;
	private final int bytesPerSample;
	private final int bitsPerSample;
	private final long locationMask;

	AudioFormat(SampleFormat format, long sampleRate, int channels, int bytesPerSample, int bitsPerSample, long locationMask) {
		super(channels, sampleRate);
		this.format = format;
		this.bytesPerSample = bytesPerSample;
		this.bitsPerSample = bitsPerSample;
		this.locationMask = locationMask;
	}

	/**
	 * Returns the type of sample format, which can be floating point or PCM
	 * (integers).
	 * 
	 * @return a non-{@code null} {@link SampleFormat}
	 */
	public SampleFormat getFormat() {
		return format;
	}

	/**
	 * Returns the sample rate (number of frames per second)
	 * @return a positive number
	 */
	public long getSampleRate() {
		return sampleRate;
	}

	/**
	 * Returns the number of channels. 
	 * <p>
	 * Each frame consists of this number of samples.
	 * @return a positive number
	 */
	public int getChannels() {
		return channels;
	}

	/**
	 * Returns the number of bytes that each sample takes up in storage.
	 * <p>
	 * This number is equal or bigger than eight times the number of valid bits
	 * per sample as returned by {@link #getValidBitsPerSample()}.
	 * 
	 * @return a positive number
	 */
	public int getBytesPerSample() {
		return bytesPerSample;
	}

	/**
	 * Returns the valid number of bits per sample.
	 * <p>
	 * The valid number of bits doesn't have to be a multiple of 8. However, for
	 * efficiency reasons, containers round the number of bits up to the next
	 * byte or even more. For example, a 20-bit sample is stored in 3 or even 4
	 * bytes.
	 * <p>
	 * Different {@link SampleFormat}s can allow different sets of valid bits.
	 * Floats, for instance, only allow 32 or 64 bits, while PCM allows
	 * everything up to 32 bits.
	 * 
	 * @return a positive number
	 */
	public int getValidBitsPerSample() {
		return bitsPerSample;
	}

	/**
	 * Returns a bit-mask of speaker locations for each frame.
	 * <p>
	 * The number of speaker locations is always equal to the number of
	 * channels.
	 * 
	 * @return a positive number.
	 */
	public long getLocationMask() {
		return locationMask;
	}

	static class Builder implements AudioFormatChannelSetter, AudioFormatSampleRateSetter, AudioFormatBitDepthSetter {
		private final SampleFormat format;
		private long sampleRate;
		private int channels;
		private int bytesPerSample;
		private int bitsPerSample;
		private long locationMask;

		Builder(SampleFormat format) {
			this.format = checkFormat(format);
		}

		@Override
		public AudioFormat validBits(int validBitsPerSample) {
			bytesPerSample = AudioFormats.getBytesPerSample(format, validBitsPerSample);
			bitsPerSample = validBitsPerSample;

			return new AudioFormat(format, sampleRate, channels, bytesPerSample, bitsPerSample, locationMask);
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
	}
}
