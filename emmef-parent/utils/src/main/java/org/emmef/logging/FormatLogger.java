package org.emmef.logging;

import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class FormatLogger implements Logger {
	private static final String TOSTRING_FORMAT = "{}";
	
	public static FormatLogger getLogger(Class<?> clazz) {
		return new FormatLogger(LoggerFactory.getLogger(clazz));
	}
	public static Logger getLogger(String name) {
		return new FormatLogger(LoggerFactory.getLogger(name));
	}
	
	final org.slf4j.Logger delegate;

	public FormatLogger(Logger delegate) {
		this.delegate = delegate;
	}
	/**
	 * Returns the underlying logger.
	 * <p>
	 * The {@link FormatLogger} should always be a delegate to another logger,
	 * as it only replaces the formatting of a normal logger with that of
	 * {@link Formatter}.
	 * 
	 * @return the underlying logger
	 */
	public Logger logger() {
		return delegate;
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return delegate.isTraceEnabled();
	}

	@Override
	public void trace(String msg) {
		delegate.trace(msg);
	}

	@Override
	public void trace(String format, Object arg) {
		if (isTraceEnabled()) {
			delegate.trace(TOSTRING_FORMAT, new LogRecord(TOSTRING_FORMAT, arg));
		}
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		if (isTraceEnabled()) {
			delegate.trace(TOSTRING_FORMAT, new LogRecord(format, arg1, arg2));
		}
	}

	@Override
	public void trace(String format, Object... arguments) {
		if (isTraceEnabled()) {
			delegate.trace(TOSTRING_FORMAT, new LogRecord(format, arguments));
		}
	}

	@Override
	public void trace(String msg, Throwable t) {
		delegate.trace(msg, t);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return delegate.isTraceEnabled(marker);
	}

	@Override
	public void trace(Marker marker, String msg) {
		if (isTraceEnabled(marker)) {
			delegate.trace(marker, TOSTRING_FORMAT, new LogRecord(msg));
		}
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		if (isTraceEnabled(marker)) {
			delegate.trace(marker, TOSTRING_FORMAT, new LogRecord(format, arg));
		}
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		if (isTraceEnabled(marker)) {
			delegate.trace(marker, TOSTRING_FORMAT, new LogRecord(format, arg1, arg2));
		}
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		if (isTraceEnabled(marker)) {
			delegate.trace(marker, TOSTRING_FORMAT, new LogRecord(format, argArray));
		}
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		delegate.trace(marker, msg, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return delegate.isDebugEnabled();
	}

	@Override
	public void debug(String msg) {
		delegate.debug(msg);
	}

	@Override
	public void debug(String format, Object arg) {
		if (isDebugEnabled()) {
			delegate.debug(TOSTRING_FORMAT, new LogRecord(format, arg));
		}
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		if (isDebugEnabled()) {
			delegate.debug(TOSTRING_FORMAT, new LogRecord(format, arg1, arg2));
		}
	}

	@Override
	public void debug(String format, Object... arguments) {
		if (isDebugEnabled()) {
			delegate.debug(TOSTRING_FORMAT, new LogRecord(format, arguments));
		}
	}

	@Override
	public void debug(String msg, Throwable t) {
		delegate.debug(msg, t);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return delegate.isDebugEnabled(marker);
	}

	@Override
	public void debug(Marker marker, String msg) {
		if (isDebugEnabled(marker)) {
			delegate.debug(marker, TOSTRING_FORMAT, new LogRecord(msg));
		}
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		if (isDebugEnabled(marker)) {
			delegate.debug(marker, TOSTRING_FORMAT, new LogRecord(format, arg));
		}
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		if (isDebugEnabled(marker)) {
			delegate.debug(marker, TOSTRING_FORMAT, new LogRecord(format, arg1, arg2));
		}
	}

	@Override
	public void debug(Marker marker, String format, Object... arguments) {
		if (isDebugEnabled(marker)) {
			delegate.debug(marker, TOSTRING_FORMAT, new LogRecord(format, arguments));
		}
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		delegate.debug(marker, msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return delegate.isInfoEnabled();
	}

	@Override
	public void info(String msg) {
		delegate.info(msg);
	}
	
	@Override
	public void info(String format, Object arg) {
		if (isInfoEnabled()) {
			delegate.info(TOSTRING_FORMAT, new LogRecord(format, arg));
		}
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		if (isInfoEnabled()) {
			delegate.info(TOSTRING_FORMAT, new LogRecord(format, arg1, arg2));
		}
	}

	@Override
	public void info(String format, Object... arguments) {
		if (isInfoEnabled()) {
			delegate.info(TOSTRING_FORMAT, new LogRecord(format, arguments));
		}
	}

	@Override
	public void info(String msg, Throwable t) {
		delegate.info(msg, t);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return delegate.isInfoEnabled(marker);
	}

	@Override
	public void info(Marker marker, String msg) {
		if (isInfoEnabled(marker)) {
			delegate.info(marker, TOSTRING_FORMAT, new LogRecord(msg));
		}
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		if (isInfoEnabled(marker)) {
			delegate.info(marker, TOSTRING_FORMAT, new LogRecord(format, arg));
		}
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		if (isInfoEnabled(marker)) {
			delegate.info(marker, TOSTRING_FORMAT, new LogRecord(format, arg1, arg2));
		}
	}

	@Override
	public void info(Marker marker, String format, Object... arguments) {
		if (isInfoEnabled(marker)) {
			delegate.info(marker, TOSTRING_FORMAT, new LogRecord(format, arguments));
		}
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		delegate.info(marker, msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return delegate.isWarnEnabled();
	}

	@Override
	public void warn(String msg) {
		delegate.warn(msg);
	}

	@Override
	public void warn(String format, Object arg) {
		if (isWarnEnabled()) {
			delegate.warn(TOSTRING_FORMAT, new LogRecord(format, arg));
		}
	}

	@Override
	public void warn(String format, Object... arguments) {
		if (isWarnEnabled()) {
			delegate.warn(TOSTRING_FORMAT, new LogRecord(format, arguments));
		}
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		if (isWarnEnabled()) {
			delegate.warn(TOSTRING_FORMAT, new LogRecord(format, arg1, arg2));
		}
	}

	@Override
	public void warn(String msg, Throwable t) {
		delegate.warn(msg, t);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return delegate.isWarnEnabled(marker);
	}

	@Override
	public void warn(Marker marker, String msg) {
		if (isWarnEnabled(marker)) {
			delegate.warn(marker, TOSTRING_FORMAT, new LogRecord(msg));
		}
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		if (isWarnEnabled(marker)) {
			delegate.warn(marker, TOSTRING_FORMAT, new LogRecord(format, arg));
		}
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		if (isWarnEnabled(marker)) {
			delegate.warn(marker, TOSTRING_FORMAT, new LogRecord(format, arg1, arg2));
		}
	}

	@Override
	public void warn(Marker marker, String format, Object... arguments) {
		if (isWarnEnabled(marker)) {
			delegate.warn(marker, TOSTRING_FORMAT, new LogRecord(format, arguments));
		}
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		delegate.warn(marker, msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return delegate.isErrorEnabled();
	}

	@Override
	public void error(String msg) {
		delegate.error(msg);
	}

	@Override
	public void error(String format, Object arg) {
		if (isErrorEnabled()) {
			delegate.error(TOSTRING_FORMAT, new LogRecord(format, arg));
		}
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		if (isErrorEnabled()) {
			delegate.error(TOSTRING_FORMAT, new LogRecord(format, arg1, arg2));
		}
	}

	@Override
	public void error(String format, Object... arguments) {
		if (isErrorEnabled()) {
			delegate.error(TOSTRING_FORMAT, new LogRecord(format, arguments));
		}
	}

	@Override
	public void error(String msg, Throwable t) {
		delegate.error(msg, t);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return delegate.isErrorEnabled(marker);
	}

	@Override
	public void error(Marker marker, String msg) {
		if (isErrorEnabled(marker)) {
			delegate.error(marker, TOSTRING_FORMAT, new LogRecord(msg));
		}
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		if (isErrorEnabled(marker)) {
			delegate.error(marker, TOSTRING_FORMAT, new LogRecord(format, arg));
		}
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		if (isErrorEnabled(marker)) {
			delegate.error(marker, TOSTRING_FORMAT, new LogRecord(format, arg1, arg2));
		}
	}

	@Override
	public void error(Marker marker, String format, Object... arguments) {
		if (isErrorEnabled(marker)) {
			delegate.error(marker, TOSTRING_FORMAT, new LogRecord(format, arguments));
		}
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		delegate.error(marker, msg, t);
	}

	public void trace(Object object) {
		delegate.trace(TOSTRING_FORMAT, object);
	}

	public void trace(Marker marker, Object object) {
		delegate.trace(marker, TOSTRING_FORMAT, object);
	}

	public void debug(Object object) {
		delegate.debug(TOSTRING_FORMAT, object);
	}

	public void debug(Marker marker, Object object) {
		delegate.debug(marker, TOSTRING_FORMAT, object);
	}

	public void info(Object object) {
		delegate.info(TOSTRING_FORMAT, object);
	}

	public void info(Marker marker, Object object) {
		delegate.info(marker, TOSTRING_FORMAT, object);
	}

	public void warn(Object object) {
		delegate.warn(TOSTRING_FORMAT, object);
	}

	public void warn(Marker marker, Object object) {
		delegate.warn(marker, TOSTRING_FORMAT, object);
	}

	public void error(Object object) {
		delegate.error(TOSTRING_FORMAT, object);
	}

	public void error(Marker marker, Object object) {
		delegate.error(marker, TOSTRING_FORMAT, object);
	}

	private static class LogRecord {
		private final String format;
		private final Object[] arguments;

		LogRecord(String format, Object...arguments) {
			this.format = format;
			this.arguments = arguments;
		}
		
		@Override
		public String toString() {
			return String.format(format, arguments);
		}
	}
}
