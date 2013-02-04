package org.emmef.utils;


public class Checks {

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
	 * @param itemCount
	 *            item count, starting at offset
	 * @param itemSize
	 *            size of each item
	 * @throws IllegalArgumentException
	 *             if combination of {@code offset} and {@code count} does not
	 *             fall within the {@code limit}.
	 * @see #validOffsetAndCount(int, int, int)
	 */
	public static void checkOffsetAndCount(int limit, int offset, int itemCount, int itemSize) {
		if (validOffsetAndCount(limit, offset, itemCount)) {
			return;
		}
		throw new IllegalArgumentException("Offset (" + offset + ") + item count (" + itemCount + ") * item size (" + itemSize + ") larger than limit (" + limit + ")");
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
		if (limit < 0) {
			throw new IllegalArgumentException("limit must be zero or positive");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("offset must be zero or positive");
		}
		if (count < 0) {
			throw new IllegalArgumentException("count must be zero or positive");
		}
		return offset + count <= limit;
	}
	
	/**
	 * Returns if combination of {@code offset} and {@code count} falls within
	 * the {@code limit}.
	 * 
	 * @param limit
	 *            the value limit
	 * @param offset
	 *            offset
	 * @param itemCount
	 *            item count, starting at offset
	 * @param itemSize
	 *            size of each item
	 * @return {@code true} if combination {@code offset} and {@code count}
	 *         falls within the {@code limit}, {@code false} otherwise.
	 */
	public static boolean validOffsetAndCount(int limit, int offset, int itemCount, int itemSize) {
		if (limit < 0) {
			throw new IllegalArgumentException("limit must be zero or positive");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("offset must be zero or positive");
		}
		if (itemCount < 0) {
			throw new IllegalArgumentException("item count must be zero or positive");
		}
		if (itemSize < 1) {
			throw new IllegalArgumentException("item count must positive");
		}
		return (long)itemCount * itemSize + offset <= limit;
	}
}
