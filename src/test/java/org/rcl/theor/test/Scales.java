package org.rcl.theor.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.rcl.theor.note.NSequence;
import org.rcl.theor.note.Note;
import org.rcl.theor.scale.Scale;

public class Scales {
	@Test
	public void testScales() {
		NSequence ns = Scale.MAJOR.apply(Note.MIDDLE_C);
		
		Note[] expectedNotes = new Note[] {
			new Note(Note.C), new Note(Note.D),
			new Note(Note.E), new Note(Note.F),
			new Note(Note.G), new Note(Note.A),
			new Note(Note.B)
		};
		
		for(int x=0; x<expectedNotes.length; x++) 
			assertTrue("C major contains expected note " + expectedNotes[x], ns.contains(expectedNotes[x], false));				
	}
}
