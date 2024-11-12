package org.emmef.config.options;

public interface SwitchBuilder extends ValueBuilder {
	SwitchBuilder optional(String switch1, String... switches);
	SwitchBuilder mandatory(String switch1, String... switches);
	String getDescription();
	SwitchBuilder describedBy(String description);
	boolean present();
}
