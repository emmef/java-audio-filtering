package org.emmef.audio.nativesoundfile;

import java.io.IOException;

/**
 * Part of the SoundFile project
 * @author michelf (original)
 * Created Aug 27, 2004
 * @author $Author: michelf $ (last modified)
 * $Revision: 1.2 $
 */
public class SoundFileException extends IOException {
	private static final long serialVersionUID = 1L;

	SoundFileException(String message) {
		super(message);
	}
}
