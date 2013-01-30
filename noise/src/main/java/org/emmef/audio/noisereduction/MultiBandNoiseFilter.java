package org.emmef.audio.noisereduction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.emmef.audio.frame.FrameType;
import org.emmef.audio.noisedetection.NoiseLevelDetectionFilter;
import org.emmef.audio.noisedetection.NoiseLevelDiscardFilter;
import org.emmef.audio.noisedetection.NrMeasurementSettings;
import org.emmef.logging.FormatLogger;
import org.emmef.logging.FormatLoggerFactory;


public class MultiBandNoiseFilter {
	private static final FormatLogger logger = FormatLoggerFactory.getLogger(MultiBandNoiseFilter.class);
	
	private final BandSplitFilterSet set;
	private final FrameType frameType;
	private final float[] samples;
	private final BufferSet buffers = new BufferSet();;
	
	public MultiBandNoiseFilter(float[] samples, FrameType frameType, NrMeasurementSettings nrMeasurements, NrDynamicsFactory nrDynamicsFactory, CrossoverInfo crossoverInfo, Timings timings) {
		this.frameType = frameType;
		if (samples == null) {
			throw new NullPointerException("samples");
		}
		if (frameType == null) {
			throw new NullPointerException("frameType");
		}
		if (crossoverInfo == null) {
			throw new NullPointerException("crossoverInfo");
		}
		if (samples.length == 0) {
			throw new IllegalArgumentException("Need at least one frame of samples");
		}
		if (samples.length % frameType.channels != 0) {
			throw new IllegalArgumentException("Number of samples must be multiple of the number of channels");
		}
		this.samples = samples;
		List<FilterFactory> filterFactories = new ArrayList<FilterFactory>();
		filterFactories.add(new MaxRmsDetectionFilter.Factory(frameType.sampleRate, nrMeasurements));
		filterFactories.add(new NoiseLevelDiscardFilter.Factory(frameType.sampleRate, nrMeasurements));
		filterFactories.add(new NoiseLevelDetectionFilter.Factory(frameType.sampleRate, nrMeasurements));
		RatedTimings ratedTimings = new RatedTimings(timings, frameType.sampleRate);
		if (nrMeasurements.measureIrregularNoise != 0) {
			filterFactories.add(new NoiseLevelMarkerFilter.Factory(frameType.sampleRate, nrMeasurements));
			filterFactories.add(new IrregularNoiseDetectionFilter.Factory(nrMeasurements, ratedTimings));
		}
		
		filterFactories.add(new NoiseReductionFilter.Factory(ratedTimings, nrDynamicsFactory, nrMeasurements));
		set = new BandSplitFilterSet(buffers, frameType.sampleRate, samples.length / frameType.channels, 25, filterFactories, crossoverInfo);
	}

	public void filter() throws InterruptedException {
		final int channels = frameType.channels;
		final CountDownLatch latch = new CountDownLatch(channels);
		for (int channel = 0; channel < channels; channel++) {
			set.filter(samples, channel, channels, latch);
		}
		
		latch.await();
		
		final List<Exception> exceptions = set.getExceptions();
		
		if (!exceptions.isEmpty()) {
			logger.error("Exceptions occurred:");
			for (Exception e : exceptions) {
				e.printStackTrace();
			}
			throw new IllegalStateException("Exceptions occurred!");
		}
	}
}
