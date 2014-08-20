package org.rcl.theor;

import org.rcl.theor.interval.Interval;
import org.rcl.theor.note.NSequence;
import org.rcl.theor.note.Note;
import org.rcl.theor.note.NoteCollection;

/**
 * A sequence analyzer takes a collection of notes, and a tonic, and computes whether or not certain
 * intervals are present.
 * @author moxious
 */
public class SequenceAnalyzer {
	public boolean flatSecond;
	public boolean second; 
	public boolean augmentedSecond;
	public boolean flatThird; 
	public boolean third;
	public boolean tritone; 
	public boolean fourth; 
	public boolean flatFifth;
	public boolean fifth; 		
	public boolean augmentedFifth;
	public boolean sixth; 
	public boolean flatSeventh;
	public boolean seventh;	
	public boolean ninth;

	public SequenceAnalyzer(Note tonic, NoteCollection notes) {
		NSequence c = new NSequence(notes).sort();
		
		flatSecond = c.contains(Interval.DIMINISHED_SECOND.apply(tonic), false); 
		second = c.contains(Interval.SECOND.apply(tonic), false);		
		flatThird = c.contains(Interval.MINOR_THIRD.apply(tonic), false); 
		third = c.contains(Interval.THIRD.apply(tonic), false);
		tritone = c.contains(Interval.TRITONE.apply(tonic), false); 
		fourth = c.contains(Interval.FOURTH.apply(tonic), false); 
		flatFifth = c.contains(Interval.FLAT_FIFTH.apply(tonic), false);
		fifth = c.contains(Interval.FIFTH.apply(tonic), false); 		
		augmentedFifth = c.contains(Interval.AUGMENTED_FIFTH.apply(tonic), false);
		sixth = c.contains(Interval.SIXTH.apply(tonic), false); 
		flatSeventh = c.contains(Interval.FLAT_SEVENTH.apply(tonic), false);
		seventh = c.contains(Interval.SEVENTH.apply(tonic), false);	
		
		ninth = second;		
	}

	public boolean equals(Object o) { 
		if(o == null) return false;
		if(!(o instanceof SequenceAnalyzer)) return false;
		SequenceAnalyzer other = (SequenceAnalyzer)o;
		
		return 
				second == other.second &&
				third == other.third &&
				fourth == other.fourth &&
				fifth == other.fifth &&
				sixth == other.sixth &&
				seventh == other.seventh &&
				flatThird == other.flatThird &&
				flatFifth == other.flatFifth &&
				flatSeventh == other.flatSeventh &&
				augmentedFifth == other.augmentedFifth &&
				tritone == other.tritone;
	}
	
	public boolean isSuspended() { 
		// A suspended chord, or "sus chord" (sometimes wrongly thought to mean sustained chord), 
		// is a chord in which the third is replaced by either the "second" or the "fourth." This 
		// results in two main chord types: the suspended second (sus2) and the suspended fourth (sus4). 
		// The chords, Csus2 and Csus4, for example, consist of the notes C D G and C F G, respectively. 
		// There is also a third type of suspended chord, in which both the second and fourth are 
		// present, for example the chord with the notes C D F G."
		return (!third && !flatThird && (fourth || second));
	}
	
	public boolean majorRange() {
		return (third || fifth) && !(flatThird || flatFifth);
	}

	public boolean minorRange() { 
		return flatThird && !third;
	}
} // End SequenceAnalyzer
