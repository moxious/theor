package org.rcl.theor.chord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jfugue.Pattern;
import org.jfugue.Player;
import org.rcl.theor.SequenceAnalyzer;
import org.rcl.theor.Syncopation;
import org.rcl.theor.TheorException;
import org.rcl.theor.interval.Interval;
import org.rcl.theor.midi.Patternable;
import org.rcl.theor.note.NSequence;
import org.rcl.theor.note.Note;
import org.rcl.theor.note.NoteCollection;
import org.rcl.theor.note.PitchClassSet;

/**
 * A chord is a set of notes.   This set of notes is un-ordered, although the notes may have a natural ordering
 * (e.g. C4 is lower than E4).   A C major chord might contain the set:  { C4, E4, G4 }.   Chords always have a
 * tonic.  So the same collection of notes (C, E, G) are different chords depending on what is considered the tonic.
 * { C, E, G } is a C major chord (C tonic) but it's something different if you consider G the tonic, since the chord
 * includes a G tonic, a 4th (C) and a 6th (E).
 * @author moxious
 */
public class Chord extends HashSet<Note> implements NoteCollection, Patternable, PitchClassSet {
	private static final long serialVersionUID = 3386041763039624476L;
	protected Note tonic = null;	
	
	protected Chord() { ; } 
	
	/**
	 * Create a chord from a collection of notes.
	 * @param ncol
	 */
	public Chord(NoteCollection ncol) { 
		this(new NSequence(ncol).sort().get(0), ncol); 		
	}
	
	public Chord(Collection<Integer> pitchCollection) {
		this(new NSequence(pitchCollection));
	}
	
	public Chord(Note tonic, NoteCollection notes) {
		setTonic(tonic);
		for(Note n : notes.getNotes()) add(n); 
	}
	
	public Chord(Note tonic, Interval[]ivs) {
		setTonic(tonic);
		//System.out.println("New chord on tonic " + tonic); 
		for(Interval i : ivs) add(i.apply(tonic)); 
	}
	
	/**
	 * Return true if the chord contains a note that is the specified interval from the tonic, false otherwise.
	 * So for example, if you wanted to know if a chord contained an augumented 5th, you'd pass this method that
	 * interval.
	 */
	public boolean containsInterval(Interval i) {
		assert(i != null); 
		return contains(i.apply(tonic), false);
	}
	
	/** 
	 * Return true if the chord contains a note that is a major third from the tonic, false otherwise.
	 * Note that chord lacking a third are neither major nor minor. 
	 */
	public boolean isMajor() { return containsInterval(Interval.THIRD); }
	
	/** Return true if the chord contains a note that is a minor third from the tonic, false otherwise.
	 * Note that chords lacking a third are neither major nor minor.
	 */
	public boolean isMinor() { return containsInterval(Interval.MINOR_THIRD); } 
	
	public void setTonic(Note tonic) { this.tonic = tonic; } 
	public Note getTonic() { return tonic; } 
		
	/**
	 * @return a chord that is the major version of this chord.
	 */
	public Chord makeMajor() { 
		NSequence ncol = new NSequence();
		
		for(Note n : this) { 
			// Don't permit a minor third in our result.
			if(n.getPitchClass() == Interval.MINOR_THIRD.apply(tonic).getPitchClass()) {
				continue;
			}
			else ncol.add(n);
		}
		
		// Guarantee the presence of a major third.
		if (!ncol.contains(Interval.THIRD.apply(tonic), false))
			ncol.add(Interval.THIRD.apply(tonic));
		return new Chord(tonic, ncol);
	}
	
	/**
	 * @return a new chord that is the minor version of this chord.
	 */
	public Chord makeMinor() {
		NSequence ncol = new NSequence();
		
		for(Note n : this) { 
			// Don't permit a major third in our result.
			if(n.getPitchClass() == Interval.THIRD.apply(tonic).getPitchClass()) continue;
			else ncol.add(n);
		}
		
		// Guarantee the presence of a minor third.
		if (!ncol.contains(Interval.MINOR_THIRD.apply(tonic), false))
			ncol.add(Interval.MINOR_THIRD.apply(tonic));
		return new Chord(tonic, ncol);
	}
	
