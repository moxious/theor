package org.rcl.theor.note;

import java.util.List;
import java.util.Set;

import org.rcl.theor.interval.Interval;

/**
 * A pitch class set, as defined by Allen Forte's "The Structural of Atonal Music" (1970)
 * @author moxious
 */
public interface PitchClassSet {
	/** Get a set describing the unique pitch classes in the set */
	public Set<Integer> getPitchClasses(); 
	
	/** Get the normal order of the set (sorted ascending pitch classes), e.g. [1, 3, 5] */
	public List<Integer> getNormalOrder();
	
	/** Get the natural order of the set, which may differ from normal order  E.g. [3, 5, 1] */
	public List<Integer> getNaturalOrder(); 
	
	/** Return true if this set is equivalent to another, false otherwise */
	public boolean equivalent(PitchClassSet other);
	
	/** Return a new PitchClassSet transposed by the given interval */
	public PitchClassSet transpose(Interval i); 
}
