package org.rcl.theor.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.rcl.theor.interval.Interval;
import org.rcl.theor.interval.IntervalVector;
import org.rcl.theor.note.NSequence;
import org.rcl.theor.note.PitchClassSets;

public class PCS {
	@Test
	public void test() {
		// Sample case that generates an error: base -- [8, 11, 10, 1, 0]
		// Correct prime form: (0,1,2,3,5)
		// Correct IV: <332110>
		
		NSequence ns = new NSequence(Arrays.asList(new Integer[] {0, 1, 3, 6, 8, 9, 10}));
		List<Integer> of = ns.getNormalOrder();
		
		List<Integer> pf = PitchClassSets.primeForm(of);
		
		assertTrue("Prime form " + PitchClassSets.toString(pf) + " matches", PitchClassSets.equals(pf, 
					    									   Arrays.asList(new Integer[] {0, 2, 3, 4, 6, 7, 9})));
		
		assertTrue("Interval vector is good", IntervalVector.fromPitchClassSet(ns).equals(new IntervalVector(3,4,5,3,4,2)));
		
		System.out.println(ns);
		System.out.println("Natural ordering " + ns.getNaturalOrder());
		System.out.println("Normal ordering " + ns.getNormalOrder());
		System.out.println("Inverse " + ns.inverse());
		System.out.println("Transposed +3 " + ns.transpose(new Interval(3)));
	}
}
