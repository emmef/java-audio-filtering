package org.emmef.audio.noisereduction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

import org.emmef.audio.filter.Filter;
import org.emmef.audio.filter.iir.butterworth.Butterworth;
import org.emmef.audio.filter.iir.butterworth.PassType;
import org.emmef.audio.filter.tools.Integrator;
import org.emmef.audio.noisereduction.BufferSet.Handle;
import org.emmef.logging.FormatLogger;
import org.emmef.logging.FormatLoggerFactory;

public class BandSplitFilterSet {
	public enum Direction {
		UPWARD, DOWNWARD;
		
		public static final Direction DEFAULT = DOWNWARD;
		
		public static Direction effectiveValueOf(String value) {
			if (value == null) {
				throw new NullPointerException("value");
			}
			for (Direction direction : values()) {
				if (direction.name().equalsIgnoreCase(value)) {
					return direction;
				}
			}
			return DEFAULT;
		}
	}
	
	private static final FormatLogger logger = FormatLoggerFactory.getLogger(BandSplitFilterSet.class);
	private static final int LOW_BANDWIDTH_LIMIT = 20;
	
	public static final int LAYER_DIFFERENCE = 0;
	public static final int LAYER_FILTER = 1;
	public static final int LAYER_ACCUMULATOR = 2;
	
	private final ChainableFilter[][] filters;
	private final int offset;
	private final long samplerate;
	private final List<FilterFactory> factories;
	private final int inPosition;
	private final int outPosition;
	private final int frameCount;
	private final int totalSampleCount;
	private final BufferSet buffers;
	private final List<Exception> exceptions = new CopyOnWriteArrayList<Exception>();
	private final CrossoverInfo crossoverInfo;

	public BandSplitFilterSet(BufferSet buffers, long samplerate, int frameCount, int bits, List<FilterFactory> factories, CrossoverInfo crossoverInfo) {
		if (buffers == null) {
			throw new NullPointerException("buffers");
		}
		if (crossoverInfo == null) {
			throw new NullPointerException("crossoverInfo");
		}
		if (factories == null) {
			throw new NullPointerException("factories");
		}
		this.crossoverInfo = crossoverInfo;
		this.buffers = buffers;
		this.samplerate = samplerate;
		this.frameCount = frameCount;
		this.factories = factories;
		
		filters = new ChainableFilter[factories.size()][];
		int filterLatency = 0;
		for (int filter = 0; filter < factories.size(); filter++) {
			filters[filter] = new ChainableFilter[crossoverInfo.crossovers.size() + 1];
			filterLatency += factories.get(filter).getLatency();
		}
		
		final double lowestCrossover = crossoverInfo.crossovers.get(0);
		final double characteristicSamples = Integrator.samples(samplerate, 0.5 / (lowestCrossover * Math.PI));
		final int bitLevelPeriods = (int)(0.5 + Math.log(2.0) * crossoverInfo.filterOrder * bits * crossoverInfo.crossovers.size() * characteristicSamples);
		final int correctedFrameCount = frameCount + 2 * bitLevelPeriods;
		offset = bitLevelPeriods;
		totalSampleCount = correctedFrameCount + filterLatency;
		inPosition = offset;
		outPosition = offset + filterLatency;
	}
	
	public void filter(final float[] data, final int offset, final int step, final CountDownLatch latch) throws InterruptedException {
		if (data == null) {
			throw new NullPointerException("data");
		}
		if (latch == null) {
			throw new NullPointerException("latch");
		}
		final Thread filterThread = new CrossoverThread(offset, data, step, latch);
		filterThread.start();
	}
	
	public List<Exception> getExceptions() {
		return new ArrayList<Exception>(exceptions);
	}
	
	private final class CrossoverThread extends Thread {
		private final int offs;
		private final float[] data;
		private final int step;
		private final CountDownLatch latch;
		private final LinkedBlockingDeque<FilterData> queue = new LinkedBlockingDeque<FilterData>();
		
		private CrossoverThread(int offset, float[] data, int step, CountDownLatch latch) {
			super("Channel " + offset + " crossovers");
			setDaemon(true);
			offs = offset;
			this.data = data;
			this.step = step;
			this.latch = latch;
		}

