package org.rcl.theor.chord;

import java.util.ArrayList;
import java.util.regex.Matcher;

import org.rcl.theor.TheorException;
import org.rcl.theor.interval.Interval;

/**
 * A ChordTemplate is a way of making chords from a template.  Template tokens are text ways of representing chords relative
 * to a tonic.  So a token would be "I" or "IVM7".   Relative to C, the token I would be C major.  The token IVM7 would be F maj 7.
 * @author moxious
 *
 */
public class ChordTemplate {
	protected static final java.util.regex.Pattern romanNumeralPattern = java.util.regex.Pattern.compile("([iIvV]+)");
	protected String token = null;
	protected Interval [] intervals = null;
	protected Interval distanceFromTonic = null;
	protected boolean major;
	
	public ChordTemplate(String token, boolean major) throws TheorException { 
		this.token = token;
		this.major = major;
		intervals = getChordIntervalsByToken(token);
		distanceFromTonic = getDistanceFromTonic();
	}
	
	public Interval[] getIntervals() { return intervals; }  
	
	public Interval getDistanceFromTonic() throws TheorException { 
		String t = token.trim().toLowerCase();
		
		if(t.contains("vii")) return Interval.SEVENTH;
		if(t.contains("vi")) return Interval.SIXTH;
		if(t.contains("iv")) return Interval.FOURTH;
		if(t.contains("v")) return Interval.FIFTH;
		if(t.contains("iii")) return Interval.THIRD;
		if(t.contains("ii")) return Interval.SECOND;
		if(t.contains("i")) return Interval.UNISON;
		
		throw new TheorException("Invalid token '" + token + "'"); 
	} // End getDistanceFromTonic
	
	protected String getRomanNumeral(String tok) { 		
		Matcher m = romanNumeralPattern.matcher(tok);
		if(m.find()) return m.group(1);
		return null;
	}
	
	/**
	 * Given a token and a key (major or minor) return a list of intervals corresponding to that chord.
	 * @param tok a chord progression token, e.g. I, IV, V7, viidim, IVM7, etc.  
	 * @param major true if a major chord progression, false if a minor chord progression.
	 * @return a set of intervals.
	 * @throws TheorException
	 */
	protected Interval[] getChordIntervalsByToken(String tok) throws TheorException {
		if(tok == null || "".equals(tok)) { throw new TheorException("Illegal null or blank token"); }  
		
		boolean diminished = tok.endsWith("dim") || tok.endsWith("o");
		boolean majSeventh = tok.contains("M7");
		boolean minSeventh = tok.contains("7") && !majSeventh;
		boolean fifth = tok.contains("5"); 
		boolean augmented = tok.contains("+");

		String numeral = getRomanNumeral(tok);
		if(numeral == null) throw new TheorException("Cannot find roman numeral in token " + tok); 
		boolean major = numeral.toUpperCase().equals(numeral);
		boolean minor = numeral.toLowerCase().equals(numeral);
		
		if(!major && !minor) throw new TheorException("Mixed case illegal chord token '" + tok + "': can't be both minor and major"); 
		
		if(!diminished && !majSeventh && !minSeventh && !augmented && !major && !minor) 
			throw new TheorException("Illegal interval token '" + tok + "'"); 					

		if(fifth && minSeventh) return Interval.POWER_SEVENTH;
		if(fifth) return Interval.POWER_CHORD;
		if(augmented && majSeventh && major) return Interval.AUGMENTED_MAJOR_SEVENTH_TETRAD;
		if(augmented && minSeventh) return Interval.AUGMENTED_SEVENTH_TETRAD;
		if(major && majSeventh) return Interval.MAJOR_SEVENTH_TETRAD;
		if(minSeventh && major) return Interval.DOMINANT_SEVENTH_TETRAD;
		if(minor && minSeventh) return Interval.MINOR_SEVENTH_TETRAD;
		if(diminished && !minSeventh && !majSeventh) return Interval.DIMINISHED_TRIAD;
		if(major && !minSeventh && !majSeventh) return Interval.MAJOR_TRIAD;
		if(minor && !minSeventh && !minSeventh) return Interval.MINOR_TRIAD;
		if(augmented && major) return Interval.AUGMENTED_TRIAD;
		
		throw new TheorException("Unrecognized interval token '" + tok + "'"); 		
	}
	
	/**
	 * Parse a string containing a series of chord tokens (separated by spaces or dashes) into a series of chord templates.
	 * Example input strings are things like "I - IV - V"  (the one four five chord progression).
	 * @param toks
	 * @param major
	 * @return
	 * @throws TheorException
	 */
	public static ChordTemplate[] parse(String toks, boolean major) throws TheorException { 
		String [] pieces = toks.split("[\\s-]+");
		
		ArrayList<ChordTemplate> l = new ArrayList<ChordTemplate>();
		
		for(String p : pieces) {
			if("".equals(p.trim()) || "-".equals(p.trim())) continue;
			l.add(new ChordTemplate(p, major));
		}
		
		return l.toArray(new ChordTemplate[]{});
	} // End parse
}
