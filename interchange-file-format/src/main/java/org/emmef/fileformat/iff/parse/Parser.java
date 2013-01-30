package org.emmef.fileformat.iff.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.emmef.fileformat.iff.ContentChunk;
import org.emmef.fileformat.iff.InterchangeChunk;
import org.emmef.fileformat.iff.InterchangeChunk.ContentBuilder;
import org.emmef.fileformat.iff.InterchangeChunk.TypeBuilder;
import org.emmef.fileformat.iff.InterchangeFormatException;
import org.emmef.fileformat.iff.InterchangeHelper;
import org.emmef.fileformat.iff.TypeChunk;

public class Parser {
	
	public static List<InterchangeChunk> readChunks(TypeResolver factory, InputStream stream) throws IOException, InterchangeFormatException {
		List<InterchangeChunk> result = new ArrayList<>();
		String id = InterchangeHelper.createIdentifier(stream);
		
		TypeBuilderFactory contentTypeFactory = factory.get(id);
		
		if (contentTypeFactory == null) {
			throw new UnrecognizedTypeChunkException(id);
		}
		
		TypeBuilder typeBuilder = contentTypeFactory.createBuilder().readContentLengthAndType(stream);
		ContentBuilderFactory contentBuilderFactory = contentTypeFactory.getContentParser(typeBuilder.getContentType());
		
		TypeChunk type = typeBuilder.build();
		ContentChunk content = null;
		
		result.add(type);
		
		while (true) {
			id = InterchangeHelper.createIdentifier(stream);
			if (id == null) {
				break;
			}
			ContentBuilder contentFactory = contentBuilderFactory.create(id);
			if (contentFactory != null) {
				content = linkContentChunk(stream, type, content, contentFactory);
				result.add(content);
				if (!content.getDefinition().preReadContent()) {
					return result; // cannot read past here...
				}
			}
			else {
				contentTypeFactory = factory.get(id);
				if (contentTypeFactory != null) {
					return result;
				}
				throw new UnrecognizedChunkException(id);
			}
		}
		
		return result;
	}

	private static ContentChunk linkContentChunk(InputStream stream, TypeChunk type, ContentChunk content, ContentBuilder contentFactory) throws IOException {
		if (content == null) {
			contentFactory.parent(type);
		}
		else {
			contentFactory.sibling(content);
		}
		contentFactory.readContentLength(stream);
		contentFactory.readContent(stream);
		ContentChunk createdChunk = contentFactory.build();
		return createdChunk;
	}
}