		@Override
		public void run() {
			try {
				filter();
			}
			catch (Exception e) {
				exceptions.add(new Exception("Exception in " + this + ": " + e, e));
			}
			finally {
				latch.countDown();
			}
		}

		private void filter() throws InterruptedException {
			final Handle handle = buffers.init(totalSampleCount, 3, 5);
			try {
				Buffer inputSamples = handle.get();
				readData(inputSamples);
				Buffer accumulator = handle.get();
				final CountDownLatch countDownLatch = new CountDownLatch(crossoverInfo.crossovers.size() + 1);
				new FilterThread("Channel " + offs + " filters/1", accumulator.getSamples(), countDownLatch, handle, queue).start();
				new FilterThread("Channel " + offs + " filters/2", accumulator.getSamples(), countDownLatch, handle, queue).start();
				filter(handle, inputSamples);
				
				countDownLatch.await();
				writeDataBack(accumulator);
			}
			catch (InterruptedException e) {
				handle.panic();
				throw e;
			}
			catch (RuntimeException e) {
				handle.panic();
				throw e;
			}
			finally {
				handle.close();
			}
		}

		private void readData(Buffer input) {
			final double[] inputSamples = input.getSamples();
			int i = 0;
			for (; i < inPosition; i++) {
				inputSamples[i] = 0.0;
			}
			final int dataInEnd = inPosition + data.length / step;
			synchronized (data) {
				for (int dataPosition = offs; i < dataInEnd; i++, dataPosition += step) {
					inputSamples[i] = data[dataPosition];
				}
			}
			for (; i < inputSamples.length; i++) {
				inputSamples[i] = 0.0;
			}
		}

		private void writeDataBack(Buffer accumulator) {
			final int dataOutEnd = Math.min(outPosition + data.length / step, accumulator.length());
			synchronized(data) {
				final double[] samples = accumulator.getSamples();
				for (int k = outPosition, dataPosition = offs; k < dataOutEnd; k++, dataPosition += step) {
					try {
						data[dataPosition] = (float)samples[k];
					}
					catch (ArrayIndexOutOfBoundsException e) {
						throw e;
					}
				}
			}
		}
		
		private void filter(Handle handle, Buffer input) throws InterruptedException {
			final int crossovers = crossoverInfo.crossovers.size();
			final Buffer source = input;
			if (crossoverInfo.direction == Direction.UPWARD) {
				double lowerFrequency = LOW_BANDWIDTH_LIMIT;
				
				for (int band = 0; band < crossovers; band++) {
					final double higherFrequency = crossoverInfo.crossovers.get(band);
					final long start = System.currentTimeMillis();
					Buffer destination = handle.get();
					final long waited = System.currentTimeMillis() - start;
					if (waited > 100) {
						logger.info("Waited " + waited + " msec for next buffer to split.");
					}
					filterBands(source.getSamples(), destination.getSamples(), higherFrequency, PassType.LOW_PASS);
					/*
					 * LAYER_FILTER now contains the filtered frequency band, we must do
					 * the filtering there!
					 */
					submitFilters(destination, lowerFrequency, higherFrequency);
					lowerFrequency = higherFrequency;
				}
				/*
				 * There is one band left: the last one, which contains the highest
				 * frequency band. We must filter that too!
				 */
				submitFilters(source, lowerFrequency, 0.5 * samplerate);
			}
			else {
				double higherFrequency = 0.5 * samplerate;
				for (int band = 0; band < crossovers; band++) {
					final double lowerFrequency = crossoverInfo.crossovers.get(crossovers - 1 - band);
					Buffer destination = handle.get();
					
					filterBands(source.getSamples(), destination.getSamples(), lowerFrequency, PassType.HIGH_PASS);
					/*
					 * LAYER_FILTER now contains the filtered frequency band, we must do
					 * the filtering there!
					 */
					submitFilters(destination, lowerFrequency, higherFrequency);
					higherFrequency = lowerFrequency;
				}
				/*
				 * There is one band left: the last one, which contains the one lowest
				 * frequency band. We must filter that too!
				 */
				submitFilters(source, LOW_BANDWIDTH_LIMIT, higherFrequency);
			}
		}
		
