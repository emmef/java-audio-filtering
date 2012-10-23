package org.emmef.config.options.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.emmef.config.options.NumberValue;
import org.emmef.config.options.Parser;
import org.emmef.config.options.SwitchBuilder;
import org.emmef.config.options.Value;
import org.emmef.config.options.ValueBuilder;
import org.emmef.config.options.parsers.BooleanParser;
import org.emmef.config.options.parsers.FileParser;
import org.emmef.config.options.parsers.IntegerParser;
import org.emmef.config.options.parsers.LongIntParser;
import org.emmef.config.options.parsers.RealParser;
import org.emmef.config.options.parsers.TextParser;

public class DefaultSwitchBuilder implements SwitchBuilder, ParseNode, ParentNode {
	private static final AtomicInteger numbers = new AtomicInteger();

	private final NodeSet values;
	private final Mandatory mandatory;
	private final List<String> switches;
	private final Mandatory mandatoryChildren;
	private final String name;
	private final String shortName;
	private boolean isHandled;
	private final int number;
	private String description;


	private DefaultSwitchBuilder(Mandatory mandatory, Mandatory mandatoryChildren, NodeSet values, List<String> switches, String name, String shortName, int number, String description) {
		this.values = values;
		this.mandatory = mandatory;
		this.mandatoryChildren = mandatoryChildren;
		this.switches = switches;
		this.name = name;
		this.shortName = shortName;
		this.number = number;
		this.description = description;
	}	

