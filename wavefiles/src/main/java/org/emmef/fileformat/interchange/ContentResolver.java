package org.emmef.fileformat.interchange;

public interface ContentResolver {

	ContentBuilderFactory getContentParser(String contentType) throws ContentTypeNotRecognisedException;

}
