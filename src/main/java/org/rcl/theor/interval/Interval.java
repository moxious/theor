package org.rcl.theor.interval;

import java.util.ArrayList;
import java.util.HashMap;

import org.rcl.theor.note.Note;
import org.rcl.theor.scale.Scale;

public class Interval {
	public static final Interval UNISON = new Interval(0);	
	public static final Interval DIMINISHED_SECOND = new Interval(0);
	public static final Interval SEMITONE = new Interval(1);
	public static final Interval HALF_STEP = new Interval(1);
	public static final Interval MINOR_SECOND = new Interval(1);
	public static final Interval SECOND = new Interval(2);
	public static final Interval WHOLE_STEP = new Interval(2);
	public static final Interval TONE = new Interval(2);
	public static final Interval DIMINISHED_THIRD = new Interval(2);
	public static final Interval MINOR_THIRD = new Interval(3);
	public static final Interval DIMINISHED_FOURTH = new Interval(4);
	public static final Interval THIRD = new Interval(4);
	public static final Interval FOURTH = new Interval(5);
	public static final Interval TRITONE = new Interval(6);
	public static final Interval DIMINISHED_FIFTH = new Interval(6);
	public static final Interval FLAT_FIFTH = new Interval(6);
	public static final Interval FIFTH = new Interval(7);	
	public static final Interval DIMINISHED_SIXTH = new Interval(7);
	public static final Interval AUGMENTED_FIFTH = new Interval(8);
	public static final Interval MINOR_SIXTH = new Interval(8);
	public static final Interval SIXTH = new Interval(9);
	public static final Interval DIMINISHED_SEVENTH = new Interval(9);
	public static final Interval MINOR_SEVENTH = new Interval(10);
	public static final Interval FLAT_SEVENTH = new Interval(10); 
	public static final Interval SEVENTH = new Interval(11);
	public static final Interval AUGMENTED_SEVENTH = new Interval(12);
	public static final Interval OCTAVE = new Interval(12);
	public static final Interval EIGHTH = new Interval(UNISON.intValue() + OCTAVE.intValue());
	public static final Interval MINOR_NINTH = new Interval(DIMINISHED_SECOND.intValue() + OCTAVE.intValue());
	public static final Interval NINTH = new Interval(SECOND.intValue() + OCTAVE.intValue());
	public static final Interval MINOR_TENTH = new Interval(MINOR_THIRD.intValue() + OCTAVE.intValue());
	public static final Interval TENTH = new Interval(THIRD.intValue() + OCTAVE.intValue());
	public static final Interval ELEVENTH = new Interval(FOURTH.intValue() + OCTAVE.intValue());
	public static final Interval TWELFTH = new Interval(FIFTH.intValue() + OCTAVE.intValue());
	public static final Interval THIRTEENTH = new Interval(SIXTH.intValue() + OCTAVE.intValue());
	public static final Interval FOURTEENTH = new Interval(SEVENTH.intValue() + OCTAVE.intValue());	
	public static final Interval FIFTEENTH = new Interval(OCTAVE.intValue() + OCTAVE.intValue());	

	public static Interval [] MINOR_TRIAD = Interval.parse("1 b3 5");
	public static Interval [] MAJOR_TRIAD = Interval.parse("1 3 5");
	public static Interval [] AUGMENTED_TRIAD = Interval.parse("1 3 #5");
	public static Interval [] DIMINISHED_TRIAD = Interval.parse("1 b3 b5");
	public static Interval [] DIMINISHED_SEVENTH_TETRAD = Interval.parse("1 b3 b5 6");
	public static Interval [] HALF_DIMINISHED_TETRAD = Interval.parse("1 b3 b5 b7");
	public static Interval [] MINOR_SEVENTH_TETRAD = Interval.parse("1 b3 5 b7");
	public static Interval [] DOMINANT_SEVENTH_TETRAD = Interval.parse("1 3 5 b7");
	public static Interval [] MAJOR_SEVENTH_TETRAD = Interval.parse("1 3 5 7");
	public static Interval [] AUGMENTED_SEVENTH_TETRAD = Interval.parse("1 3 #5 b7");
	public static Interval [] AUGMENTED_MAJOR_SEVENTH_TETRAD = Interval.parse("1 3 #5 7");
	public static Interval [] POWER_CHORD = Interval.parse("1 5");
	public static Interval [] POWER_SEVENTH = Interval.parse("1 5 b7"); 
	
	protected static HashMap<String,Interval>map = new HashMap<String,Interval>();
	static { initializeMap(); } 	
		
	/********************************************************************************/
	
	protected int i;
	public Interval(Note one, Note two) { 
		int diff = (two.getMIDI() - one.getMIDI());
		//System.out.println("INTERVAL " + one + " MIDI " + one.getMIDI() + " - " + two + " MIDI " + two.getMIDI() + " diff " + diff); 
		// If positive, two was higher.
		if(diff > 0) diff = diff % 12;
		else if(diff < 0) diff = 12 + diff; 
		
		this.i = diff;
	}
	
