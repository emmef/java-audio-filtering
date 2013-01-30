package org.emmef.logging;

import java.util.Formatter;

import org.slf4j.Marker;

public interface FormatLogger extends org.slf4j.Logger {

	/**
	 * Logs an object (toString) at the TRACE level. The object
	 * {@link Object#toString()} is only called when it is actually logged.
	 */
	void trace(Object object);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void trace(String format, Object arg1, Object arg2);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void trace(String format, Object... arguments);

	/**
	 * Logs an object (toString) at the TRACE level, considering the marker. The
	 * object {@link Object#toString()} is only called when it is actually
	 * logged.
	 */
	void trace(Marker marker, Object object);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void trace(Marker marker, String format, Object arg);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void trace(Marker marker, String format, Object arg1, Object arg2);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void trace(Marker marker, String format, Object... argArray);

	/**
	 * Logs an object (toString) at the DEBUG level. The object
	 * {@link Object#toString()} is only called when it is actually logged.
	 */
	void debug(Object object);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void debug(String format, Object arg);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void debug(String format, Object arg1, Object arg2);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void debug(String format, Object... arguments);

	/**
	 * Logs an object (toString) at the DEBUG level, considering the marker. The
	 * object {@link Object#toString()} is only called when it is actually
	 * logged.
	 */
	void debug(Marker marker, Object object);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void debug(Marker marker, String format, Object arg);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void debug(Marker marker, String format, Object arg1, Object arg2);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void debug(Marker marker, String format, Object... arguments);

	@Override
	void debug(Marker marker, String msg, Throwable t);

	/**
	 * Logs an object (toString) at the INFO level. The object
	 * {@link Object#toString()} is only called when it is actually logged.
	 */
	void info(Object object);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void info(String format, Object arg);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void info(String format, Object arg1, Object arg2);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void info(String format, Object... arguments);

	/**
	 * Logs an object (toString) at the INFO level, considering the marker. The
	 * object {@link Object#toString()} is only called when it is actually
	 * logged.
	 */
	void info(Marker marker, Object object);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void info(Marker marker, String format, Object arg);

	@Override
	/**
	 * {@inheritDoc}<p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	void info(Marker marker, String format, Object arg1, Object arg2);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void info(Marker marker, String format, Object... arguments);

	/**
	 * Logs an object (toString) at the WARN level. The object
	 * {@link Object#toString()} is only called when it is actually logged.
	 */
	void warn(Object object);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void warn(String format, Object arg);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void warn(String format, Object... arguments);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void warn(String format, Object arg1, Object arg2);

	/**
	 * Logs an object (toString) at the WARN level, considering the marker. The
	 * object {@link Object#toString()} is only called when it is actually
	 * logged.
	 */
	void warn(Marker marker, Object object);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void warn(Marker marker, String format, Object arg);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void warn(Marker marker, String format, Object arg1, Object arg2);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void warn(Marker marker, String format, Object... arguments);

	/**
	 * Logs an object (toString) at the INFO level. The object
	 * {@link Object#toString()} is only called when it is actually logged.
	 */
	void error(Object object);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void error(String format, Object arg);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void error(String format, Object arg1, Object arg2);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void error(String format, Object... arguments);

	/**
	 * Logs an object (toString) at the ERROR level, considering the marker. The
	 * object {@link Object#toString()} is only called when it is actually
	 * logged.
	 */
	void error(Marker marker, Object object);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void error(Marker marker, String format, Object arg);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void error(Marker marker, String format, Object arg1, Object arg2);

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Important</strong>: The {@code format} and {@code arguments} are
	 * interpreted by {@link Formatter}.
	 */
	@Override
	void error(Marker marker, String format, Object... arguments);
}
