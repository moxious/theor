package org.rcl.theor.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.rcl.theor.note.Note;

public class Notes {
	@Test
	public void testNotes() {
		Note c = Note.MIDDLE_C;
		
		assertTrue("C's relative minor is A", c.getRelativeMinor().getPitchClass() == Note.A);
		assertTrue("C's MIDI number is 60", c.getMIDI() == 60);					
		assertTrue("C's relative major is Eb", c.getRelativeMajor().getPitchClass() == Note.EF);		
		assertTrue("C's dominant is G", c.getDominant().getPitchClass() == Note.G);
		assertTrue("C's subdominant is F", c.getSubdominant().getPitchClass() == Note.F);
		
		double freq = c.getFrequency();
		// Due to double math, it's hard to ask for the value to be exact.
		assertTrue("C's frequency is really close to reference value 261.626", freq >= 261.625 && freq <= 261.627);
		
		assertTrue("C is natural", c.isNatural());
		assertTrue("C is not sharp", !c.isSharp());
		
	}
}
