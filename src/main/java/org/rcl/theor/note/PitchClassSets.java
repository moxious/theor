package org.rcl.theor.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.rcl.theor.interval.Interval;
import org.rcl.theor.interval.IntervalVector;

/**
 * A set of methods that implement pitch class set relationships and operations.  This is generally done abstractly over
 * lists of Integers.  Other types exist which implement the PitchClassSet interface, which use these methods to do some of 
 * their work.
 * 
 * <p>Most types dealt with here are simple List<Integer>.  This strips away some of the music context (chord, sequence, whatever).
 * If you take a chord and try to compute its prime form, you will get a certain PCS.  Re-interpreting that PCS as a chord or a 
 * sequence is up to the developer.
 * 
 * @author moxious
 */
public class PitchClassSets {
	public static List<Integer> normalForm(List<Integer> pcs) {
		List<Integer> normalOrder = new ArrayList<Integer>(pcs);		
		System.out.println("BASE: " + toString(normalOrder));
		
		List<?>[] rotations = enumerateRotations(normalOrder);
		// for(List<?> l : rotations) { System.out.println("ENUMERATION: " + toString(l)); }
		
		List<Integer> normalForm = chooseMostPackedForm(rotations);
		System.out.println("NORMAL " + toString(normalForm));

		return normalForm;
	}
	
	/** Convenience function that calls transpose with pcs's natural order */
	public static List<Integer> transpose(PitchClassSet pcs, Interval i) { return transpose(pcs.getNaturalOrder(),i.intValue()); }
	
	/** Convenience function that calls normalForm with pcs's natural order */
	public static List<Integer> normalForm(PitchClassSet pcs) { return normalForm(pcs.getNaturalOrder()); }
	
	/** Convenience function that calls primeForm with pcs's natural order */
	public static List<Integer> primeForm(PitchClassSet pcs) { return primeForm(pcs.getNaturalOrder()); }
	
	/**
	 * This implementation takes the "rigorous method" identified by this:
	 * http://composertools.com/Theory/PCSets/PCSets3.htm 
	 * The outline of the approach is to start with the normal order of the PCS.  Then enumerate all rotations of that set.
	 * Then determine which rotation has the minimum distance between first and last elements of the set.  
	 * The PCS is then transposed so its first element is zero.
	 * @param set
	 * @return
	 */
	public static List<Integer> primeForm(List<Integer> pcs) { 
		List<Integer> normalForm = normalForm(pcs);
		
		// This is how much we need to transpose up, in order to get the first item to zero.
		int transpositionInterval = 12 - normalForm.get(0);
		
		List<Integer> candidate = new ArrayList<Integer>(normalForm);		
		transpose(candidate, transpositionInterval);
		
		// OK, at this point we have a candidate.  But we also have to check its inversion...
		List<Integer> candidateInversion = new ArrayList<Integer>(candidate); 
		
		invert(candidateInversion);
		candidateInversion = normalForm(candidateInversion);
		int transpositionalInterval = 12 - candidateInversion.get(0);
		transpose(candidateInversion, transpositionalInterval);
		
		// Now we have a candidate, and its inversion.  Whichever is more packed to the left
		// is the prime form.
		List<Integer> primeForm = chooseMostPackedForm(new List[] { candidate, candidateInversion });
		
		System.out.println("PRIME_FORM(" + toString(pcs) + "): normal " + normalForm + 
				" => transposed " + candidate + 
				" => inverted/transposed " + candidateInversion + 
				" => prime " + primeForm); 
		
		return primeForm;
	}
	
	/**
	 * @param pcs1 a pitch class set
	 * @param pcs2 a pitch class set
	 * @return true if pcs2 is a subset of pcs1, false otherwise.
	 */
	public static boolean isSubset(List<Integer> pcs1, List<Integer>pcs2) {
		validate(pcs1);
		validate(pcs2);
		
		for(Integer i : pcs2)
			if(!pcs1.contains(i)) return false;
		
		return true;
	}
	
