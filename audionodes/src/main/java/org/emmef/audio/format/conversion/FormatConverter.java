package org.emmef.audio.format.conversion;

public interface FormatConverter<P, I> {
	P publish(I internalFormat);
	I intern(P publishedFormat);
}