	/**
	 * Check to see if one chord equals another.
	 * @param other the other chord
	 * @param tonicSensitive if true, the tonics of the chords must be the same.  If false, the tonics may differ.
	 * @param octaveSensitive if true, the octaves must be the same.   If false, they may differ.   E.g. if octaveSensitive=true,  
	 * then { C4, E4, G4 } is not the same chord as { C5, E5, G5 }.   If octaveSensitive=false, then those two are the same chord.
	 * @return
	 */
	public boolean equals(Chord other, boolean tonicSensitive, boolean octaveSensitive) { 
		if(tonicSensitive && (getTonic() != other.getTonic())) return false;		
		return new NSequence(this).sort().equals(new NSequence(other).sort(), octaveSensitive);		
	}
	
	public NSequence asSequence() { return sort(); } 	
	public NSequence sort() { 
		NSequence l = new NSequence(this);		
		Collections.sort(l, Note.cmp);
		return l;
	}
	
	/**
	 * Returns the inversion of this particular chord.
	 * That is, 0 if the tonic is the lowest note in the chord;
	 * 1 if the tonic is the second note in the chord, and 2 if
	 * it is the third. A fourth inversion is possible for tetrads.
	 * In all other cases, this returns -1, 
	 * including if the chord only has two tones.
	 * @return the inversion number of the chord.
	 */
	public int getInversion() { 
		NSequence notes = sort();
		
		int idx = notes.indexOf(tonic);
		if(idx == -1) return -1;
		
		// If it's a non-zero index, then the inversion
		// actually corresponds to the index.
		return idx;
	}
	
	/** Returns true if the chord contains two notes, false otherwise */
	public boolean isDyad() { return size() == 2; } 
	
	/** Returns true if the chord contains three notes, false otherwise */
	public boolean isTriad() { return size() == 3; } 
	
	/** Returns true if the chord contains four notes, false otherwise */
	public boolean isTetrad() { return size() == 4; } 
	
	/** Returns true if the chord contains five notes, false otherwise */
	public boolean isPentad() { return size() == 5; } 
	
