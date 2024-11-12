package org.emmef.config.program;

public interface Program {
	public void run(String[] arguments) throws Exception;
	public String getSynopsis();
}
