package org.emmef.fileformat.iff;

import org.emmef.fileformat.iff.parse.ContentBuilderFactory;
import org.emmef.fileformat.iff.parse.UnrecognizedContentTypeException;

public interface ContentResolver {

	ContentBuilderFactory getContentParser(String contentType) throws UnrecognizedContentTypeException;

}