	/**
	 * Transpose the given pitch class set by a given interval.  This function modifies its argument.
	 * @param pcs
	 * @param interval
	 * @return a transposed set.
	 */
	public static List<Integer> transpose(List<Integer> pcs, Integer interval) {	
		validate(pcs); 
		
		for(int x=0; x<pcs.size(); x++) 
			pcs.set(x, Math.abs((pcs.get(x) + interval) % 12));
		
		return pcs;
	}
	
	/**
	 * Invert the provided PCS.  This function modifies its argument.
	 * @param pcs the pcs.
	 */
	public static List<Integer> invert(List<Integer> pcs) { 
		validate(pcs);
		
		for(int x=0; x<pcs.size(); x++) { 
			pcs.set(x, (12 - pcs.get(x)) % 12);
		}
		
		return pcs;
	}

	/**
	 * This function helps validate other function inputs.  If the PCS provided is null or empty, it 
	 * throws a runtime exception.
	 * @param pcs
	 * @throws RuntimeException
	 */
	protected static void validate(List<Integer> pcs) { 
		if(pcs == null) throw new RuntimeException("Invalid pcs: null"); 
		if(pcs.size() == 0) throw new RuntimeException("Invalid pcs: empty"); 
	}
	
	public static void main(String [] args) throws Exception { 
		NSequence ns = new NSequence();
		
		Random r = new Random();
		
		for(int x=0; x<r.nextInt(12)+5; x++) { 
			ns.add(new Note(r.nextInt(12), r.nextInt(3)));
		}

		System.out.println("Starting random sequence: " + ns); 
		System.out.println("=====> Prime form " + primeForm(ns.getNaturalOrder()));
		
		System.out.println("Starting NS again " + ns);
		System.out.println(toString(ns.getNormalOrder()));
		System.out.println("IV: " + IntervalVector.fromPitchClassSet(ns));
	}
	
