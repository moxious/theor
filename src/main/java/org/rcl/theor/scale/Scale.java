package org.rcl.theor.scale;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.rcl.theor.interval.Interval;
import org.rcl.theor.interval.IntervalRing;
import org.rcl.theor.note.NSequence;
import org.rcl.theor.note.Note;

public class Scale extends AbstractScale {
	public static final Scale AEOLIAN = new Scale("aeolian", Interval.parse("W h W W h W W"));
	public static final Scale ALTERED = new Scale("altered", Interval.parse("h W h W W W W"));
	public static final Scale ALTERED_bb7 = new Scale("altered bb7", Interval.parse("h W h W W h m3")); 
	public static final Scale ARABIAN = new Scale("arabian", Interval.parse("W W h h W W W")); 
	public static final Scale AUGMENTED = new Scale("augmented", Interval.parse("m3 h m3 h m3 h"));
	public static final Scale AUGMENTED_IONIAN = new Scale("augmented ionian", Interval.parse("W W h m3 h W h")); 
	public static final Scale AUGMENTED_LYDIAN = new Scale("augmented lydian", Interval.parse("W W W W h W h")); 
	public static final Scale BALINESE = new Scale("balinese", Interval.parse("h W m3 W 3"));
	public static final Scale BLUES = new Scale("blues", Interval.parse("m3 w h h m3 w"));
	public static final Scale BYZANTINE = new Scale("byzantine", Interval.parse("h m3 h W h m3 h")); 
	public static final Scale CHINESE = new Scale("chinese", Interval.parse("3 W h 3 h")); 
	public static final Scale CHROMATIC = new Scale("chromatic", Interval.parse("h h h h h h h h h h h h"));
	public static final Scale DIMINISHED_HALFTONE_WHOLETONE = new Scale("diminished halftone-wholetone", Interval.parse("h W h W h W h W")); 
	public static final Scale DOUBLE_HARMONIC = new Scale("double harmonic", Interval.parse("h m3 h w h m3 h")); 
	public static final Scale DORIAN = new Scale("dorian", Interval.parse("W h W W W h W"));
	public static final Scale EGYPTIAN = new Scale("egyptian", Interval.parse("W m3 W m3 W")); 
	public static final Scale ENIGMATIC = new Scale("enigmatic", Interval.parse("h m3 W W W h h")); 
	public static final Scale HARMONIC_MINOR = new Scale("harmonic minor", Interval.parse("w h w w h m3 h"));
	public static final Scale HINDU = new Scale("hindu", Interval.parse("W W h W h W W"));
	public static final Scale HIRAJOSHI = new Scale("hirajoshi", Interval.parse("W h 3 h 3"));
	public static final Scale HUNGARIAN_MAJOR = new Scale("hungarian major", Interval.parse("m3 h W h W h W"));
	public static final Scale HUNGARIAN_MINOR = new Scale("hungarian minor", Interval.parse("W h m3 h h m3 h"));
	public static final Scale ICHIKOSUCHO = new Scale("ichikosucho", Interval.parse("W W h h h W W h")); 
	public static final Scale IONIAN = new Scale("ionian", Interval.parse("w w h w w w h"));
	public static final Scale IWATO = new Scale("iwato", Interval.parse("h 3 h 3 w"));
	public static final Scale KUMOI = new Scale("kumoi", Interval.parse("W h 3 W m3")); 
	public static final Scale LEADING_WHOLETONE = new Scale("leading wholetone", Interval.parse("W W W W W h h")); 
	public static final Scale LOCRIAN = new Scale("locrian", Interval.parse("h W W h W W W"));
	public static final Scale LYDIAN = new Scale("lydian", Interval.parse("W W W h W W h"));
	public static final Scale MAJOR = new Scale("major", Interval.parse("w w h w w w h"));
	public static final Scale MAJOR_BEBOP = new Scale("major bebop", Interval.parse("w w h w w h h h"));
	public static final Scale MAJOR_PENTATONIC = new Scale("major pentatonic", Interval.parse("w w m3 w m3"));	
	public static final Scale MELODIC_MINOR = new Scale("melodic minor", Interval.parse("w h w w w w h"));
	public static final Scale MINOR_PENTATONIC = new Scale("minor pentatonic", Interval.parse("m3 w w m3 w"));
	public static final Scale MIXOLYDIAN = new Scale("mixolydian", Interval.parse("W W h W W h W"));
	public static final Scale MOHAMMEDAN = new Scale("mohammedan", Interval.parse("W h W W h m3 h")); 
	public static final Scale MONGOLIAN = new Scale("mongolian", Interval.parse("W W m3 W m3")); 
	public static final Scale NATURAL_MINOR = new Scale("natural minor", Interval.parse("w h w w h w w"));
	public static final Scale OVERTONE = new Scale("overtone", Interval.parse("W W W h W h W"));
	public static final Scale PELOG = new Scale("pelog", Interval.parse("h W 3 h 3"));
	public static final Scale PERSIAN = new Scale("persian", Interval.parse("h m3 h h W m3 h")); 
	public static final Scale PHRYGIAN = new Scale("phrygian", Interval.parse("h W W W h W W"));
	public static final Scale PROMETHEUS = new Scale("prometheus", Interval.parse("W W W m3 h W"));
	public static final Scale PURVI_THETA = new Scale("purvi theta", Interval.parse("h m3 W h h m3 h"));	
	public static final Scale SIX_TONE_SYMMETRICAL = new Scale("six tone symmetrical", Interval.parse("h m3 h m3 h m3"));
	public static final Scale SPANISH_8_TONE = new Scale("spanish 8 tone", Interval.parse("h W h h h W W W"));
	public static final Scale TODI_THETA = new Scale("todi theta", Interval.parse("h W m3 h h m3 h")); 
	public static final Scale TRITONE = new Scale("tritone", Interval.parse("h m3 m3 h m3 h"));	 
	public static final Scale WHOLETONE = new Scale("wholetone", Interval.parse("w w w w w w")); 
		
