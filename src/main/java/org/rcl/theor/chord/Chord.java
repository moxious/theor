package org.rcl.theor.chord;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.jfugue.Pattern;
import org.jfugue.Player;
import org.rcl.theor.NSequence;
import org.rcl.theor.Note;
import org.rcl.theor.NoteCollection;
import org.rcl.theor.SequenceAnalyzer;
import org.rcl.theor.Syncopation;
import org.rcl.theor.TheorException;
import org.rcl.theor.interval.Interval;
import org.rcl.theor.midi.Patternable;

public class Chord extends HashSet<Note> implements NoteCollection, Patternable {
	private static final long serialVersionUID = 3386041763039624476L;
	protected Note tonic = null;	
	
	protected Chord() { ; } 
	
	public Chord(NoteCollection ncol) { 
		this(new NSequence(ncol).sort().get(0), ncol); 		
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
	
	public void setTonic(Note tonic) { this.tonic = tonic; } 
	public Note getTonic() { return tonic; } 
	
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
	
	public boolean isDyad() { return size() == 2; } 
	public boolean isTriad() { return size() == 3; } 
	public boolean isTetrad() { return size() == 4; } 
	public boolean isPentad() { return size() == 5; } 
	public boolean isHexad() { return size() == 6; } 
		
	public String getName() { 
		if(tonic == null || !contains(tonic, false)) return "IDFK: " + asSequence() + " tonic=" + tonic; 
				
		StringBuffer b = new StringBuffer("");
		String tonicName = Note.name(tonic.getToneClass());
		b.append(tonicName);
		
		SequenceAnalyzer r = new SequenceAnalyzer(tonic, this);
		
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
		if(r.second) b.append("add9");
		if(r.sixth) b.append("add6");
		
		return b.toString();
		/*
		return "IDFK: " + asSequence() + " tonic=" + tonic + 
				(" 2="+ r.second + " b3=" + r.flatThird + " 3=" + r.third + 
				 " 4=" + r.fourth + " b5=" + r.flatFifth + " 5=" + r.fifth + 
				 " b6=" + r.augmentedFifth + " 6=" + r.sixth + " b7=" + r.flatSeventh + 
				 " 7=" + r.seventh);
		*/ 
	} // End getName
	
	public int countNotes() { return size(); } 
	
	/** Check to see if this contains a given note; if octaveSensitive is true, it will require that the note be in a given octave. */
	public boolean contains(Note n, boolean octaveSensitive) { 
		for(Note o : this) { 
			if(o.equals(n) || (o.getToneClass() == n.getToneClass() && !octaveSensitive)) return true;
		}
		
		return false;
	}
	
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
			if(bottom.getToneClass() < top.getToneClass()) newTone = new Note(bottom.getToneClass(), top.getOctave()+1);
			else newTone = new Note(bottom.getToneClass(), top.getOctave());
			
			ns.add(newTone);
			if(bottom.equals(tonic)) newTonic = newTone;
			
			currentInversion++;
		} // End while
		
		while(inversion < currentInversion) {
			// Take the bottom tone, and place it above the top tone.
			Note top = ns.remove(ns.size()-1); 
			Note bottom = ns.get(0);
			Note newTone = null;
			
			if(top.getToneClass() > bottom.getToneClass()) newTone = new Note(top.getToneClass(), bottom.getOctave()-1);
			else newTone = new Note(top.getToneClass(), bottom.getOctave()); 
			
			if(top.equals(tonic)) newTonic = newTone;
			currentInversion--;			
		}

		return new Chord(newTonic, ns); 
	}	
	
	public static void main(String [] args) throws Exception {
		Note tonic = new Note(Note.C, 0, true); 
				
		/*
		for(int x=0; x<10; x++) { 
			Chord c = Chord.random(tonic); 
			System.out.println("Chord " + c + " is " + c.getName()); 
		}
		*/
		
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
} // End Chord
