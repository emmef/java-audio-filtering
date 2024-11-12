package org.emmef.config.options.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Tail {
	private final List<String> tail;
	private final boolean[] ignored;
	private int position; 

	private Tail(List<String> tail) {
		this.tail = tail;
		this.ignored = new boolean[tail.size()];
		this.position = 0;
	}
	
	public final String get() {
		return tail.get(position);
	}
	
	public final boolean proceed() {
		if (position < ignored.length) {
			position++;
			return true;
		}
		
		return false;
	}
	
	public void ignore() {
		ignored[position] = true;
	}
	
	public boolean done() {
		return position >= ignored.length;
	}
	
	public List<String> getSuperfluousItems() {
		if (position < ignored.length) {
			return tail.subList(position, ignored.length);
		}
		return Collections.emptyList();
	}
	
	public List<String> getIgnoredItems() {
		List<String> ignoredItems = new ArrayList<String>();
		for (int i = 0; i < ignored.length; i++) {
			if (ignored[i]) {
				ignoredItems.add(tail.get(i));
			}
		}
		return ignoredItems.isEmpty() ? Collections.<String>emptyList() : ignoredItems;
	}

	
	public int getPosition() {
		return position;
	}

	public static Tail create(String... arguments) {
		if (arguments == null) {
			throw new NullPointerException("arguments");
		}
		for (String argument : arguments) {
			if (argument == null) {
				throw new NullPointerException("argument");
			}
		}
		if (arguments.length == 0) {
			return new Tail(Collections.<String>emptyList());
		}
		else {
			return new Tail(Collections.unmodifiableList(Arrays.asList(arguments)));
		}
	}

	public static Tail create(List<String> arguments) {
		if (arguments == null) {
			throw new NullPointerException("arguments");
		}
		List<String> copy = new ArrayList<String>(arguments);
		for (String argument : copy) {
			if (argument == null) {
				throw new NullPointerException("argument");
			}
		}
		if (copy.isEmpty()) {
			return new Tail(Collections.<String>emptyList());
		}
		else {
			return new Tail(Collections.unmodifiableList(copy));
		}
	}
}
