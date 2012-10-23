package org.emmef.config.options.internal;

import java.util.Comparator;


interface ParseNode {
	enum Mandatory { 
		YES(0), RELATIVE(1), NO(2);
		
		private final int order;

		private Mandatory(int order) {
			this.order = order;
		}
		
		public int getOrder() {
			return order;
		}
	}
	
	Comparator<Mandatory> MANDATORY_COMPARATOR = new Comparator<Mandatory>() {
		@Override
		public int compare(Mandatory o1, Mandatory o2) {
			return o1.getOrder() - o2.getOrder();
		}
	};
	
	Mandatory getMandatory();
	boolean matches(String argument);
	boolean isHandled();
	boolean isMultiple();
	boolean isBroadMatch();
	void handleArgument(String matchedArgument, Tail tail);
	int getNumber();
	
	String getName();
	String getShortName();
	String getLongName();
	String getDescription();
}
