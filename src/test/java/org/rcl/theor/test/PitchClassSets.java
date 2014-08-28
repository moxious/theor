package org.rcl.theor.test;

import org.junit.Test;
import org.rcl.theor.interval.Interval;
import org.rcl.theor.note.NSequence;
import org.rcl.theor.note.Note;

public class PitchClassSets {
	@Test
	public void test() {
		NSequence ns = new NSequence();
		ns.add(new Note(Note.C)); ns.add(new Note(Note.CS));
		ns.add(new Note(Note.D)); ns.add(new Note(Note.DS));
		ns.add(new Note(Note.E)); 
		
		System.out.println(ns);
		System.out.println("Natural ordering " + ns.getNaturalOrder());
		System.out.println("Normal ordering " + ns.getNormalOrder());
		System.out.println("Inverse " + ns.inverse());
		System.out.println("Transposed +3 " + ns.transpose(new Interval(3)));
	}
}
