package org.rcl.theor.note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.jfugue.Pattern;
import org.rcl.theor.Syncopation;
import org.rcl.theor.midi.Patternable;

public class NSequence extends ArrayList<Note> implements NoteCollection, Patternable {
	private static final long serialVersionUID = 619198894201315898L;

	public enum StringType { NoteName, Frequency, MIDI };

	public NSequence() { super(); } 
	
	public NSequence(Note[] notes) { 
		for(Note n : notes) add(n); 
	}
	
	public NSequence(NoteCollection other) { 
		for(Note n : other.getNotes()) add(n); 
	}
	 
	public String toString() { return toString(StringType.NoteName); } 
	
	/** Returns a sorted version of this sequence.  Does not modify this sequence. */
	public NSequence sort() { 
		NSequence n = new NSequence(this);
		Collections.sort(n, Note.cmp); 
		return n;
	}
	
	public boolean add(Note n) { return super.add(n); }
	
	public NSequence shift(int positions) {
		NSequence copy = new NSequence(this);
		Collections.rotate(copy, positions);
		return copy;
	}
	
	public HashSet<Integer> getToneClasses() { 
		HashSet<Integer> tc = new HashSet<Integer>();
		for(Note n : this) tc.add(n.getToneClass()); 
		return tc;
	}
	
	/** Check to see if this contains a given note; if octaveSensitive is true, it will require that the note be in a given octave. */
	public boolean contains(Note n, boolean octaveSensitive) { 
		for(Note o : this) { 
			if(o.equals(n) || (o.getToneClass() == n.getToneClass() && !octaveSensitive)) return true;
		}
		
		return false;
	}	
	
	public boolean equals(List<Note>other) { return equals(other, true); } 
	
	public boolean equals(List <Note> other, boolean octaveSensitive) { 
		if(other == null) return false;
		if(other.size() != size()) return false;
		
		for(int x=0; x<size(); x++) {			
			if(octaveSensitive && !other.get(x).equals(get(x))) return false;
			else if(other.get(x).getToneClass() != get(x).getToneClass()) return false;
		}
		
		return true; 
	} // End equals
	
	public String toString(StringType t) {
		StringBuffer b = new StringBuffer(""); 
		for(Note n : this) {
			switch(t) { 
			case NoteName: b.append(n); break;
			case MIDI: b.append(""+n.getMIDI()); break;
			case Frequency: b.append(n.getFrequency()); break;
			default: b.append(n); break;
			}
						
			b.append(" "); 
		}
		return b.toString();
	}
	
	public NSequence reverse() {
		NSequence copy = new NSequence(this);
		Collections.reverse(copy);
		return copy; 
	}  
	
	public Pattern toPattern(Syncopation sync) { 
		Pattern p = new Pattern();				 
		for(Note n : this) p.addElement(new org.jfugue.Note(n.getMIDI(), sync.next()));
		return p;
	}
		
	public Collection<Note> getNotes() { return this; }
	public int countNotes() { return size(); } 
}