	/**
	 * Determine which rotation has the minimum distance between first and last items.  If it's a tie, compare the first and second 
	 * items.  If that's a tie, compare first and third, and so on until the tie is resolved.
	 * @param enumerations
	 * @return the rotation with minimum distance between first and last items.
	 */	
	@SuppressWarnings("unchecked")
	protected static List<Integer> chooseMostPackedForm(List<?>[] enumerations) {
		if(enumerations == null || enumerations.length == 0) throw new RuntimeException("Invalid enumerations, empty or null");
		
		// Validate all of the enumerated options to prevent bad inputs.
		int expectedLength = enumerations[0].size();
		for(int x=0; x<enumerations.length; x++) {
			validate((List<Integer>)enumerations[x]);
			if(enumerations[x].size() != expectedLength) throw new RuntimeException("All options must be the same length!"); 
		}
		
		// If there's only one option, it's definitely the most packed.
		if(enumerations.length == 1) return (List<Integer>)enumerations[0];
		
		// If there are two options that are equal, then either will do.
		else if(enumerations.length == 2 && equals((List<Integer>)enumerations[0], (List<Integer>)enumerations[1])) 
			return (List<Integer>)enumerations[0];
		
		// We'll start by comparing first and last items.
		int x=0, y=enumerations[0].size()-1;
					
		// System.out.println("BEST BETWEEN: " + toString(enumerations[0]) + " and " + toString(enumerations[1])); 
		while(true) {
			// First item is the index of the best difference so far, second item
			// is what that difference is.  Third item is number of times seen.
			// Initialize with values so that get wiped out on first iteration.
			int [] bestDifferenceSoFar = new int[] { -1, Integer.MAX_VALUE, 0 };
			
			for(int z=0; z<enumerations.length; z++) {
				List<?> l = enumerations[z];
				
				// Get the difference
				int diff = (Integer)l.get(y) - (Integer)l.get(x);
				
				if(diff < bestDifferenceSoFar[1]) {
					// Update with best difference so far.
					bestDifferenceSoFar[0] = z;    // it's at index z
					bestDifferenceSoFar[1] = diff; // this is how much of a difference there is.
					bestDifferenceSoFar[2] = 1;    // we've seen this difference once.
				} else if(diff == bestDifferenceSoFar[1]) {
					// At this level, there was a "tie" -- two PCs have the same.
					// Increment number of times seen.  But we might still find a better difference,
					// so we have to keep going.
					bestDifferenceSoFar[2] = bestDifferenceSoFar[2] + 1;
				}
			}
		
			if(bestDifferenceSoFar[2] == 1) {
				// We found a best difference between the x and the y'th elements.  There's only one
				// PCS that has that, so we're done.
				return (List<Integer>)enumerations[bestDifferenceSoFar[0]];
			} 
			
			System.out.println("Iterating x=" + x + " y=" + y + " over " + enumerations.length + " options."); 
			
			// Otherwise, we need to keep checking, adjusting the y values.
			// The algorithm says compare first and last items first.  Then look at first and second,
			// first and third, and so on.
			if(y == enumerations[0].size() - 1) { 
				y = 1;  // Look at first and second (x=0, y=1)
				
				if(enumerations[0].size() == 2) { 
					// We can't repeat here - we already looked at first and last (x=0, y=1).  The two sets aren't different, return
					// the first.
					// System.err.println("2-item list showed no intervallic difference  " + toString(enumerations)); 
					return (List<Integer>)enumerations[0];
				}
			} else if(y == enumerations[0].size() - 2) {
				// Degenerate error case.  We've looked through all combinations.
				
				// enumerations.length == 2 means there are only two options.  Meaning we can only run one
				// iteration of the algorithm.
				
				// y == enumerations[0].size() - 2 means we're currently looking at the second to last item.  
				// Looking at the one after that would be futile, since the first algorithm run is looking at the first and last.
				
				// There's *still* a tie?
				// If we increment y now,
				// we'll be checking a case we already checked.  So throw an exception here instead of getting
				// stuck in an infinite loop.
				if(bestDifferenceSoFar[0] >= 0) {
					// A tie after this many rounds is possible.  So just pick the first item if we can't figure out a better option.
					// System.err.println("Warning, returning index " + bestDifferenceSoFar[0] + " despite continued tie after " + 
				    // (y+1) + " iterations looking through " + toString(enumerations));
					return (List<Integer>)enumerations[bestDifferenceSoFar[0]];
				} else
					// This should be impossible.
					throw new RuntimeException("Failed to find best PCS rotation after checking all possibilities");
			} else { 
				y++;   // Look at the first and the next yth.
			}
			
			System.out.println("NEXT:  x=" + x + " y=" + y + " over " + enumerations.length + " options."); 
		}		
	}

	/**
	 * Determine whether two pitch class sets are a Z-related pair. The definition of a z-related pair is two 
	 * pitch class sets that have identical interval vectors, but which are not transpositionally or 
	 * inversionally equivalent.
	 * @param pcs1
	 * @param pcs2
	 * @return
	 */
	protected static boolean zRelatedPair(List<Integer> pcs1, List<Integer>pcs2) {
		validate(pcs1);
		validate(pcs2);
		
		IntervalVector v1 = IntervalVector.fromPitchClassSet(pcs1);
		IntervalVector v2 = IntervalVector.fromPitchClassSet(pcs2);
		
		if(!v1.equals(v2)) return false;
		
		Interval i = findEquivalentTranspositionInterval(pcs1, pcs2);
		
		// If such an interval exists, then they're not a Z-related pair.
		if(i != null) 
			return false;
		
		boolean inversionallyEquivalent = equivalent(pcs1, invert(new ArrayList<Integer>(pcs2)));
		
		if(inversionallyEquivalent) 
			return false;
		
		return true;
	}
	
	protected static String toString(List<?>[] options) { 
		StringBuffer b = new StringBuffer("");
		
		for(List<?> l : options) { 
			b.append(toString(l));
			b.append(", ");
		}
		
		return b.toString();
	}
	
