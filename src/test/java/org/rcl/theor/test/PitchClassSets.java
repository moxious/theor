package org.rcl.theor.test;

import org.junit.Test;
import org.rcl.theor.interval.Interval;
import org.rcl.theor.note.NSequence;
import org.rcl.theor.note.Note;

public class PitchClassSets {
	@Test
	public void test() {
		NSequence ns = new NSequence();
		ns.add(new Note(Note.G));
		ns.add(new Note(Note.F));
		ns.add(new Note(Note.B));
		
		System.out.println(ns);
		System.out.println("Natural ordering " + ns.getNaturalOrder());
		System.out.println("Normal ordering " + ns.getNormalOrder());
		System.out.println("Inverse " + ns.inverse());
		System.out.println("Transposed +3 " + ns.transpose(new Interval(3)));
		System.out.println("Prime form " + ns.primeForm().getNormalOrder() + " (" + ns.primeForm() + ")");		
	}
}