		/**
		 * Applies a low-pass Linkwitz-Riley filter.
		 * 
		 * The filter uses the data from the source layer and stores
		 * the results in the destination layer. All values in the
		 * result layer are then subtracted from the source layer.
		 */
		private void filterBands(double[] sourceLayer, double[] destinationLayer, double frequency, PassType passBand) {
			logger.debug("Apply crossover @ %d Hz, %s", (long)(0.5 + frequency), passBand);
			Filter filter = Butterworth.create(frequency/samplerate, crossoverInfo.filterOrder, passBand);
			
			synchronized (destinationLayer) {
				for (int i = 0; i < sourceLayer.length; i++) {
					destinationLayer[i] = filter.filter(sourceLayer[i]);
				}
				filter.reset();
				for (int i = sourceLayer.length - 1; i >= 0; i--) {
					final double filteredSample = filter.filter(destinationLayer[i]);
					destinationLayer[i] = filteredSample;
					sourceLayer[i] -= filteredSample;
				}
			}
		}
		
		public void submitFilters(Buffer data, double lowerFrequency, double higherFrequency) {
			final FilterData filterData = new FilterData(data, lowerFrequency, higherFrequency);
			queue.add(filterData);
		}
	}
	
	private class FilterThread extends Thread {
		private final double[] accumulator;
		private final CountDownLatch latch;
		private final LinkedBlockingDeque<FilterData> queue;
		private final Handle handle;

		public FilterThread(String name, double[] accumulator, CountDownLatch latch, Handle handle, LinkedBlockingDeque<FilterData> queue) {
			super(name);
			setDaemon(true);
			this.handle = handle;
			this.accumulator = accumulator;
			this.latch = latch;
			this.queue = queue;
		}
		
		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					FilterData data = queue.take();
					executeFilterData(data);
				}
			}
			catch (Exception e) {
				exceptions.add(new Exception("Exception in " + this + ": " + e, e));
				logger.debug("Something bad happened: %s", e);
				handle.panic();
			}
			finally {
				while (latch.getCount() > 0) {
					latch.countDown();
				}
			}
		}

		private void executeFilterData(final FilterData data) {
			try {
				applyFilters(data.getData(), data.lowerFrequency, data.higherFrequency);
			}
			finally {
				latch.countDown();
				handle.put(data.data);
			}
		}
		
		/**
		 * Applies the filters enlisted in factories to the specified data. Afterwards,
		 * it adds the data to the LAYER_ACCUMULATE.
		 */
		private void applyFilters(Buffer data, double lowerFrequency, double higherFrequency) {
			logger.info("Apply filters for %1.0f to %1.0f Hz", lowerFrequency, higherFrequency);
			Object metaData = null;
			final double[] samples = data.getSamples();
			for (int filter = 0; filter < filters.length; filter++) {
				final FilterFactory filterFactory = factories.get(filter);
				final int endOffset = offset + frameCount + filterFactory.getLatency() - filterFactory.getEndOffset();
				final int startOffset = offset + filterFactory.getStartOffset();
				ChainableFilter bandFilter = filterFactory.createFilter(metaData, lowerFrequency, higherFrequency, data.getMarkers());
				for (int i = startOffset; i < endOffset; i++) {
					samples[i] = bandFilter.filter(samples[i]);
				}
				metaData = bandFilter.getMetaData();
			}
			synchronized (accumulator) {
				for (int i = 0; i < samples.length; i++) {
					accumulator[i] += samples[i];
				}
			}
		}
	}
	
	private static class FilterData {
		final Buffer data;
		final double lowerFrequency;
		final double higherFrequency;

		public FilterData(Buffer data, double lowerFrequency, double higherFrequency) {
			this.data = data;
			this.lowerFrequency = lowerFrequency;
			this.higherFrequency = higherFrequency;
		}
		
		public Buffer getData() {
			synchronized (data) {
				return data;
			}
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "(" + lowerFrequency + "-" + higherFrequency + ";"+data + ")";
		}
	}

	public static class FrequencyBand {
		double[] samples;
		double noiseLevel;
		double low, high;
		
		public FrequencyBand() {
			// TODO Auto-generated constructor stub
		}
	}
}
