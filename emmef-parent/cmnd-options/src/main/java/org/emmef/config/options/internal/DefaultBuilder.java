package org.emmef.config.options.internal;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

import org.emmef.config.options.Builder;
import org.emmef.config.options.NumberValue;
import org.emmef.config.options.Parser;
import org.emmef.config.options.SwitchBuilder;
import org.emmef.config.options.Validator;
import org.emmef.config.options.Value;
import org.emmef.config.options.ValueBuilder;
import org.emmef.config.options.internal.ParseNode.Mandatory;
import org.emmef.config.options.parsers.BooleanParser;
import org.emmef.config.options.parsers.FileParser;
import org.emmef.config.options.parsers.IntegerParser;
import org.emmef.config.options.parsers.LongIntParser;
import org.emmef.config.options.parsers.RealParser;
import org.emmef.config.options.parsers.TextParser;

public class DefaultBuilder implements Builder, ParentNode {
	private static final AtomicInteger numbers = new AtomicInteger();
	private final Mandatory mandatory;
	private final NodeSet values;
	private String description;
	private boolean isPresent;

	private DefaultBuilder(NodeSet values, Mandatory mandatory, String description) {
		this.mandatory = mandatory;
		this.values = values;
		this.description = description;
	}
	
	public DefaultBuilder() {
		this(new NodeSet(), Mandatory.NO, null);
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public DefaultBuilder describedBy(String description) {
		if (description == null) {
			throw new NullPointerException("description");
		}
		this.description = description;
		return this;
	}
	
	@Override
	public void parse(String... commandline) {
		final Tail tail = Tail.create(commandline);
		
		while (!tail.done()) {
			final String argument = tail.get();
			final ParseNode node = values.findMatch(argument);
			if (node == null) {
				tail.ignore();
				tail.proceed();
			}
			else {
				node.handleArgument(argument, tail);
			}
		}
		
		values.checkMandatoryNodes(this);
		isPresent = true;
		final List<String> superfluousItems = tail.getSuperfluousItems();
		if (!superfluousItems.isEmpty()) {
			StringBuilder text = new StringBuilder();
			text.append("Superfluous items:");
			for (String item : superfluousItems) {
				text.append(" ").append(item);
			}
			System.err.println(text);
		}
	}
	
	@Override
	public boolean present() {
		return isPresent;
	}

	@Override
	public ValueBuilder mandatory() {
		return new DefaultBuilder(values, Mandatory.YES, description); 
	}

	@Override
	public SwitchBuilder optional(String switch1, String... switches) {
		final DefaultSwitchBuilder builder = DefaultSwitchBuilder.create(Mandatory.NO, numbers.getAndIncrement(), switch1, switches);
		values.add(builder);
		return builder;
	}
	
	@Override
	public SwitchBuilder mandatory(String switch1, String... switches) {
		final DefaultSwitchBuilder builder = DefaultSwitchBuilder.create(Mandatory.YES, numbers.getAndIncrement(), switch1, switches);
		values.add(builder);
		return builder;
	}


	@Override
	public Value<File> file() {
		return values.add(new AbstractValue<File>(File.class, FileParser.INSTANCE, mandatory, numbers.getAndIncrement()));
	}

	@Override
	public Value<Boolean> flag() {
		return values.add(new AbstractValue<Boolean>(Boolean.class, BooleanParser.INSTANCE, mandatory, numbers.getAndIncrement()));
	}

	@Override
	public NumberValue<Integer> integer() {
		return values.add(new AbstractNumberValue<Integer>(Integer.class, IntegerParser.BOTH, mandatory, numbers.getAndIncrement()));
	}

	@Override
	public NumberValue<Long> longint() {
		return values.add(new AbstractNumberValue<Long>(Long.class, LongIntParser.BOTH, mandatory, numbers.getAndIncrement()));
	}

	@Override
	public NumberValue<Double> real() {
		return values.add(new AbstractNumberValue<Double>(Double.class, RealParser.INSTANCE, mandatory, numbers.getAndIncrement()));
	}

	@Override
	public Value<String> text() {
		return values.add(new AbstractValue<String>(String.class, TextParser.INSTANCE, mandatory, numbers.getAndIncrement()));
	}

	@Override
	public <T> Value<T> value(Class<T> type, Parser<T> parser) {
		return values.add(new AbstractValue<T>(type, parser, mandatory, numbers.getAndIncrement()));
	}

	@Override
	public <T extends Number & Comparable<T>> NumberValue<T> number(Class<T> type, Parser<T> parser) {
		return values.add(new AbstractNumberValue<T>(type, parser, mandatory, numbers.getAndIncrement()));
	}
	
	@Override
	public Set<ParseNode> children() {
		return values.getDisplayNodes();
	}

	@Override
	public String getCommandLineSummary() {
		StringBuilder output = new StringBuilder();
		
		for (ParseNode node : children()) {
			addToCommandLine(output, node);
		}
		
		return output.toString();
	}
	
	@Override
	public String getSynopsis() {
		StringBuilder output = new StringBuilder();
		
		for (ParseNode node : children()) {
			addToSynpsis(output, node, 0);
		}
		
		return output.toString();
	}

	public static final <V> V checkNotNull(V value, String description) {
		if (value == null) {
			throw new NullPointerException(description);
		}
		
		return value;
	}
	
	private void addToCommandLine(StringBuilder output, ParseNode node) {
		final boolean optional = node.getMandatory() == Mandatory.NO;
		output.append(' ');
		if (optional) {
			output.append('[');
		}
		output.append(node.getShortName());
		if (node instanceof ParentNode) {
			for (ParseNode child : ((ParentNode)node).children()) {
				addToCommandLine(output, child);
			}
		}
		if (optional) {
			output.append(']');
		}		
	}
	

	private void addToSynpsis(StringBuilder output, ParseNode node, int indent) {
		int pos = output.length();
		AbstractValue<?> singleChild = null;
		appendIndent(output, indent).append(node.getLongName());
		if (node.getMandatory() != Mandatory.NO) {
			output.append(" (required)");
		}
		if (node instanceof AbstractValue<?>) {
			final AbstractValue<?> value = (AbstractValue<?>) node;
			addValueDetails(output, value);
		}
		else if (node instanceof ParentNode) {
			final Set<ParseNode> children = ((ParentNode)node).children();
			if (children.size() == 1) {
				final ParseNode childNode = children.iterator().next();
				if (childNode instanceof AbstractValue<?>) {
					singleChild = (AbstractValue<?>)childNode;
					if (singleChild.getMandatory() == Mandatory.NO) {
						output.append('[');
					}
					output.append(' ').append(singleChild.getLongName());
					addValueDetails(output, singleChild);
					if (singleChild.getMandatory() == Mandatory.NO) {
						output.append(']');
					}
				}
			}
		}
		final int lineLength = output.length() - pos;
		int descriptionIndent = Math.max(24, indent + 4);
		if (lineLength + 2 > descriptionIndent) {
			output.append('\n');
			appendIndent(output, descriptionIndent);
		}
		else {
			appendIndent(output, descriptionIndent - lineLength);
		}
		String description = node.getDescription();
		if (singleChild != null) {
			description += "\n" + singleChild.getDescription();
		}
		final StringTokenizer tokenizer = new StringTokenizer(description, "\n");
		if (tokenizer.hasMoreTokens()) {
			output.append(tokenizer.nextToken());
			while (tokenizer.hasMoreTokens()) {
				appendIndent(output.append("\n"), descriptionIndent).append(tokenizer.nextToken());
			}
		}
		output.append("\n");
		if (singleChild == null && node instanceof ParentNode) {
			for (ParseNode n : ((ParentNode)node).children()) {
				addToSynpsis(output, n, indent + 4);
			}
		}
	}

	private void addValueDetails(StringBuilder output, final AbstractValue<?> value) {
		final List<?> defaults = value.getDefaults();
		if (!defaults.isEmpty()) {
			output.append(" DEFAULTS ").append(defaults);
		}
		final Validator<?> validator = value.getValidator();
		if (validator != null) {
			output.append(' ').append(validator);
		}
	}

	private StringBuilder appendIndent(StringBuilder source, int indent) {
		for (int i = 0; i < indent; i++) {
			source.append(' ');
		}
		return source;
	}
}
