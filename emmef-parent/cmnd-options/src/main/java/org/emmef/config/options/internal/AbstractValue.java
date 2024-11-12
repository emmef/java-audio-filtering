package org.emmef.config.options.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.emmef.config.options.Parser;
import org.emmef.config.options.Validator;
import org.emmef.config.options.Value;
import org.emmef.config.options.Validator.Result;

class AbstractValue<T> implements ParseNode, Value<T> {
	private final Parser<T> parser;
	private final Mandatory mandatory;
	private final int number;
	private final Class<T> type;
	private final AtomicReference<T> candidateValue = new AtomicReference<T>();
	private String description;
	private String name;
	private Validator<T> validator;
	private List<T> defaults = Collections.emptyList();
	private List<T> values = new ArrayList<T>();
	private boolean isMultiple;
	private boolean isHandled;

	public AbstractValue(Class<T> type, Parser<T> parser, Mandatory mandatory, int number) {
		if (type == null) {
			throw new NullPointerException("type");
		}
		if (parser == null) {
			throw new NullPointerException("parser");
		}
		if (mandatory == null) {
			throw new NullPointerException("mandatory");
		}
		this.type = type;
		this.parser = parser;
		this.mandatory = mandatory;
		this.number = number;
	}
	
	@Override
	public Value<T> defaults(@SuppressWarnings("unchecked") T... defaults) {
		final List<T> digest = getArrayDigest(isMultiple() ? "defaults" : "default", defaults);
		if (isAbsolutelyMandatory()) {
			if (!digest.isEmpty()) {
				throw new IllegalStateException(this + " is mandatory and cannot have defaults");
			}
			this.defaults = Collections.emptyList();
			return this;
		}
		
		if (digest.isEmpty()) {
			if (isMultiple) {
				throw new IllegalArgumentException(this + " needs to have at least one default value");
			}
			else {
				throw new IllegalArgumentException(this + " needs to have default value");
			}
		}
		
		if (!isMultiple() && digest.size() > 1) {
			throw new IllegalArgumentException(this + " is single-valued and cannot have more than one default value");
		}
		
		if (validator != null) {
			for (T value : digest) {
				final Result<T> check = validator.check(value);
				final String errorMessage = check.getErrorMessage();
				if (errorMessage != null) {
					throw new IllegalArgumentException(this + " cannot have default that is invalid: " + errorMessage);
				}
			}
		}
		this.defaults = digest;
		return this;
	}
	
	public List<T> getDefaults() {
		return Collections.unmodifiableList(defaults);
	}

	@Override
	public Value<T> describedBy(String description) {
		if (description == null) {
			throw new NullPointerException("description");
		}
		this.description = description;
		return this;
	}
	
	@Override
	public Value<T> name(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		this.name = name;
		return this;
	}

	@Override
	public List<T> values() {
		if (!values.isEmpty()) {
			return values;
		}
		if (isAbsolutelyMandatory()) {
			throw new IllegalStateException(this + " is mandatory, but has no values set");
		}
		
		return defaults;
	}

	@Override
	public T getValue() {
		final List<T> v = values();
		
		return v.isEmpty() ? null : v.get(0);
	}

	@Override
	public T getValue(int index) {
		return values.get(index);
	}

	@Override
	public Value<T> multiple() {
		this.isMultiple = true;
		return this;
	}

	@Override
	public Value<T> validatedBy(Validator<T> validator) {
		if (validator == null) {
			throw new NullPointerException("validator");
		}

		for (T value : defaults) {
			final Result<T> check = validator.check(value);
			final String errorMessage = check.getErrorMessage();
			if (errorMessage != null) {
				throw new IllegalArgumentException(this + " cannot have validator that conflicts with default value: " + errorMessage);
			}
		}
		
		this.validator = validator;
		return this;
	}
	
	public Validator<T> getValidator() {
		return validator;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String getName() {
		if (name == null) {
			return isMultiple() ? type.getSimpleName() + "..." : type.getSimpleName();
		}
		return name + " (" + type.getSimpleName() + (isMultiple() ? ")..." : ")");
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public String getShortName() {
		if (name == null) {
			return isMultiple() ? type.getSimpleName() + "..." : type.getSimpleName();
		}
		return isMultiple() ? name + "..." : name;
	}
	
	@Override
	public String getLongName() {
		return getName();
	}
	
	public boolean isAbsolutelyMandatory() {
		return mandatory == Mandatory.YES;
	}
	
	public boolean failOnParseError() {
		return false;
	}
	
	@Override
	public boolean isMultiple() {
		return isMultiple;
	}
	
	private List<T> getArrayDigest(String arrayDescription, T[] values) {
		if (values == null) {
			throw new NullPointerException("values");
		}
		if (values.length == 0) {
			return Collections.emptyList();
		}
		List<T> digest = new ArrayList<T>();
		for (T value : values) {
			if (value == null) {
				throw new IllegalArgumentException(this + ": " + arrayDescription + " cannot be null");
			}
			digest.add(value);
		}
		
		return digest.isEmpty() ? Collections.<T>emptyList() : Collections.<T>unmodifiableList(digest);
	}

	@Override
	public Mandatory getMandatory() {
		return mandatory;
	}

	@Override
	public boolean matches(String argument) {
		final T parsedValue = parser.parse(argument);
		if (parsedValue == null) {
			if (failOnParseError()) {
				throw new IllegalStateException(this + " failed to parse " + argument);
			}
			return false;
		}
		if (!candidateValue.compareAndSet(null, parsedValue)) {
			throw new IllegalStateException();
		}
		
		return candidateValue.get() != null;
	}

	@Override
	public void handleArgument(String matchedArgument, Tail tail) {
		T candidate = candidateValue.getAndSet(null);
		if (candidate == null) {
			throw new IllegalStateException("INTERNAL ERROR");
		}
		if (validator == null) {
			setValue(candidate, tail);
			return;
		}
		final Result<T> result = validator.check(candidate);
		final String errorMessage = result.getErrorMessage();
		final T newValue = result.getValue();
		if (errorMessage != null) {
			if (newValue == null) {
				throw new IllegalArgumentException(this + ": " + errorMessage);
			}
			setValue(newValue, tail);
			return;
		}
		if (newValue == null) {
			throw new IllegalStateException("INTERNAL ERROR");
		}
		setValue(newValue, tail);
	}

	@Override
	public boolean isHandled() {
		return isHandled;
	}
	
	@Override
	public boolean isBroadMatch() {
		return true;
	}
	
	@Override
	public int getNumber() {
		return number;
	}

	private void setValue(final T parsedValue, Tail tail) {
		values.add(parsedValue);
		isHandled = true;
		tail.proceed();
	}
}
