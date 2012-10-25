package org.emmef.sndfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.emmef.audio.frame.FrameType;

/**
 * Part of the SoundFile project 
 * @author michelf (original)
 * Created Aug 18, 2004
 * @author $Author: michelf $ (last modified)
 * $Revision: 1.2 $
 */
final class SoundFileType {
	// masks
	public final static int MASK_SUBTYPE        = 0x0000FFFF;
	public final static int MASK_TYPE           = 0x0FFF0000;
	public final static int MASK_ENDIAN         = 0x30000000;
	// format identifiers
	public final static int TYPE_WAV          = 0x00010000;
	public final static int TYPE_AIFF         = 0x00020000;
	public final static int TYPE_AU           = 0x00030000;
	public final static int TYPE_RAW          = 0x00040000;
	public final static int TYPE_PAF          = 0x00050000;
	public final static int TYPE_SVX          = 0x00060000;
	public final static int TYPE_NIST         = 0x00070000;
	public final static int TYPE_VOC          = 0x00080000;
	public final static int TYPE_IRCAM        = 0x000A0000;
	public final static int TYPE_W64          = 0x000B0000;
	public final static int TYPE_MAT4         = 0x000C0000;
	public final static int TYPE_MAT5         = 0x000D0000;
	// sub format identifiers
    public final static int FORMAT_PCM_S8       = 0x00000001;
    public final static int FORMAT_PCM_16       = 0x00000002;
    public final static int FORMAT_PCM_24       = 0x00000003;
    public final static int FORMAT_PCM_32       = 0x00000004;
    public final static int FORMAT_PCM_U8       = 0x00000005;
    public final static int FORMAT_FLOAT        = 0x00000006;
    public final static int FORMAT_DOUBLE       = 0x00000007;
    public final static int FORMAT_ULAW         = 0x00000010;
    public final static int FORMAT_ALAW         = 0x00000011;
    public final static int FORMAT_IMA_ADPCM    = 0x00000012;
    public final static int FORMAT_MS_ADPCM     = 0x00000013;
    public final static int FORMAT_GSM610       = 0x00000020;
    public final static int FORMAT_VOX_ADPCM    = 0x00000021;
    public final static int FORMAT_G721_32      = 0x00000030;
    public final static int FORMAT_G723_24      = 0x00000031;
    public final static int FORMAT_G723_40      = 0x00000032;
    public final static int FORMAT_DWVW_12      = 0x00000040;
    public final static int FORMAT_DWVW_16      = 0x00000041;
    public final static int FORMAT_DWVW_24      = 0x00000042;
    public final static int FORMAT_DWVW_N       = 0x00000043;

	// endian-ness	
	public final static int ENDIAN_FILE         = 0x00000000;
	public final static int ENDIAN_LITTLE       = 0x10000000;
	public final static int ENDIAN_BIG          = 0x20000000;
	public final static int ENDIAN_CPU          = 0x30000000;

    private static final ArrayList<MajorFormat> majorFormats = new ArrayList<MajorFormat>();
    private static final ArrayList<SubFormat> subFormats = new ArrayList<SubFormat>();
    private static final String libraryVersion; 
	
    public static String getLibraryVersion() {
    	return libraryVersion;
    }
    
    public static int getMajorFormatCount() {
    	return getMajorFormatCount0();
    }
    
    public static Collection<MajorFormat> majorFormats() {
    	return Collections.unmodifiableList(majorFormats);
    }
    
    public static int getSubFormatCount() {
    	return getSubFormatCount0();
    }
    
    public static Collection<SubFormat> subFormats() {
    	return Collections.unmodifiableList(subFormats);
    }
    
    public static boolean isValidFormat(int samplerate, int channels, int format) {
    	return isValidFormat0(samplerate, channels, format);
    }
    
    public static MajorFormat getMajorFormat(int format) {
    	final int value = format & MASK_TYPE;
    	for (final MajorFormat mf : majorFormats) {
    		if (mf.getFormat() == value) {
    			return mf;
    		}
    	}
    	
    	throw new IllegalArgumentException("Major format not recognized: 0x" + Integer.toHexString(format) + " / 0x" + Integer.toHexString(value));
    }
    
    public static SubFormat getSubFormat(int format) {
    	final int value = format & MASK_SUBTYPE;
    	for (final SubFormat sf : subFormats) {
    		if (sf.getFormat() == value) {
    			return sf;
    		}
    	}
    	
    	throw new IllegalArgumentException("Sub-format not recognized: 0x" + Integer.toHexString(format) + " / 0x" + Integer.toHexString(value));
    }
    