	/** Returns true if the chord contains six notes, false otherwise */
	public boolean isHexad() { return size() == 6; } 
		
	
	/**
	 * Attempt to guess a name of the chord, given the sequence and the tonic.  Standard abbreviations include aug, dim, 7, 
	 * M7 (major 7th), m (minor), m7 (minor 7th), and + (augmented 5th).  In the worse case, it will return a list of the tones
	 * with their tonic if the algorithm can't tell what this chord is supposed to be.   (E.g. C, C#, D)
	 * 
	 * <p>Information on procedures for naming chords:  http://www.standingstones.com/chordname.html
	 * Root of the chord
	 * If the third degree is minor, put m, otherwise nothing
	 * If the seventh degree is minor, put 7, else if it is major put maj 7 or a 7 with a circle around it. 
	 * If the fifth degree is perfect, put nothing. If it is diminished, put -5, if it is augmented put +5. Sometimes a chord may have both ±5. Use superscript if available.
	 * Next, the ninth degree. If it is perfect, put nothing. If it is minor, put -9. Sometimes a chord may have both ±9. (That would require a major third to be present as well.) Use superscript if available.
	 * Next, the eleventh degree. If it is perfect, put nothing. If it is augmented, put +11. No other possibilities are allowed. Use superscript if available.
	 * Next, the thirteenth degree. If it is perfect, put nothing. If it is minor, put -13. No other possibilities are allowed. Use superscript if available.
	 * 
	 * @return a string name of a chord, for example CM7 for C major 7 (C, E, G, B)
	 */
	public String getName() { 
		if(tonic == null || !contains(tonic, false)) 
			return "Atonal Chord " + asSequence() + " tonic=" + tonic; 
				
		StringBuffer b = new StringBuffer("");
		String tonicName = Note.name(tonic.getPitchClass());
		b.append(tonicName);
		
		SequenceAnalyzer r = new SequenceAnalyzer(tonic, this);
		
		/*
		if(r.flatThird) b.append("m");
		
		if(r.flatSeventh) b.append("7");
		else if(r.seventh) b.append("maj 7");
		
		if(r.flatFifth && r.augmentedFifth) 
			b.append("-/+5");
		else if(r.flatFifth)
			b.append("-5");
		else if(r.augmentedFifth)
			b.append("5");
		*/
		
		if(size() == 3) { 
			if(r.third && r.fifth) return tonicName; 			
			if(r.third && r.augmentedFifth) return tonicName + "aug";			
			if(r.flatThird && r.fifth) return tonicName + "m";
			if(r.flatThird && r.flatFifth) return tonicName + "dim";
		} else if(size() == 4) { 
			if(r.third && r.fifth && r.seventh) return tonicName + "M7"; 			
			if(r.flatThird && r.flatFifth && r.sixth) return tonicName + "dim7";  
			if(r.flatThird && r.flatFifth && r.flatSeventh) return tonicName + "m7b5";
			if(r.flatThird && r.fifth && r.seventh) return tonicName + "m maj7";
			if(r.third && r.fifth && r.flatSeventh) return tonicName + "7";
			if(r.third && r.augmentedFifth && r.flatSeventh) return tonicName + "aug7";
			if(r.third && r.augmentedFifth && r.seventh) return tonicName + "7+";
		} 
				
		if(r.majorRange()) b.append("M");
		else if(r.minorRange()) b.append("m");
		else if(r.isSuspended()) { 
			if(r.second) b.append("sus2"); 
			else if(r.fourth) b.append("sus4"); 
		}
				
		if(r.flatFifth && !r.fifth) b.append("b5");
		else if(r.flatFifth && r.fifth) b.append("#4");
		
		if(r.augmentedFifth && !r.fifth) b.append("+");
		else if(r.fifth && r.augmentedFifth) b.append("add b6");
		
		if(r.flatSeventh && !r.seventh) b.append("7");
		else if(r.seventh && !r.flatSeventh) b.append("M7");
		
		if(r.fourth) b.append("add4"); 
		if(r.ninth || r.second) b.append("add9");
		if(r.sixth) b.append("add6");
		
		return b.toString();
	} // End getName
	
	/** Return the number of notes */
	public int countNotes() { return size(); } 
	
	/** Check to see if this contains a given note; if octaveSensitive is true, it will require that the note be in a given octave. */
	public boolean contains(Note n, boolean octaveSensitive) { 
		for(Note o : this) { 
			if(o.equals(n) || (o.getPitchClass() == n.getPitchClass() && !octaveSensitive)) return true;
		}
		
		return false;
	}
	
	/**
	 * Render the chord as a JFugue pattern, given a syncopation.
	 */
	public Pattern toPattern(Syncopation sync) { 
		Pattern pat = new Pattern();
				
		// Use one syncopation value for the whole chord,
		// since it's played all at once.
		double val = sync.next();
		
		for(Note n : this) pat.addElement(new org.jfugue.Note(n.getMIDI(), val));
		
		Pattern patr = new Pattern();		
		patr.add(pat.getMusicString().trim().replaceAll("\\s+", "+"));
		return patr;
	}

	public Collection<Note> getNotes() { return this; } 
	
	public String toString() { return sort().toString(); } 
	
	public static Chord random(Note tonic) { 
		NSequence ns = new NSequence();
		
		for(int y=0; y<randIntBetween(2,3); y++) { 
			Note o = new Note(randIntBetween(0, 12), randIntBetween(0, 3), tonic.renderFlat());
			
			if(o.equals(tonic)) { y--; continue; }
			if(ns.contains(o, false)) { y--; continue; }
			
			else ns.add(o);
		}
		
		ns.add(tonic); 
		Chord c = new Chord(tonic, ns);
		return c;
	}
			
