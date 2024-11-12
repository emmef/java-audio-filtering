package org.emmef.utils;

import java.io.EOFException;

/**
 * Decides what happens when an input stream could not read or skip the
 * expected number of bytes.
 */
public interface EndOfFile {
	enum Action {
		THROW,
		CONTINUE
	};
	
	interface Result {
		Action getAction();
		String getErrorMessage();
		
		Result CONTINUE = new Result() {
			@Override
			public Action getAction() {
				return Action.CONTINUE;
			}
			@Override
			public String getErrorMessage() {
				return null;
			}
		};
		
		final class Throw implements Result {
			private final String errorMessage;
			
			public Throw(String errorMessage) {
				this.errorMessage = errorMessage;
			}

			@Override
			public Action getAction() {
				return Action.THROW;
			}

			@Override
			public String getErrorMessage() {
				return errorMessage;
			}
		}
	}
	
	interface Event {
		Result onEndOfFile(long requestedNumber, long actualNumber) throws EOFException;
		
		Event CONTINUE = new Event() {
			@Override
			public Result onEndOfFile(long requestedNumber, long actualNumber) throws EOFException {
				return new Result() {
					@Override
					public Action getAction() {
						return Action.CONTINUE;
					}
					@Override
					public String getErrorMessage() {
						return null;
					}
				};
			}
		};
		
		/**
		 * If less bytes could be read than requested, throw an {@link EOFException}.
		 */
		Event THROW = new Event() {
			@Override
			public Result onEndOfFile(long requestedNumber, long actualNumber) throws EOFException {
				return new Result() {
					@Override
					public Action getAction() {
						return Action.THROW;
					}
					@Override
					public String getErrorMessage() {
						return null;
					}
				};
			}
		};
	}
	
	class Handler {
		public static void handle(EndOfFile.Event event, long requestedNumber, long actualNumber) throws EOFException {
			Result onEndOfFile = event.onEndOfFile(requestedNumber, actualNumber);
			
			switch (onEndOfFile.getAction()) {
			case CONTINUE:
				return;
			case THROW:
				String errorMessage = onEndOfFile.getErrorMessage();
				if (errorMessage != null) {
					throw new EOFException(errorMessage);
				}
				else {
					throw new EOFException("EOF");
				}
			}
			
		}
	}
}