package org.emmef.utils;


public class Preconditions {

	/**
	 * Checks if combination of {@code offset} and {@code count} falls within
	 * the {@code limit} and throws if it is not.
	 * <p>
	 * The actual check is done by invoking
	 * {@link #validOffsetAndCount(int, int, int)}.
	 * 
	 * @param limit
	 *            the value limit
	 * @param offset
	 *            offset
	 * @param count
	 *            count starting at offset
	 * @throws IllegalArgumentException
	 *             if combination of {@code offset} and {@code count} does not
	 *             fall within the {@code limit}.
	 * @see #validOffsetAndCount(int, int, int)
	 */
	public static void checkOffsetAndCount(int limit, int offset, int count) {
		if (validOffsetAndCount(limit, offset, count)) {
			return;
		}
		throw new IllegalArgumentException("Offset (" + offset + ") + count(" + count + ") larger than limit (" + limit + ")");
	}

	/**
	 * Returns if combination of {@code offset} and {@code count} falls within the
	 * {@code limit}.
	 * 
	 * @param limit
	 *            the value limit
	 * @param offset
	 *            offset
	 * @param count
	 *            count starting at offset
	 * @return {@code true} if combination {@code offset} and {@code count}
	 *         falls within the {@code limit}, {@code false} otherwise.
	 */
	public static boolean validOffsetAndCount(int limit, int offset, int count) {
		return offset + count <= limit;
	}
	
	/**
	 * Returns {@code argument} if it is not {@code null} and throws otherwise.
	 * 
	 * @param argument
	 *            argument to be returned if it is not {@code null}
	 * @param argumentName
	 *            name of the argument that is used for exception message if
	 *            {@code argument} is {@code null}.
	 * @return
	 * @throw {@link IllegalArgumentException} with the message
	 *        {@code argumentName + " cannot be null"} if {@code argument} is
	 *        {@code null}.
	 */
	public static <T> T checkNotNull(T argument, String argumentName) {
		if (argument != null) {
			return argument;
		}
		
		throw new IllegalArgumentException(argumentName + " cannot be null");
	}
}