	public Chord invert(int inversion) throws TheorException {
		int s = size();
		if(s < 3) throw new TheorException("Cannot invert chords with only " + size() + " tones"); 
		if(inversion >= s) throw new TheorException("Cannot create inversion " + inversion + " of a chord with " + s + " tones"); 		
		
		int currentInversion = getInversion();
		if(currentInversion == inversion) return this;
		
		if(inversion < 0 || inversion >= s) { throw new TheorException("Inversion of a chord with " + s + " tones must be between 0 and " + s); }
		
		NSequence ns = sort();
		
		Note newTonic = getTonic();
		
		while(inversion > currentInversion) {
			// Take the bottom tone, and place it above the top tone.
			Note top = ns.get(ns.size()-1);
			Note bottom = ns.remove(0);
			
			Note newTone = null;
			if(bottom.getPitchClass() < top.getPitchClass()) newTone = new Note(bottom.getPitchClass(), top.getOctave()+1);
			else newTone = new Note(bottom.getPitchClass(), top.getOctave());
			
			ns.add(newTone);
			if(bottom.equals(tonic)) newTonic = newTone;
			
			currentInversion++;
		} // End while
		
		while(inversion < currentInversion) {
			// Take the bottom tone, and place it above the top tone.
			Note top = ns.remove(ns.size()-1); 
			Note bottom = ns.get(0);
			Note newTone = null;
			
			if(top.getPitchClass() > bottom.getPitchClass()) newTone = new Note(top.getPitchClass(), bottom.getOctave()-1);
			else newTone = new Note(top.getPitchClass(), bottom.getOctave()); 
			
			if(top.equals(tonic)) newTonic = newTone;
			currentInversion--;			
		}

		return new Chord(newTonic, ns); 
	}	
	
	public static void main(String [] args) throws Exception {
		Note tonic = new Note(Note.C, 0, true); 
						
		for(int x=0; x<10; x++) { 
			Chord c = Chord.random(tonic); 
			System.out.println("Chord " + c + " is " + c.getName());
			
		}
		
		System.exit(0);
		
		Chord c = new Chord(tonic, Interval.MAJOR_SEVENTH_TETRAD);
		
		System.out.println(c.getName() + " -- " + c);
		
		Chord v1 = c.invert(1); 
		Chord v2 = c.invert(2);
		Chord v3 = c.invert(3); 
		
		Player p = new Player();
		Pattern pat = new Pattern();
		
		Syncopation sync = new Syncopation(new Double[]{Syncopation.HALF_NOTE});
		pat.add(c.toPattern(sync));
		pat.add(v1.toPattern(sync)); 
		pat.add(v2.toPattern(sync));
		pat.add(v3.toPattern(sync)); 
		
		System.out.println("First inversion " + v1.getName() + " -- " + v1);
		System.out.println("Second inversion " + v2.getName() + " -- " + v2); 
		System.out.println("Third inversion " + v3.getName() + " -- " + v3); 
		
		p.play(pat); 
	}
	
	public static int randIntBetween(int x, int y) { 
		return x + (int)(Math.random() * ((y-x) + 1));
	}

	public Set<Integer> getPitchClasses() {
		HashSet<Integer> pcs = new HashSet<Integer>();
		for(Note n : this) pcs.add(n.getPitchClass());
		return pcs;
	}

	public List<Integer> getNormalOrder() {
		ArrayList<Integer> list = new ArrayList<Integer>(getPitchClasses());
		Collections.sort(list);
		return list;
	}

	public List<Integer> getNaturalOrder() { return getNormalOrder(); } 

	public boolean equivalent(PitchClassSet other) { 
		if(other == null) return false;
		List<Integer> nf = other.getNormalOrder();
		List<Integer> me = getNormalOrder();
		
		if(nf.size() != me.size()) return false;
		
		for(int x=0; x<me.size(); x++) { 
			if(me.get(x).intValue() != nf.get(x).intValue()) return false;
		}
		
		return true;
	}

	public Chord transpose(Interval i) {
		NoteCollection ncol = new NSequence();
		for(Note n : this)
			ncol.add(n.transpose(i));		
		return new Chord(ncol);
	}	
	
	public Chord inverse() {
		NoteCollection ncol = new NSequence();
		for(Note n : this)
			ncol.add(n.inverse());
		return new Chord(ncol); 
	}
} // End Chord