	/**
	 * Takes a PCS, and returns a list of List<Integer>, corresponding to all rotations of the PCS.
	 * The first is always the normal form.
	 * @param set
	 * @return a List[].   All lists are guaranteed to contain integers.
	 */
	protected static List<?>[] enumerateRotations(List<Integer> pcs) { 
		List<Integer> normalOrder = new ArrayList<Integer>(pcs);
		Collections.sort(normalOrder);
		validate(normalOrder); 
		
		List<Integer> latestRotation = new ArrayList<Integer>();
		
		int y = normalOrder.size();
		
		// A set of y items has a total of y rotations.
		List<?> [] rotations = new List[y] ;
		
		rotations[0] = normalOrder;
		latestRotation = new ArrayList<Integer>(normalOrder);
		
		if(y == 1) return rotations;
		
		for(int x=1; x<y; x++) { 			
			// Rotate it by 1.
			// System.out.print("Rotating " + toString(latestRotation) + " => ");
			Collections.rotate(latestRotation, -1);
			// System.out.print(toString(latestRotation) + " => ");
			// Add 12 to the last element.
			int lastItem = latestRotation.size() - 1;
			latestRotation.set(lastItem, latestRotation.get(lastItem) + 12);
			
			// System.out.println(toString(latestRotation));
			
			List<Integer> copy = new ArrayList<Integer>(latestRotation);			
			rotations[x] = copy;
		}

		return rotations;
	}
	
	/**
	 * Determine whether an interval exists between two PCSs that make them equivalent.  This will always return null
	 * if the sizes are different.
	 * @param a a PCS
	 * @param b a PCS
	 * @return an interval, which when applied, will transform one PCS to the other, or null, if no such interval exists.
	 */
	public static Interval findEquivalentTranspositionInterval(List<Integer> a, List<Integer> b) { 
		validate(a);
		validate(b);

		List<Integer> normalA = normalForm(a);
		List<Integer> normalB = normalForm(b);
		
		// If
		if(normalA.size() != normalB.size()) return null;
		
		// The interval between the first items.
		int interval = Math.abs(normalA.get(0) - normalB.get(0));
		
		// Check if it works for the remaining items.
		for(int x=1; x<normalA.size(); x++) { 
			int thisInterval = Math.abs(normalA.get(x) - normalB.get(x));
			
			// Can't transpose these, because you need a different interval here.
			if(thisInterval != interval) return null;
		}
		
		// The originally found interval works everywhere, so it's good.
		return new Interval(interval);
	}
	
	/**
	 * Determine whether the prime forms of two sets are equivalent or not.
	 * @param a a pcs
	 * @param b a pcs
	 * @return true if the prime form of a and b are equal, false otherwise.
	 */
	public static boolean equivalent(List<Integer> a, List<Integer> b) {
		validate(a);
		validate(b);
		
		List<Integer> primeA = primeForm(a);
		List<Integer> primeB = primeForm(b);
		
		if(primeA.size() != primeB.size()) return false;
		
		for(int x=0; x<primeA.size(); x++) { 
			if(primeA.get(x) != primeB.get(x)) return false;
		}
		
		return true;
	}
	
	public static boolean equals(List<Integer> a, List<Integer>b) { 
		validate(a);
		validate(b);
		
		if(a.size() != b.size()) return false;
		for(int x=0; x<a.size(); x++) {
			if(a.get(x) != b.get(x)) return false;
		}
		
		return true;
	}
	
	/**
	 * @param pcs a pitch class set
	 * @return a String representation of the form e.g. [0, 1, 2, 4]
	 */
	public static String toString(List<?> pcs) { 
		StringBuffer b = new StringBuffer("[");
		
		for(int x=0; x<pcs.size(); x++) { 
			b.append(pcs.get(x));
			
			if(x < pcs.size() - 1) b.append(", "); 
		}
		
		b.append("]");
		return b.toString();
	}
} // End PitchClassSets
