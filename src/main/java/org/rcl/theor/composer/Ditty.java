package org.rcl.theor.composer;

import java.util.Properties;

import org.jfugue.Pattern;

/**
 * A ditty is a very small song that the composer composes, containing a JFugue pattern.
 * @author moxious
 */
public class Ditty {
	/** The JFugue pattern suitable for rendering or saving to MIDI */
	protected Pattern pat;
	
	/** Properties describing how the ditty was generated/what it contains */
	protected Properties props;
	
	public Ditty(Pattern pat) { 
		this.pat = pat;
		props = new Properties();
	}
	
	public Pattern getPattern() { return pat; } 
	public Properties getProperties() { return props; } 
	public void setProperties(Properties p) { props = p; } 
}
