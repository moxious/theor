package org.rcl.theor;

public class TheorException extends Exception {
	private static final long serialVersionUID = 1L;
	public TheorException() { super(); } 
	public TheorException(String msg) { super(msg); } 
	public TheorException(String msg, Throwable t) { super(msg, t); } 
}
