package org.emmef.audio.format;

import java.util.Set;

public interface AudioFormatChannelSetter {
	AudioFormatSampleRateSetter channels(int count); 
	AudioFormatSampleRateSetter locations(SpeakerLocations locations); 
	AudioFormatSampleRateSetter mask(long mask);
	AudioFormatSampleRateSetter setLocations(Set<SpeakerLocation> locations); 
}
