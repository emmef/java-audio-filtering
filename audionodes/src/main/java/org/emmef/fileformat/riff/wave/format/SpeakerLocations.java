package org.emmef.fileformat.riff.wave.format;

import static org.emmef.fileformat.riff.wave.format.AudioFormats.checkChannels;
import static org.emmef.fileformat.riff.wave.format.SpeakerLocation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class SpeakerLocations implements Set<SpeakerLocation> {
	private static final List<SpeakerLocations> CACHED_LOCATIONS = new ArrayList<>();
	private static final SpeakerLocation[] LOCATION_VALUES = SpeakerLocation.values();
	
	public static final SpeakerLocations MONO = addCached(new SpeakerLocations("Mono", FC));
	public static final SpeakerLocations STEREO = addCached(new SpeakerLocations("Stereo", FL, FR));
	public static final SpeakerLocations QUADROPHONIC = addCached(new SpeakerLocations("Quadrophonic", FL, FR, BL, BR));
	public static final SpeakerLocations SURROUND = addCached(new SpeakerLocations("Surround", FL, FR, FC, BC));
	public static final SpeakerLocations SURROUND_5_1 = addCached(new SpeakerLocations("5.1", FL, FR, FC, LF, BL, BR));
	public static final SpeakerLocations SURROUND_7_1 = addCached(new SpeakerLocations("7.1", FL, FR, FC, LF, BL, BR, FLC, FRC));
	
	private static final long[] CACHED_MASKS = createCachedMasks(CACHED_LOCATIONS);
	
	public static SpeakerLocations of(SpeakerLocation... locations) {
		if (locations == null) {
			throw new NullPointerException("Locations cannot be null");
		}
		if (locations.length == 0) {
			throw new IllegalArgumentException("Need at least one location");
		}
		long mask = 0;
		for (int i = 0; i < locations.length; i++) {
			SpeakerLocation location = locations[i];
			if (location == null) {
				throw new NullPointerException("Location cannot be null");
			}
			mask |= location.getBitMask();
		}
		
		return createOrGetCached(mask);
	}
	
	public static SpeakerLocations ofMask(long mask) {
		SpeakerLocation.checkMask(mask);
		return createOrGetCached(mask);
	}
	
	public static SpeakerLocations ofChannels(int numberOfChannels) {
		checkChannels(numberOfChannels);
		switch (numberOfChannels) {
		case 1:
			return MONO;
		case 2:
			return STEREO;
		case 4:
			return QUADROPHONIC;
		case 6:
			return SURROUND_5_1;
		case 8:
			return SURROUND_7_1;
		default:
			throw new IllegalArgumentException("No constant for " + numberOfChannels + " channels");
		}
	}

	private static SpeakerLocations createOrGetCached(long mask) {
		int index = Arrays.binarySearch(CACHED_MASKS, mask);
		if (index >= 0) {
			return CACHED_LOCATIONS.get(index);
		}
		return new SpeakerLocations(mask);
	}
	
	private final long mask;
	private final int channels;
	private final String name;

	private SpeakerLocations(long mask) {
		this.name = null;
		this.mask = mask;
		this.channels = Long.bitCount(mask);
	}

	private SpeakerLocations(String name, SpeakerLocation... locations) {
		long value = calculateMaskUnchecked(locations);
		this.name = name;
		this.mask = value;
		this.channels = Long.bitCount(mask);
	}
	
	@Override
	public int size() {
		return channels;
	}
	
	public long getMask() {
		return mask;
	}

	@Override
	public boolean isEmpty() {
		return channels == 0;
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof SpeakerLocation) {
			return (mask & ((SpeakerLocation) o).getBitMask()) != 0;
		}
		return false;
	}

	@Override
	public Iterator<SpeakerLocation> iterator() {
		return new LocationIterator(mask);
	}

	@Override
	public Object[] toArray() {
		return fillArray(new Object[channels]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		T[] result;
		if (a == null || a.length < channels) {
			result = (T[]) Array.newInstance(SpeakerLocation.class, channels);
		} else {
			result = a;
		}

		fillArray(result);

		return result;
	}

	@SuppressWarnings("unchecked")
	private <T> T[] fillArray(T[] result) {
		int i = 0;
		for (SpeakerLocation location : LOCATION_VALUES) {
			if (location.testMask(mask)) {
				result[i++] = (T) location;
			}
		}
		for (; i < result.length; i++) {
			result[i] = null;
		}

		return result;
	}

	@Override
	public boolean add(SpeakerLocation e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		if (collection == null) {
			throw new NullPointerException("collection");
		}
		if (collection instanceof SpeakerLocations) {
			long yourMask = ((SpeakerLocations)collection).mask;
			return yourMask == (yourMask & mask); 
		}
		if (collection instanceof List) {
			List<?> list = (List<?>)collection;
			int size = collection.size();
			for (int i = 0; i < size; i++) {
				if (!contains(list.get(i))) {
					return false;
				}
			}
		}
		for (Object o : collection) {
			if (!contains(o)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean addAll(Collection<? extends SpeakerLocation> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String toString() {
		if (name != null) {
			return name;
		}
		StringBuilder builder = new StringBuilder(2 + channels * 5);
		builder.append('[');
		boolean first = true;
		for (SpeakerLocation location : LOCATION_VALUES) {
			if (first) {
				first = false;
			}
			else {
				builder.append(", ");
			}
			if (location.testMask(mask)) {
				builder.append(location.name());
			}
		}
		builder.append(']');
		return builder.toString();
	}

	private static long calculateMaskUnchecked(SpeakerLocation... locations) {
		long value = 0;
		for (SpeakerLocation location : locations) {
			value |= location.getBitMask();
		}
		return value;
	}
	
	private static SpeakerLocations addCached(SpeakerLocations locations) {
		int index = Collections.binarySearch(CACHED_LOCATIONS, locations, SpeakerLocationComparator.INSTANCE);
		if (index >= 0) {
			return CACHED_LOCATIONS.get(index);
		}

		int insertionPoint = -1 - index;
		CACHED_LOCATIONS.add(insertionPoint, locations);
		
		return locations;
	}
	
	private static long[] createCachedMasks(List<SpeakerLocations> cachedLocations) {
		long[] result = new long[cachedLocations.size()];
		
		for (int i = 0; i < cachedLocations.size(); i++) {
			result[i] = cachedLocations.get(i).getMask();
		}
		
		return result;
	}
	
	private static final class SpeakerLocationComparator implements Comparator<SpeakerLocations> {
		static final Comparator<SpeakerLocations> INSTANCE = new SpeakerLocationComparator();

		@Override
		public int compare(SpeakerLocations o1, SpeakerLocations o2) {
			if (o1.mask > o2.mask) {
				return 1;
			}
			if (o1.mask < o2.mask) {
				return -1;
			}
			return 0;
		}
	}

	private static final class LocationIterator implements Iterator<SpeakerLocation> {
		private int position = 0;
		private SpeakerLocation nextValue = null;
		private final long mask;
		
		LocationIterator(long mask) {
			this.mask = mask;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public SpeakerLocation next() {
			if (hasNext()) {
				SpeakerLocation result = nextValue;
				nextValue = null;
				return result;
			}
			throw new NoSuchElementException();
		}

		@Override
		public boolean hasNext() {
			if (nextValue != null) {
				return true;
			}
			while (position < LOCATION_VALUES.length) {
				SpeakerLocation testValue = LOCATION_VALUES[position++];
				if (testValue.testMask(mask)) {
					nextValue = testValue;
					return true;
				}
			}
			return false;
		}
	}
}
