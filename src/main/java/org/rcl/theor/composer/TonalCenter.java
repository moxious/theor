package org.rcl.theor.composer;

import org.rcl.theor.note.Note;

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
