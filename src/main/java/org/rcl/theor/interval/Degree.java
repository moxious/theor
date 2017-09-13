package org.rcl.theor.interval;

/**
 * Degrees are just relative names for various intervals; sometimes it's more convenient to work in this terminology.
 * @author moxious
 */
public class Degree {
	/** An interval a unison away */
	public static final Interval TONIC = Interval.UNISON;
	/** An interval a second away */
	public static final Interval SUPERTONIC = Interval.SECOND;
	/** An interval a third away */
	public static final Interval MEDIANT = Interval.THIRD;
	/** An interval a fourth away */
	public static final Interval SUBDOMINANT = Interval.FOURTH;
	/** An interval a fifth away */
	public static final Interval DOMINANT = Interval.FIFTH;
	/** An interval a sixth away */
	public static final Interval SUBMEDIANT = Interval.SIXTH;
	/** An interval a seventh away */
	public static final Interval SUBTONIC = Interval.MINOR_SEVENTH;
	/** An interval a seventh away */
	public static final Interval LEADING = Interval.SEVENTH;
}
