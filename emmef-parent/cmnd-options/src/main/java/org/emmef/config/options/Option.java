package org.emmef.config.options;

public interface Option {
	/**
	 * Returns a description of the option in a synopsis
	 */
	String getDescription();

	/**
	 * Gets the name of the option, that can occur in a synopsis or command line description (Usage) 
	 */
	String getName();
	
	/**
	 * Returns whether the option is mandatory.
	 * 
	 * <p>An option that is part of a group can be mandatory. If the 
	 * whole group is missing, though, this won't lead to problems.</p>
	 */
	boolean isMandatory();
	
	/**
	 * Returns if the option is in a valid state.
	 * 
	 * Returns <code>false</code> when
	 * <ul>
	 * <li>the option is mandatory and it is missing from the command line</li>
	 * <li>the option is mandatory but couldn't be set due to restrictions or parse problem</li>
	 * </ul>
	 * <p>The program should have been aborted in the above cases, but you can 
	 * still verify that the option is valid using this method.</p>
	 *  
	 */
	boolean isValid();
}
