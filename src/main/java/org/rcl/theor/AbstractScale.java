package org.rcl.theor;

public abstract class AbstractScale {
	public String name = null;
	public IntervalRing intervals;

	protected void setName(String name) { this.name = name; }
	protected void setIntervals(Interval [] ivs) { this.intervals = new IntervalRing(ivs); }  
	
	public IntervalRing getIntervals() { return intervals; } 
	public String getName() { return name; } 
} // End AbstractScale