	static DefaultSwitchBuilder create(Mandatory mandatory, int number, String switch1, String... switches) {
		if (mandatory == null) {
			throw new NullPointerException("mandatory");
		}
		List<String> list;
		checkSwitch(switch1);
		if (switches != null && switches.length > 0) {
			list = new ArrayList<String>();
			list.add(switch1);
			for (String sw : switches) {
				checkSwitch(sw);
				list.add(sw);
			}
		}
		else {
			list = Collections.singletonList(switch1);
		}
		String longestName = "";
		for (String sw : list) {
			if (sw.length() > longestName.length()) {
				longestName = sw;
			}
		}
		String shortestName = longestName;
		for (String sw : list) {
			if (sw.length() < shortestName.length()) {
				shortestName = sw;
			}
		}
			
		return new DefaultSwitchBuilder(mandatory, Mandatory.NO, new NodeSet(), Collections.unmodifiableList(list), longestName, shortestName, number, null);
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public SwitchBuilder optional(String switch1, String... additionalSwitches) {
		final DefaultSwitchBuilder builder = DefaultSwitchBuilder.create(Mandatory.NO, numbers.getAndIncrement(), switch1, additionalSwitches);
		values.add(builder);
		return builder;
	}
	
	@Override
	public SwitchBuilder mandatory(String switch1, String... additionalSwitches) {
		Mandatory childMandatory;
		if (mandatory == Mandatory.YES) {
			childMandatory = Mandatory.YES;
		}
		else {
			childMandatory = Mandatory.RELATIVE;
		}
		final DefaultSwitchBuilder builder = DefaultSwitchBuilder.create(childMandatory, numbers.getAndIncrement(), switch1, additionalSwitches);
		values.add(builder);
		return builder;
	}
	
	@Override
	public ValueBuilder mandatory() {
		Mandatory childMandatory;
		if (mandatory == Mandatory.YES) {
			childMandatory = Mandatory.YES;
		}
		else {
			childMandatory = Mandatory.RELATIVE;
		}
		return new DefaultSwitchBuilder(mandatory, childMandatory, values, switches, name, shortName, numbers.getAndIncrement(), description);
	}
	
	@Override
	public DefaultSwitchBuilder describedBy(String description) {
		if (description == null) {
			throw new NullPointerException("description");
		}
		this.description = description;
		return this;
	}
	
	@Override
	public Mandatory getMandatory() {
		return mandatory;
	}
	
	@Override
	public String toString() {
		return name;
	}
	@Override
	public boolean matches(String argument) {
		for (String sw : switches) {
			if (sw.equals(argument)) {
				return true;
			}
			final int length = sw.length();
			final int argLength = argument.length();
			if (argLength > sw.length() + 1 && argument.charAt(length) == '=') {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void handleArgument(String argument, Tail tail) {
		boolean success = false;
		try {
			for (String sw : switches) {
				if (sw.equals(argument)) {
					tail.proceed();
					if (tail.done()) {
						return;
					}
					final String next = tail.get();
					handle(next, tail);
					return;
				}
				final int length = sw.length();
				final int argLength = argument.length();
				if (argLength > sw.length() + 1 && argument.charAt(length) == '=') {
					handle(argument.substring(length + 1), tail);
					return;
				}
			}
			success = true;
		}
		catch (RuntimeException e) {
			throw e;
		}
		finally {
			isHandled = true;
			if (success) {
				values.checkMandatoryNodes(this);
			}
		}
	}

	@Override
	public boolean isHandled() {
		return isHandled;
	}
	
	@Override
	public boolean present() {
		return isHandled();
	}
	
	@Override
	public boolean isMultiple() {
		return false;
	}
	
	@Override
	public boolean isBroadMatch() {
		return false;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getShortName() {
		return shortName;
	}
	
	@Override
	public String getLongName() {
		StringBuilder builder = new StringBuilder();
		for (String sw : switches) {
			builder.append("|").append(sw);
		}
		return builder.substring(1);
	}

	@Override
	public Value<File> file() {
		return values.add(new AbstractValue<File>(File.class, FileParser.INSTANCE, mandatoryChildren, numbers.getAndIncrement()));
	}

	@Override
	public Value<Boolean> flag() {
		return values.add(new AbstractValue<Boolean>(Boolean.class, BooleanParser.INSTANCE, mandatoryChildren, numbers.getAndIncrement()));
	}

	@Override
	public NumberValue<Integer> integer() {
		return values.add(new AbstractNumberValue<Integer>(Integer.class, IntegerParser.BOTH, mandatoryChildren, numbers.getAndIncrement()));
	}

	@Override
	public NumberValue<Long> longint() {
		return values.add(new AbstractNumberValue<Long>(Long.class, LongIntParser.BOTH, mandatoryChildren, numbers.getAndIncrement()));
	}

	@Override
	public NumberValue<Double> real() {
		return values.add(new AbstractNumberValue<Double>(Double.class, RealParser.INSTANCE, mandatoryChildren, numbers.getAndIncrement()));
	}

	@Override
	public Value<String> text() {
		return values.add(new AbstractValue<String>(String.class, TextParser.INSTANCE, mandatoryChildren, numbers.getAndIncrement()));
	}

	@Override
	public <T> Value<T> value(Class<T> type, Parser<T> parser) {
		return values.add(new AbstractValue<T>(type, parser, mandatoryChildren, numbers.getAndIncrement()));
	}

	@Override
	public <T extends Number & Comparable<T>> NumberValue<T> number(Class<T> type, Parser<T> parser) {
		return values.add(new AbstractNumberValue<T>(type, parser, mandatoryChildren, numbers.getAndIncrement()));
	}
	
	@Override
	public int getNumber() {
		return number;
	}

	public static void checkSwitch(String sw) {
		if (sw == null) {
			throw new IllegalArgumentException("Switches cannot have a null name");
		}
		if (sw.charAt(0) != '-') {
			throw new IllegalArgumentException("Switches need to start with '-'");
		}
	}
	
	@Override
	public Set<ParseNode> children() {
		return values.getDisplayNodes();
	}

	private void handle(String tryMatch, Tail tail) {
		ParseNode node = values.findMatch(tryMatch);
		String argument = tryMatch; 
		int positionCheck = tail.getPosition();
		while (node != null) {
			node.handleArgument(argument, tail);
			int newPosition = tail.getPosition();
			if (newPosition == positionCheck || tail.done()) {
				return;
			}
			argument = tail.get();
			positionCheck = newPosition;
			node = values.findMatch(argument); 
		}
	}
}
