package org.rcl.theor;

import java.util.Properties;

import org.jfugue.Pattern;

public class Ditty {
	protected Pattern pat;
	protected Properties props;
	
	public Ditty(Pattern pat) { 
		this.pat = pat;
		props = new Properties();
	}
	
	public Pattern getPattern() { return pat; } 
	public Properties getProperties() { return props; } 
	public void setProperties(Properties p) { props = p; } 
}