	/** All intervals are divided into 7 classes (really 6, with 0 a special exception).
	 * Interval class for a given interval is between 0 and 6.
	 * @return the interval class
	 */
	public Integer getIntervalClass() { 
		if(i >= 0 && i <= 6) return i;
		if(i > 6) return 12 - i;
		
		// If the interval is negative, then move it up until it's positive, then recurse.
		int z = i;
		while(z < 0) {
			z = z + 12; // Up an octave
		}
		
		return new Interval(z).getIntervalClass();
	}
	
	public Interval(int i) { this.i = i; } 
	public int intValue() { return i; }
	public String toString() { 
		for(String k : map.keySet()) { if(map.get(k).equals(i)) return k; }
		return ""+i;
	} 

	public String getName() { return Interval.name(this); } 
	public boolean isWholeStep() { return Math.abs(i) == WHOLE_STEP.intValue(); } 
	public boolean isHalfStep() { return Math.abs(i) == HALF_STEP.intValue(); }
	public boolean isForward() { return i > 0; } 
	public boolean isReverse() { return i < 0; } 
	
	/** Return the inversion of this interval; that is, an interval to the same note, but in the opposite direction.
	 * The inversion of the unison is the unison.  */
	public Interval inverse() {  
		if(i == 0) return Interval.UNISON;
		
		return new Interval(12 - Math.abs(i));
	}
	
	public Note apply(Note n) { 
		int c = n.getPitchClass();
		int o = n.getOctave();
		c += i;
		
		if(c >= 12) { c-=12; o++; }
		if(c < 0) { c+=12; o--; } 
		
		Note r = new Note(c, o, n.renderFlat());
		return r;
	}
	
	public static Interval[] parse(String str) { 
		if(map == null) initializeMap(); 
		
		// System.out.println("Interval#parseInterval: '" + str + "'"); 
		ArrayList<Interval>is = new ArrayList<Interval>();
		String[] parts = str.split("\\s+");
		for(int x=0; x<parts.length; x++) { 
			String s = parts[x].trim().toLowerCase();
			if(map.containsKey(s)) is.add(map.get(s)); 
			else System.err.println("Interval#parse: Unrecognized interval '" + s + "'");			
		}
		
		return is.toArray(new Interval[]{});
	} // End parse
	
	public static void main(String [] args) throws Exception { 
		Scale s = Scale.MAJOR;
		System.out.println(s.apply());
		
		Note c = new Note(Note.C, 0); 
		System.out.println("A 3rd from " + c + " is " + THIRD.apply(c) + " and its inversion is " + THIRD.inverse().apply(c)); 		
	}
	
	public static String name(Interval[] ivs) {
		if(ivs == MINOR_TRIAD) return "minor triad";
		if(ivs == MAJOR_TRIAD) return "major triad";
		if(ivs == AUGMENTED_TRIAD) return "augmented triad";
		if(ivs == DIMINISHED_TRIAD) return "diminished triad";
		if(ivs == DIMINISHED_SEVENTH_TETRAD) return "diminished seventh tetrad";
		if(ivs == HALF_DIMINISHED_TETRAD) return "half diminished tetrad";
		if(ivs == MINOR_SEVENTH_TETRAD) return "minor seventh tetrad";
		if(ivs == MAJOR_SEVENTH_TETRAD) return "major seventh tetrad";
		if(ivs == AUGMENTED_SEVENTH_TETRAD) return "augmented seventh tetrad";
		if(ivs == AUGMENTED_MAJOR_SEVENTH_TETRAD) return "augmented major seventh tetrad";
		
		return "Unknown Interval Chord";
	} // End name
	
	public static String name(Interval i) { 
		for(String k : map.keySet()) { if(map.get(k).i == i.i) return k; } 
		return "Unnamed interval " + i;
	}
	
	private static void initializeMap() { 
		// Interval-ese
		map = new HashMap<String,Interval>();
		map.put("w", WHOLE_STEP); map.put("h", HALF_STEP); map.put("m3", MINOR_THIRD);
		map.put("3", THIRD); map.put("4", FOURTH); map.put("5", FIFTH); map.put("6", SIXTH);
		map.put("7", SEVENTH); map.put("8", OCTAVE);
		map.put("b3", MINOR_THIRD); map.put("b2", MINOR_SECOND);
		map.put("b5", DIMINISHED_FIFTH); map.put("b6", MINOR_SIXTH);
		map.put("#7", AUGMENTED_SEVENTH); map.put("#4", TRITONE); map.put("1", UNISON);
		map.put("#5", AUGMENTED_FIFTH); map.put("b7", MINOR_SEVENTH); 
		
		//map.put("tonic", Degree.TONIC); map.put("supertonic", Degree.SUPERTONIC);
		//map.put("mediant", Degree.MEDIANT); map.put("subdominant", Degree.SUBDOMINANT); 
		//map.put("dominant", Degree.DOMINANT); map.put("submediant", Degree.SUBMEDIANT); 
		//map.put("subtonic", Degree.SUBTONIC); map.put("leading", Degree.LEADING); 				
	} // End initializeMap
}
