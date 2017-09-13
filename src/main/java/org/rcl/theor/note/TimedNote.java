package org.rcl.theor.note;

/**
 * A timed note is just a regular note, that also has a floating point measurement for how long the note should ring.
 * This class does not impose an interpretation on the timing; it may be dependent on the library or technology used to 
 * render the note.   So for example, in some libraries, a timing of 0.25 represents a quarter note.  In other libraries, 
 * a timing of 1 might mean to let the note ring for 1 second.  Be careful about the distinction, depending on how you 
 * intend to use the class. 
 * 
 * @author moxious
 */
public class TimedNote extends Note {
	protected float timing = (float) 0.25;

	public TimedNote(int toneCl, int octave, float timing) {
		super(toneCl, octave, true);
		this.timing = timing;
	}

	public float getTiming() {
		return timing;
	}

	public void setTiming(float t) {
		this.timing = t;
	}

	public String toString() {
		return super.toString() + "/" + getTiming();
	}
}
