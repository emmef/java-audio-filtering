package org.emmef.fileformat.riff.wave.format;

import java.util.EnumSet;
import java.util.Set;

public enum SpeakerLocation {
	FL("Front Left", 0x1),
	FR("Front Right", 0x2),
	FC("Front Center", 0x4),
	LF("Low Frequency", 0x8),
	BL("Back Left", 0x10),
	BR("Back Right", 0x20),
	FLC("Front Left of Center", 0x40),
	FRC("Front Right of Center", 0x80),
	BC("Back Center", 0x100),
	SL("Side Left", 0x200),
	SR("Side Right", 0x400),
	TC("Top Center", 0x800),
	TFL("Top Front Left", 0x1000),
	TFC("Top Front Center", 0x2000),
	TFR("Top Front Right", 0x4000),
	TBL("Top Back Left", 0x8000),
	TBC("Top Back Center", 0x10000),
	TBR("Top Back Right", 0x20000);
	
	private static final long LOCATION_MASK = createLocationMask(); 
	private static final long INVALID_LOCATION_MASK = -1 ^ LOCATION_MASK;
	
	private final String description;
	private final long bitMask;
	

	private SpeakerLocation(String description, long bitMask) {
		this.description = description;
		this.bitMask = bitMask;
	}
	
	private static long createLocationMask() {
		long mask = 0;
		for (SpeakerLocation location : values()) {
			mask |= location.getBitMask();
		}
		return mask;
	}

	public String getDescription() {
		return description;
	}
	
	public long getBitMask() {
		return bitMask;
	}
	
	public boolean testMask(long mask) {
		return (mask & bitMask) != 0;
	}
	
	public static Set<SpeakerLocation> toSet(long locationMask, boolean strict) {
		EnumSet<SpeakerLocation> set = EnumSet.noneOf(SpeakerLocation.class);
		
		if (strict) {
			checkMask(locationMask);
		}
		for (SpeakerLocation location : values()) {
			if (location.testMask(locationMask)) {
				set.add(location);
			}
		}
		
		return set;
	}
	
	public static long toMask(Set<SpeakerLocation> set) {
		if (set == null) {
			throw new NullPointerException("set");
		}
		long mask = 0;
		for (SpeakerLocation location : values()) {
			if (set.contains(location)) {
				mask |= location.getBitMask();
			}
		}
		
		return mask;
	}
	
	public static int getNumberOfChannels(long mask) {
		return Long.bitCount(mask);
	}

	public static boolean isValidMask(long locationMask) {
		return (locationMask & LOCATION_MASK) == locationMask;
	}
	
	public static long getInvalidBits(long locationMask) {
		return locationMask & INVALID_LOCATION_MASK;
	}
	
	public static void checkMask(long locationMask) {
		long invalidBits = getInvalidBits(locationMask);
		if (invalidBits != 0) {
			throw new IllegalArgumentException("Invalid bits in location mask: 0x" + Long.toHexString(invalidBits));
		}
	}
}
