package org.rcl.theor.test;

import static org.junit.Assert.*;

import java.util.List;

import org.jfugue.Pattern;
import org.jfugue.Player;
import org.jfugue.Tempo;
import org.junit.Test;
import org.rcl.theor.Syncopation;
import org.rcl.theor.TheorException;
import org.rcl.theor.chord.Chord;
import org.rcl.theor.chord.ChordProgression;
import org.rcl.theor.interval.Interval;
import org.rcl.theor.note.NSequence;
import org.rcl.theor.note.Note;
import org.rcl.theor.scale.Scale;

public class Demo {
	@Test
	public void demo() throws TheorException { 		
		Note a = new Note(Note.A);
		System.out.println(a);
		System.out.println(a.getFrequency());
		System.out.println(a.isSharp());
		
		
		// Make a C major chord.   This is done by applying a set of intervals (the major triad) to a note.
		Chord c = new Chord(Note.MIDDLE_C, Interval.MAJOR_TRIAD);
		Chord cm = new Chord(Note.MIDDLE_C, Interval.MINOR_TRIAD);
		
		// Make a D major scale, starting at octave 0.
		NSequence ns = Scale.MAJOR.apply(new Note(Note.D, 0));		
		
		// Create a blank MIDI pattern from JFugue.
		Pattern container = new Pattern();
		container.addElement(new Tempo(80));   // Set its tempo to 80bpm.
				
		// Make a simple I, IV, V chord progression, starting at F major.
		List<Chord> chords = ChordProgression.MI_IV_V.apply(new Note(Note.F, 0));
		for(Chord ch : chords) { 
			System.out.println("Progression:  " + ch + " => " + ch.getName());
			
			// Add this chord (played as a quarter note) to the main pattern.
			container.add(ch.toPattern(new Syncopation(Syncopation.QUARTER_NOTE)));
		}
		
		Player player = new Player();
		// player.play(container);
		
		assertTrue("C major is the same as the major version of itself.", c.equals(c.makeMajor(), true, true));
		assertTrue("C minor is the same as the minor version of C major.", cm.equals(c.makeMinor(), true, true));
		
	}
}
