package org.rcl.theor.note;

import java.util.List;
import java.util.Set;

import org.rcl.theor.interval.Interval;

/**
 * A pitch class set, as defined by Allen Forte's "The Structural of Atonal Music" (1970)
 * @author moxious
 */
public interface PitchClassSet {
	/** @return a set describing the unique pitch classes in the set */
	public Set<Integer> getPitchClasses();

	/** @return the normal order of the set (sorted ascending pitch classes), e.g. [1, 3, 5] */
	public List<Integer> getNormalOrder();

	/** @return the natural order of the set, which may differ from normal order  E.g. [3, 5, 1] */
	public List<Integer> getNaturalOrder();

	/** @return true if this set is equivalent to another, false otherwise */
	public boolean equivalent(PitchClassSet other);

	/** @return a new PitchClassSet transposed by the given interval */
	public PitchClassSet transpose(Interval i);

	/** Return the PitchClassSet that is the inverse of this one.
	 * The inverse will always be in normal order, not natural order.
	 * The inverse of any pitch class (a') is a' = 12 - a (mod 12)
	 * @return the inverse of the PitchClassSet
	 */
	public PitchClassSet inverse();
}
