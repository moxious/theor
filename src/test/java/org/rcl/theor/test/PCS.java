package org.rcl.theor.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.rcl.theor.interval.IntervalVector;
import org.rcl.theor.note.NSequence;
import org.rcl.theor.note.Note;
import org.rcl.theor.note.PitchClassSet;
import org.rcl.theor.note.PitchClassSets;

public class PCS {
	public class TestCase {
		public PitchClassSet pcs = null;
		public List<Integer> expectedPrime = null;
		public IntervalVector expectedVector = null;
		
		public TestCase(PitchClassSet p, List<Integer>e, IntervalVector v) {
			pcs = p; expectedPrime = e; expectedVector = v;
		}
	}
	
	@Test
	public void randomPCSTest() { 
		Random r = new Random();
		
		for(int i=0; i<1000; i++) { 
			NSequence ns = new NSequence();
			
			for(int x=0; x<r.nextInt(12)+5; x++) { 
				ns.add(new Note(r.nextInt(12), r.nextInt(3)));
			}
			
			List<Integer> no = ns.getNormalOrder();
			List<Integer> pf  = PitchClassSets.primeForm(ns);
			List<Integer> pf2 = PitchClassSets.invert(new ArrayList<Integer>(pf));
			List<Integer> doublePf = PitchClassSets.primeForm(pf);
			
			assertTrue("Prime form " + PitchClassSets.toString(pf) + " and its inverse " + 
			           PitchClassSets.toString(pf2) + " are equivalent", PitchClassSets.equivalent(pf, pf2));
			assertTrue("Prime form of " + PitchClassSets.toString(pf) + " (a prime form) is itself.", 
					   PitchClassSets.equivalent(pf, doublePf));
			
			// In a PC set of k items, you expect (k^2 - k) / 2 intervals.
			int k = no.size();			
			IntervalVector v = IntervalVector.fromPitchClassSet(ns);

			if(k == 1)
				assertTrue("PCS cardinals of 1 have boring IVs " + v + " == " + "[0 0 0 0 0 0]",
						v.equals(new IntervalVector(0, 0, 0, 0, 0, 0)));
			else if(k == 11)
				assertTrue("PCS cardinals of 11 have boring IVs " + v + " == " + "[10 10 10 10 10 10 5]",
						v.equals(new IntervalVector(10, 10, 10, 10, 10, 5)));
			else if(k == 12)
				assertTrue("PCS cardinals of 12 have boring IVs " + v + " == " + "[12 12 12 12 12 6]",
						v.equals(new IntervalVector(12, 12, 12, 12, 12, 6)));
			
			// A few odd properties from the forte book.
			if(k == 7) 
				assertTrue("Forte says that all 7 element PCSs have all intervals represented " + PitchClassSets.toString(no) + 
						" IV=" + v,
						v.ic1 > 0 && 
						v.ic2 > 0 && 
						v.ic3 > 0 &&
						v.ic4 > 0 &&
						v.ic5 > 0 &&
						v.ic6 > 0);			
			else if(k == 5) 
				assertTrue("Forte says all 5 element sets have at least one ic4 " + PitchClassSets.toString(no) + " IV=" + v,
						v.ic4 > 0);
			
			if(k == 5 || k == 6) 
				assertTrue("Forte says all 5 and 6 element PC sets have a maximum of 3 empty interval sets " + 
						   PitchClassSets.toString(no) + " IV=" + v,
						   v.countEmptyIntervalSets() <= 3);
		} // End for		
	}
	
	@Test
	public void test() {
		// Sample case that generates an error: base -- [8, 11, 10, 1, 0]
		// Correct prime form: (0,1,2,3,5)
		// Correct IV: <332110>
		
		List<TestCase> cases = Arrays.asList(new TestCase[] {
				// This first case is when the inversion of the prime form is better than the original.
				new TestCase(new NSequence(Arrays.asList(new Integer[] {8, 11, 10, 1, 0})),
						     Arrays.asList(new Integer[] {0, 1, 2, 3, 5}), 
						     new IntervalVector(3, 3, 2, 1, 1, 0)),
						     
			    // In this case, the first form is better.
		        new TestCase(new NSequence(Arrays.asList(new Integer[] {0, 2, 3, 4, 6, 7, 9})),
		        		     Arrays.asList(new Integer[] { 0, 2, 3, 4, 6, 7, 9 }),
		        		     new IntervalVector(3, 4, 5, 3, 4, 2))						     
		});
				
		for(TestCase tc : cases) { 
			List<Integer> no = tc.pcs.getNormalOrder();
			List<Integer> pf = PitchClassSets.primeForm(no); 
			IntervalVector v = IntervalVector.fromPitchClassSet(no);
			IntervalVector v2 = IntervalVector.fromPitchClassSet(pf); 
			
			assertTrue("Prime form of " + PitchClassSets.toString(no) + " is " +
			           PitchClassSets.toString(tc.expectedPrime),
			           PitchClassSets.equals(pf, tc.expectedPrime));
			assertTrue("Interval vector of " + PitchClassSets.toString(no) + " is " + tc.expectedVector,
					   tc.expectedVector.equals(v));
			assertTrue("Vectors " + v + " and " + v2 + " are the same.",
					   v.equals(v2));
		}
	}
}
