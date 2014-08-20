package org.rcl.theor;

import java.util.Collection;

public interface NoteCollection {
	public Collection<Note> getNotes();
	public int countNotes();
	public boolean add(Note n); 
}
