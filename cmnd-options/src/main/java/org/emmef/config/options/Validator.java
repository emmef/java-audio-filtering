package org.emmef.config.options;

public interface Validator<T> {
	/**
	 * Checks the provided argument value.
	 * 
	 * <p>The result contains the validated value and a message that indicates what is wrong.</p>
	 * <p>If the argumentValue is wrong, a message will be set in the result. If the error 
	 * is correctable, a corrected value will be in the result. If the error is not 
	 * correctable, the value in the result will be <code>null</code>.</p>
	 *  
	 * @param argumentValue
	 */
	Result<T> check(T argumentValue);
	
	class Result<T> {
		private final T value;
		private final String errorMessage;
		
		public static <T> Result<T> create(T value) {
			if (value == null) {
				throw new NullPointerException("value");
			}
			return new Result<T>(value, null);
		}
		
		public static <T> Result<T> create(T value, String errorMessage) {
			if (errorMessage == null) {
				throw new NullPointerException("errorMessage");
			}
			return new Result<T>(value, errorMessage); 
		}
		
		private Result(T value, String errorMessage) {
			this.value = value;
			this.errorMessage = errorMessage;
		}
		
		public T getValue() {
			return value;
		}
		
		public String getErrorMessage() {
			return errorMessage;
		}
	}
}
