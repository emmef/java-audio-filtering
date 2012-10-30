package org.emmef.audio.format;

import static org.emmef.audio.format.AudioFormats.checkChannels;
import static org.emmef.audio.format.SpeakerLocation.*;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class SpeakerLocations implements Set<SpeakerLocation>, Comparable<SpeakerLocations> {
	private static final SpeakerLocation[] LOCATION_VALUES = SpeakerLocation.values();
	private static final SortedMap<SpeakerLocations, String> NAMES = new TreeMap<>(); 
	
	public static final SpeakerLocations MONO = linkToName(new SpeakerLocations(FC), "Mono");
	public static final SpeakerLocations STEREO = linkToName(new SpeakerLocations(FL, FR), "Stereo");
	public static final SpeakerLocations QUADROPHONIC = linkToName(new SpeakerLocations(FL, FR, BL, BR), "Quadrophonic");
	public static final SpeakerLocations SURROUND = linkToName(new SpeakerLocations(FL, FR, FC, BC), "Surround");
	public static final SpeakerLocations SURROUND_5_1 = linkToName(new SpeakerLocations(FL, FR, FC, LF, BL, BR), "5.1");
	public static final SpeakerLocations SURROUND_7_1 = linkToName(new SpeakerLocations(FL, FR, FC, LF, BL, BR, FLC, FRC), "7.1");
	
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
		
		return new SpeakerLocations(mask);
	}
	
	public static SpeakerLocations ofMask(long mask) {
		SpeakerLocation.checkMask(mask);
		return new SpeakerLocations(mask);
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
	
	public static String getNameFor(int numberOfChannels) {
		checkChannels(numberOfChannels);
		switch (numberOfChannels) {
		case 1:
			return NAMES.get(MONO);
		case 2:
			return NAMES.get(STEREO);
		case 4:
			return NAMES.get(QUADROPHONIC);
		case 6:
			return NAMES.get(SURROUND_5_1);
		case 8:
			return NAMES.get(SURROUND_7_1);
		default:
			throw new IllegalArgumentException("No constant for " + numberOfChannels + " channels");
		}
	}
	
	private final long mask;

	private SpeakerLocations(long mask) {
		this.mask = mask;
	}

	private SpeakerLocations(SpeakerLocation... locations) {
		long value = calculateMaskUnchecked(locations);
		this.mask = value;
	}
	
	@Override
	public int size() {
		return getChannels();
	}
	
	public long getMask() {
		return mask;
	}

	@Override
	public boolean isEmpty() {
		return mask == 0;
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
		return fillArray(new Object[getChannels()]);
	}

	public final int getChannels() {
		return Long.bitCount(mask);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		T[] result;
		int channels = getChannels();
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
	public int compareTo(SpeakerLocations o) {
		return Long.compare(mask, o.mask);
	}
	
	@Override
	public int hashCode() {
		return 31 + (int) (mask ^ (mask >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		return mask == ((SpeakerLocations) obj).mask;
	}
	
//	
//	@Override
//	public int hashCode() {
//		
//		int hash = getClass().hashCode();
//		hash *= 31;
//		hash += (int)(mask ^ (mask >>> 32)); 
//				
//	}
	
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
		String name = NAMES.get(this);
		if (name != null) {
			return name;
		}
		StringBuilder builder = new StringBuilder(2 + getChannels() * 5);
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
	
	private static SpeakerLocations linkToName(SpeakerLocations locations, String name) {
		String found = NAMES.get(locations);
		if (found == null) {
			NAMES.put(locations, name);
		}
		
		return locations;
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
