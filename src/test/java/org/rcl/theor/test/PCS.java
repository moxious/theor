package org.rcl.theor.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.rcl.theor.interval.IntervalVector;
import org.rcl.theor.note.NSequence;
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
