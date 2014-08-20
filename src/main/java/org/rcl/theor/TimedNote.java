package org.rcl.theor;

public class TimedNote extends Note {
	protected float timing = (float)0.25;
	
	public TimedNote(int toneCl, int octave, float timing) {
		super(toneCl, octave, true);
		this.timing = timing;
	}

	public float getTiming() { return timing; } 
	public void setTiming(float t) { this.timing = t; } 
	
	public String toString() { 
		return super.toString() + "/" + getTiming();
	}
}
