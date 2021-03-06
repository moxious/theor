package org.rcl.theor.scale;

import org.rcl.theor.interval.Interval;
import org.rcl.theor.interval.IntervalRing;

/**
 * Base class for all scales.  What they all have in common is a name, an an interval ring.
 * 
 * @author moxious
 *
 */
public abstract class AbstractScale {
	public String name = null;
	public IntervalRing intervals;

	protected void setName(String name) {
		this.name = name;
	}

	protected void setIntervals(Interval[] ivs) {
		this.intervals = new IntervalRing(ivs);
	}

	public IntervalRing getIntervals() {
		return intervals;
	}

	public String getName() {
		return name;
	}
} // End AbstractScale
