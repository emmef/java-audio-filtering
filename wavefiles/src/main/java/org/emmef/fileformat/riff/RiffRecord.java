package org.emmef.fileformat.riff;

/**
 * Describes a record in a Redmond Interchange File Format file.
 */
public interface RiffRecord extends Comparable<RiffRecord> {
	/**
	 * Returns the identifier for this record.
	 * <p>
	 * The identifier is always four characters long, where each character 
	 * must meet {@link RiffUtils#isValidChunkIdentifierCharacter(char)}.
	 * 
	 * @return a non-{@code null}, four-character string
	 * 
	 * @see RiffUtils#isValidChunkIdentifierCharacter(char)
	 */
	String getIdentifier();

	
	/**
	 * Returns the length of the record header.
	 * <p>
	 * This length is always 8, except for a RIFF file identifier, then its 12.  
	 * 
	 * @return a zero or positive number.
	 */
	int getHeaderLength();

	/**
	 * Returns the number of bytes to skip for the next chunk, assuming the header was already read.
	 * @return a zero or positive number.
	 */
	long getSkipToNext();

	/**
	 * Returns the length of the data inside the record.
	 * <p>
	 * The data inside can also contain other records.
	 * 
	 * @return a zero or positive number.
	 */
	long getContentLength();
	
	/**
	 * Returns the relative offset within the parent record.
	 * <p>
	 * Because a record always contains at least an identifier and 
	 * a 4-byte length, this value is eight or greater.
	 * @return a number &gt;= {@literal 8}
	 */
	long getRelativeOffset();
	
	/**
	 * Returns the absolute offset within the stream that contains the record.
	 * <p>
	 * A record that has a zero absolute offset is the so-called root-record.
	 * @return a zero or positive number.
	 */
	long getAbsoluteOffset();
	
	/**
	 * Returns parent record or {@code null} if there is no parent record. 
	 * @return parent record or {@code null} if there is no parent record.
	 */
	RiffRecord getParent();
}
