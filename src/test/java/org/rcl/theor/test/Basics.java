package org.rcl.theor.test;

import org.jfugue.Pattern;
import org.jfugue.Player;
import org.junit.Test;
import org.rcl.theor.Syncopation;
import org.rcl.theor.note.NSequence;
import org.rcl.theor.note.Note;
import org.rcl.theor.scale.Scale;

public class Basics {
	@Test
	public void test() throws Exception { 
		Player p = new Player();
		
		NSequence seq = Scale.MINOR_PENTATONIC.apply(new Note(Note.C, 0));
		
		Pattern pat1 = seq.toPattern(new Syncopation(Syncopation.QUARTER_NOTE));
		Pattern pat2 = seq.reverse().toPattern(new Syncopation(Syncopation.QUARTER_NOTE));
		
		System.out.println("Music string: " + pat1.getMusicString()); 
		System.out.println("Music string: " + pat2.getMusicString());
		
		Pattern pat = new Pattern();
		pat.add(pat1);
		pat.add(pat2);
		
		p.play(pat);
		
		System.out.println("Done");
	} // End main
} // End class Test
