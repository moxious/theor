package org.rcl.theor.note;

import java.util.List;
import java.util.Set;

/**
 * A pitch class set, as defined by Allen Forte's "The Structural of Atonal Music" (1970)
 * @author moxious
 */
public interface PitchClassSet {
	public Set<Integer> getPitchClasses(); 
	public List<Integer> getNormalOrder();
	public List<Integer> getNaturalOrder(); 
	public boolean equivalent(PitchClassSet other);
}
