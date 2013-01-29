package org.emmef.fileformat.iff;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.emmef.utils.Preconditions;

public class InterchangeHelper {

	/**
	 * Returns whether the character is a valid identifier character.
	 * <p>
	 * Valid characters are {@code 0} and {@code 0x20} .. ({@code 0x1f})
	 * 
	 * @param character character to verify
	 * @return {@code true} if the character is a valid identifier character, {@code false} otherwise.
	 */
	public static boolean isValidChunkIdentifierCharacter(char character) {
		return character == 0 || character >= ' ' && character <= (char)126;
	}

	/**
	 * Returns the character if it is a valid chunk identifier character and fails otherwise.
	 * 
	 * @param character character to verify
	 * 
	 * @return a valid chunk identifier character
	 * @throws InvalidChunkIdentifierException
	 * @throws IllegalArgumentException if the character is not a valid identifier character.
	 * @see isValidChunkIdentifierCharacter
	 */
	public static char validChunkIdentifierCharacter(char character) throws InvalidChunkIdentifierException {
		if (!isValidChunkIdentifierCharacter(character)) {
			throw new InvalidChunkIdentifierException("Invalid character (0x" + Integer.toHexString(character) + ") in identifier");
		}
		
		return character;
	}

	/**
	 * Returns the identifier if it is a valid identifier, fails otherwise.
	 * 
	 * @param identifier identifier to be verified
	 * 
	 * @return the valid identifier
	 * @throws InvalidChunkIdentifierException
	 * @throws NullPointerException if the identifier is {@code null}
	 * @throws IllegalArgumentException if the identifier is not valid
	 */
	public static String verifiedChunkIdentifier(String identifier) throws InvalidChunkIdentifierException {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if (identifier.length() != 4) {
			throw new IllegalArgumentException("Identifier must have length 4, not " + identifier.length());
		}
		
		for (int i = 0; i < 4; i++) {
			validChunkIdentifierCharacter(identifier.charAt(i));
		}
		
		return identifier;
	}

	/**
	 * Creates an identifier from characters in character buffer.
	 * 
	 * @param characters
	 *            a character array
	 * @param offset
	 *            position from which to read the identifier
	 * 
	 * @return a valid identifier
	 * @throws InvalidChunkIdentifierException
	 * @throws NullPointerException
	 *             if the array is {@code null}
	 * @throws IllegalArgumentException
	 *             if the intended characters are not valid identifier
	 *             characters according to
	 *             {@link isValidChunkIdentifierCharacter}
	 */
	public static String createIdentifier(char[] characters, int offset) throws InvalidChunkIdentifierException {
		Preconditions.checkNotNull(characters, "Identifier characters");
		Preconditions.checkOffsetAndCount(characters.length, offset, 4);
		
		StringBuilder id = new StringBuilder(4);
		for (int i = 0; i < 4; i++) {
			id.append(validChunkIdentifierCharacter(characters[offset + i]));
		}
		
		return id.toString();
	}

	/**
	 * Creates an identifier from characters in byte buffer.
	 * 
	 * @param characters
	 *            a character array
	 * @param offset
	 *            position from which to read the identifier
	 * 
	 * @return a valid identifier
	 * @throws InvalidChunkIdentifierException
	 * @throws NullPointerException
	 *             if the array is {@code null}
	 * @throws IllegalArgumentException
	 *             if the intended characters are not valid identifier
	 *             characters according to
	 *             {@link isValidChunkIdentifierCharacter}
	 */
	public static String createIdentifier(byte[] characters, int offset) throws InvalidChunkIdentifierException {
		Preconditions.checkNotNull(characters, "Identifier characters");
		Preconditions.checkOffsetAndCount(characters.length, offset, 4);
		
		StringBuilder id = new StringBuilder(4);
		for (int i = 0; i < 4; i++) {
			id.append(validChunkIdentifierCharacter((char)(0xff & characters[i])));
		}
		
		return id.toString();
	}

	/**
	 * Creates an identifier from characters in the stream.
	 * 
	 * @param stream
	 *            input stream to read characters from
	 * 
	 * @return a valid identifier
	 * @throws InvalidChunkIdentifierException
	 * @throws NullPointerException
	 *             if the stream is {@code null}
	 * @throws IllegalArgumentException
	 *             if the intended characters are not valid identifier
	 *             characters according to
	 *             {@link isValidChunkIdentifierCharacter}
	 */
	public static String createIdentifier(InputStream stream) throws IOException, InvalidChunkIdentifierException {
		Preconditions.checkNotNull(stream, "stream");
		
		StringBuilder id = new StringBuilder(4);
		for (int i = 0; i < 4; i++) {
			int read = stream.read();
			if (read == -1) {
				throw new EOFException("EOF while reading identifier");
			}
			char character = (char)read;
			id.append(validChunkIdentifierCharacter(character));
		}
		
		return id.toString();
	}

	public static String verifiedContentTypeIdentifier(String contentType) throws InvalidContentTypeIdentfierException {
		try {
			return verifiedChunkIdentifier(contentType);
		}
		catch (InvalidChunkIdentifierException e) {
			throw new InvalidContentTypeIdentfierException(e);
		}
	}

	public static String createContentTypeIdentifier(InputStream stream) throws IOException, InvalidContentTypeIdentfierException {
		try {
			return createIdentifier(stream);
		}
		catch (InvalidChunkIdentifierException e) {
			throw new InvalidContentTypeIdentfierException(e);
		}
	}

}
