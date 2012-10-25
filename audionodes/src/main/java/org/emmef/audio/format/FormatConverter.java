package org.emmef.audio.format;

public interface FormatConverter<P, I> {
	P publish(I internalFormat);
	I intern(P publishedFormat);
}