	public static HashMap<String,Scale> scaleCatalog;
	
	protected String formula = null;
	protected Note root = Note.MIDDLE_C;	
	
	public Scale(String name, IntervalRing ring) { 
		this(name, ring.getRing());
	}
	
	public Scale(String name, Interval[] intervals) {
		this(name, intervals, Note.MIDDLE_C);
		
		if(!getIntervals().isCyclical()) 
			System.err.println("Warning: scale " + getName() + " contains non-cyclical intervals."); 
	}
	
	public Scale(String name, Interval [] intervals, Note root) { 
		setName(name);
		setIntervals(intervals);
		setRoot(root); 
		
		StringBuffer b = new StringBuffer("");
		for(Interval i : intervals)
			b.append(Interval.name(i) + " " ); 
		
		formula = b.toString().trim();
		
		if(scaleCatalog == null) scaleCatalog = new HashMap<String,Scale>();
		if(!scaleCatalog.containsKey(name)) scaleCatalog.put(name, this); 
	}
	
	public Interval absoluteDistanceToNote(int noteIdx) { 
		if(noteIdx < 0) return null;
		
		int tot = 0; 
				
		for(int x=0; x<noteIdx; x++) { 
			tot += intervals.get(x).intValue(); 
		}
		
		return new Interval(tot);
	}
	
	public String getFormula() { return formula; } 
	public int countNotes() { return apply().size(); } 
	
	public NSequence apply() { return apply(root); } 
	
	public NSequence applyN(Note base, int n) { 
		NSequence r = new NSequence();
		intervals.reset();
		r.add(root);
		Note last = root; 
		
		for(int x=0; x<n; x++) { 
			Interval i = intervals.next();
			//System.out.println("ApplyN(" + x + "): " + i + " applied to " + last + " => "); 
			last = i.apply(last);
			//System.out.println(last);
			r.add(last);
		}
		
		return r;
	}
	
	/**
	 * Returns the tone classes that are a member of this scale starting from base.
	 * @param base the base (tonic) of the scale
	 * @return a set of Integer tone classes.
	 */
	public Set<Integer> getMembers(Note base) {
		HashSet<Integer> toneClassMembers = new HashSet<Integer>();
		NSequence ns = apply(base);
		for(Note n : ns) { 
			toneClassMembers.add(n.getToneClass());
		}
		
		return toneClassMembers;
	}
	
	public NSequence apply(Note base) { 
		NSequence r = new NSequence();				
		setRoot(base); 
		Note last = root; 
		r.add(root);
		
		for(int x=0; x<intervals.size(); x++) {
			// System.out.print("Applied " + intervals.get(x) + " to " + last + " => ");
			last = intervals.get(x).apply(last);
			//System.out.println(last);
			r.add(last);
		}
		
		return r;
	} // End apply
	
	public void setRoot(Note n) { root = n; } 
	public Note getRoot() { return root; } 
	
	public static void main(String [] args) throws Exception {
		Scale s = BALINESE;
		Note b = new Note(Note.A, 0); 
	
		System.out.println(b + " scale " + s.getName() + " => " + s.apply(b));
		System.out.println("Scale formula: " + s.getFormula()); 
		
		/*
		Player p = new Player();
		NSequence nseq = s.applyN(b, 25);
		System.out.println("SEQUENCE:  " + nseq);
		
		Pattern pat = new Pattern();
		//pat.add(nseq.toPattern(Syncopation.makeJitteredConstant(Syncopation.QUARTER_NOTE)));
		pat.addElement(new Tempo(300)); 
		pat.add(nseq.toPattern(Syncopation.makeJitteredConstant(Syncopation.QUARTER_NOTE)));
		
		pat.add(nseq.toPattern(new Syncopation(Syncopation.QUARTER_NOTE)));
		
		p.play(pat);
		*/
		/*
		System.out.println("C IONIAN=" + IONIAN.apply(new Note(Note.C, 0)));
		System.out.println("D DORIAN=" + DORIAN.apply(new Note(Note.D, 0)));
		System.out.println("E PHRYGIAN=" + PHRYGIAN.apply(new Note(Note.E, 0)));
		System.out.println("F LYDIAN=" + LYDIAN.apply(new Note(Note.F, 0)));
		System.out.println("G MIXOLYDIAN=" + MIXOLYDIAN.apply(new Note(Note.G, 0)));
		System.out.println("A AEOLIAN=" + AEOLIAN.apply(new Note(Note.A, 0)));
		System.out.println("B LOCRIAN=" + LOCRIAN.apply(new Note(Note.B, 0)));
		*/		
	}
	
	public Collection<Note> getNotes() {
		return this.apply(root);
	}
} // End Scale
