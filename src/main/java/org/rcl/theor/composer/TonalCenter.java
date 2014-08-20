package org.rcl.theor.composer;

import org.rcl.theor.note.Note;

/**
 * A tonal center is a general location where a composition is trying to hang out (at least for a time).
 * For short ditties, the composer will pick a tonal center and work with that.   This somewhat conflates the idea
 * of tonal center and key in music theory, but I'm OK with that.  :)
 * @author moxious
 *
 */
public class TonalCenter {
	public int noteConstant;
	public boolean major;
	
	public TonalCenter() { 
		noteConstant = Note.C;
		major = true;
	}
	
	public TonalCenter(int noteConstant, boolean major) { 
		this.noteConstant = noteConstant;
		this.major = major;
	}
	
	public String toString() { 
		return Note.name(noteConstant) + (major ? "major" : "minor"); 
	}
}
