package org.emmef.audio.format.conversion;


public class FormatConvertors {
	public static <T> FormatConverter<T, T> identity() {
		return Identity.getInstance();
	}
	
	private static final class Identity<T> implements FormatConverter<T, T> {
		static final Identity<Object> INSTANCE = new Identity<>();
		
		@SuppressWarnings("unchecked")
		static <T> FormatConverter<T, T> getInstance() {
			return (FormatConverter<T, T>)Identity.INSTANCE;
		}
		@Override
		public T publish(T internalFormat) {
			return internalFormat;
		}

		@Override
		public T intern(T publishedFormat) {
			return publishedFormat;
		}
	}

}
