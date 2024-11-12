package org.emmef.sndfile;

import org.emmef.audio.format.FrameMetrics;

/**
 * Part of the SoundFile project 
 * @author michelf (original)
 * Created Aug 18, 2004
 * @author $Author: michelf $ (Last modified)
 * $Revision: 1.1 $
 */
public class SoundFileFormat {
	
	private final FrameMetrics frameType;
	private final SoundFileType audioType;
	
	public SoundFileFormat(FrameMetrics frameType, SoundFileType audioType) {
		this.frameType = frameType; 
		this.audioType = audioType;
	}
	
	public FrameMetrics getFrameFormat() {
		return frameType;
	}
	
	public SoundFileType getAudioType() {
		return audioType;
	}
	
	public String toString() {
		return frameType.toString() + " " + audioType;
	}
}