    static int getNativeFormat(MajorFormat majorFormat, SubFormat subFormat) {
    	return majorFormat.getFormat() | subFormat.getFormat();
    }
    
    public static int getEndianNess(int format) {
    	return format & MASK_ENDIAN;
    }

	private static native boolean isValidFormat0(int samplerate, int channels, int format);
	private static native String getSndFileVersion0();
	private static native int getMajorFormatCount0(); 
	private static native boolean getMajorFormatInfo0(int index, FormatInfo info);
	private static native int getSubFormatCount0();
	private static native boolean getSubFormatInfo0(int index, FormatInfo info);
	private static native boolean initLibSoundFile();
    
	private final int endianNess;
	private final MajorFormat majorFormat;
	private final SubFormat subFormat;
	private final FrameType frameType;

	public SoundFileType(MajorFormat majorFormat, SubFormat subFormat, FrameType frameType, int endianNess) {
		if (majorFormat == null) {
			throw new IllegalArgumentException("Parameter 'majorFormat' cannot be null");
		}
		if (subFormat == null) {
			throw new IllegalArgumentException("Parameter 'subFormat' cannot be null");
		}
		if (frameType == null) {
			throw new IllegalArgumentException("Parameter 'frameType' cannot be null");
		}
		if ((endianNess & MASK_ENDIAN) != endianNess) {
			throw new IllegalArgumentException("Parameter 'endianNess' must fit in mask 0x" + Integer.toHexString(MASK_ENDIAN));
		}
		
		int format = majorFormat.getFormat() | subFormat.getFormat() | endianNess;
		
		if (!isValidFormat(frameType.sampleRate, frameType.channels, format)) {
			throw new IllegalArgumentException("Invalid or inrecognized sound file type: \"" + majorFormat + " - " + subFormat + "\" with " + frameType.channels + " channels at " + frameType.sampleRate + " Hz");
		}
		this.majorFormat = majorFormat;
		this.subFormat = subFormat;
		this.frameType = frameType;
		this.endianNess = endianNess;
	}
	
	public SoundFileType(int format, int channels, int samplerate) {
		this.frameType = new FrameType(channels, samplerate);
		this.majorFormat = getMajorFormat(format);
		this.subFormat = getSubFormat(format);
		this.endianNess = getEndianNess(format);
	}
	
	public SoundFileType(InfoStructure info) {
		this(info.format, info.channels, info.samplerate);
	}
	
	public InfoStructure createInfoStructure() {
		return new InfoStructure(getNativeFormat(majorFormat, subFormat), frameType.channels, frameType.sampleRate);
	}
	
	public static InfoStructure createEmptyInfoStructure() {
		return new InfoStructure();
	}
	
	public final int getFormat() {
		return majorFormat.getFormat() | subFormat.getFormat() | endianNess;
	}

	public final FrameType getFrameType() {
		return frameType;
	}

	public final MajorFormat getMajorFormat() {
		return majorFormat;
	}

	public final SubFormat getSubFormat() {
		return subFormat;
	}
	
	public final int getChannels() {
		return frameType.channels;
	}
	
	public final int getSampleRate() {
		return frameType.sampleRate;
	}
	
	public final String toString() {
		return getClass().getSimpleName() + "[" + majorFormat.getName() + "; " + subFormat.getName() + "; " + frameType + "]"; 
	}
	
	public static class InfoStructure {
		public long frames;
		public int samplerate;
		public int channels;
		public int format;
		public boolean seekable;

		public InfoStructure() {
			// Empty default value constructor
		}
		
		public InfoStructure(int format, int channels, int samplerate) {
			this.format = format;
			this.channels = channels;
			this.samplerate = samplerate;
		}
	}
	
    static {
       	if (!initLibSoundFile()) {
       		throw new IllegalStateException("Could not initialize sound library");
       	}
       	
       	libraryVersion = getSndFileVersion0();
       	int majorFormatCount = getMajorFormatCount0();
       	for (int i = 0; i < majorFormatCount; i++) {
        	FormatInfo info = new FormatInfo();
        	if (getMajorFormatInfo0(i, info)) {
           		majorFormats.add(new MajorFormat(info));
        	}
       	}
       	int subFormatCount = getSubFormatCount0(); 
       	for (int i = 0; i < subFormatCount; i++) {
        	FormatInfo info = new FormatInfo();
        	if (getSubFormatInfo0(i, info)) {
           		subFormats.add(new SubFormat(info));
        	}
       	}
	}
}
