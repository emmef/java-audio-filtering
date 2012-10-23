package org.emmef.logging;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class Logger {
	private static final Logger DEFAULT = new Logger();
	private final PrintStream printStream;
	
	public static final Logger getDefault() {
		return DEFAULT;
	}
	
	private volatile Level level;
	
	private final ThreadLocal<StringBuilder> buffer = new ThreadLocal<StringBuilder>() {
		protected StringBuilder initialValue() {
			return new StringBuilder(100);
		}
	};
	private final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
		}
	};
	
	public Logger(Level level) {
		PrintStream pStream = null;
		try {
			FileOutputStream stream = new FileOutputStream("./log.txt");
			pStream = new PrintStream(stream);
		}
		catch (IOException e) {
			// ignore
		}
		this.printStream = pStream;
		this.level = level;
	}
	
	public Logger() {
		this(Level.INFO);
	}
	
	public void setLevel(Level level) {
		if (level == null) {
			throw new NullPointerException("level");
		}
		this.level = level;
	}
	
	public void trace(String format, Object... args) {
		logOnLevel(Level.FINEST, format, args);
	}
	
	public void trace(Object o) {
		logOnLevel(Level.FINEST, o);
	}
	
	public void debug(String format, Object... args) {
		logOnLevel(Level.FINER, format, args);
	}
	
	public void debug(Object o) {
		logOnLevel(Level.FINER, o);
	}
	
	public void fine(String format, Object... args) {
		logOnLevel(Level.FINE, format, args);
	}
	
	public void fine(Object o) {
		logOnLevel(Level.FINE, o);
	}
	
	public void config(String format, Object... args) {
		logOnLevel(Level.CONFIG, format, args);
	}
	
	public void config(Object o) {
		logOnLevel(Level.CONFIG, o);
	}
	
	public void info(String format, Object... args) {
		logOnLevel(Level.INFO, format, args);
	}
	
	public void info(Object o) {
		logOnLevel(Level.INFO, o);
	}
	
	public void warn(String format, Object... args) {
		logOnLevel(Level.WARNING, format, args);
	}
	
	public void warn(Object o) {
		logOnLevel(Level.WARNING, o);
	}
	
	public void error(String format, Object... args) {
		logOnLevel(Level.SEVERE, format, args);
	}
	
	public void error(Object o) {
		logOnLevel(Level.SEVERE, o);
	}


	private void logOnLevel(final Level level, String format, Object... args) {
		if (skipLog(level)) {
			return;
		}
		log(level, format, args);
	}

	private void logOnLevel(final Level level, Object o) {
		if (skipLog(level)) {
			return;
		}
		log(level, o != null ? o.toString() : "null");
	}

	private void log(Level level, String format, Object... args) {
		if (level.intValue() >= Level.WARNING.intValue()) {
			log(System.err, format,args);
		}
		else {
			log(System.out, format,args);
		}
	}
	
	private void log(PrintStream stream, String format, Object[] args) {
		final StringBuilder text = buffer.get();
		text.append(dateFormat.get().format(new Date()));
		final String threadName = Thread.currentThread().getName();
		int l = threadName.length();
		text.append(" | ").append(threadName);
		while (l < 25) {
			text.append(' ');
			l++;
		}
		text.append(" | ");
		if (args == null || args.length == 0) {
			text.append(format.replace("\n", "\n" + text.toString()));
		}
		else {
			text.append(String.format(format, args).replace("\n", "\n" + text.toString()));
		}
		
		final String toString = text.toString();
		stream.println(toString);
		if (printStream != null) {
			printStream.println(toString);
		}
		text.setLength(0);
	}

	private boolean skipLog(Level finest) {
		return finest.intValue() < level.intValue();
	}
}
