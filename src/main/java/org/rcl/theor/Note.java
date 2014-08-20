package org.rcl.theor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.jfugue.Pattern;
import org.rcl.theor.interval.Interval;
import org.rcl.theor.midi.Patternable;
import org.rcl.theor.scale.Scale;

/**
 * A note is an object that contains a tone class, an octave, and a rendering hint.  A tone class is what most people think of
 * when they think of notes; a tone class might be C, or D flat.  Tone classes though are independent of octave; the note C may
 * occur in any number of octaves.  Octaves are relative to 0, so middle C is the note with tone class C, octave 0.  Finally, 
 * the rendering hint is whether to render a note as flat or not.  Most notes have two names; i.e. C# and Db are the same thing.
 * For that tone class, the rendering hint indicates whether the note should be considered sharp or flat.
 * @author moxious
 *
 */
public class Note implements Patternable {
	/** A */
	public static final int A = 9;
	/** A sharp */
	public static final int AS = 10;
	/** B flat */
	public static final int BF = 10;
	/** B */
	public static final int B = 11;
	/** C flat */
	public static final int CF = 11;
	/** B sharp */
	public static final int BS = 0;
	/** C */
	public static final int C = 0;
	/** C sharp */
	public static final int CS = 1;
	/** D flat */
	public static final int DF = 1;
	/** D */
	public static final int D = 2;
	/** D sharp */
	public static final int DS = 3;
	/** E flat */
	public static final int EF = 3;
	/** E */
	public static final int E = 4;
	/** F flat */
	public static final int FF = 4;
	/** E sharp */
	public static final int ES = 5;
	/** F */
	public static final int F = 5;
	/** F sharp */
	public static final int FS = 6;
	/** G flat */
	public static final int GF = 6;
	/** G */
	public static final int G = 7;
	/** G sharp */
	public static final int GS = 8;
	/** A flat */
	public static final int AF = 8;
	
	public static final Map<String,Integer> nameToInt = new HashMap<String,Integer>();
	public static final Map<Integer,String[]> intToName = new HashMap<Integer,String[]>();
	
	public static final Note MIDDLE_C = new Note(C, 0);
	public static final Note A_440 = new Note(A, 0);
	
	static {		
		intToName.put(A, new String[]{"A"});
		intToName.put(B, new String[]{"B"});
		intToName.put(C, new String[]{"C"});
		intToName.put(D, new String[]{"D"});
		intToName.put(E, new String[]{"E"});
		intToName.put(F, new String[]{"F"});
		intToName.put(G, new String[]{"G"});
		intToName.put(AS, new String[]{"A#", "Bb"});		
		intToName.put(CS, new String[]{"C#", "Db"});
		intToName.put(DS, new String[]{"D#", "Eb"});
		intToName.put(FS, new String[]{"F#", "Gb"});
		intToName.put(GS, new String[]{"G#", "Ab"});
		
		for(Integer k : intToName.keySet()) {
			String[]vals = intToName.get(k);
			for(int x=0; x<vals.length; x++) { nameToInt.put(vals[x], k); } 			
		}
	}
	
	public static final Comparator<Note>cmp = new Comparator<Note>(){
		public int compare(Note o1, Note o2) {
			byte b1 = o1.getMIDI();
			byte b2 = o2.getMIDI();
			
			if(b1 < b2) return -1;
			if(b1 > b2) return 1;
			return 0;
		}		
	};
	
	public static int getDominant(int noteConstant) { 
		return (noteConstant + 7) % 12;
	}
	
	public static int getSubdominant(int noteConstant) { 
		return (noteConstant + 5) % 12;
	}
	
	public static int getRelativeMajor(int noteConstant) { 
		return (noteConstant + 3) % 12;
	}
	
	public static int getRelativeMinor(int noteConstant) { 
		return (noteConstant + 9) % 12; 
	}
	
	/** Returns true iff the note is not natural.  Be careful, in that since tone classes
	 * have more than one name, if you ask if Eb is sharp, the answer will be yes (because it's also
	 * D#, and it's not natural)
	 * @param c tone class
	 * @return true if the tone class is not natural, false otherwise.
	 */
	public static boolean isSharp(int c) { return !isNatural(c); }
	
	/**
	 * Returns true iff the tone class is A, B, C, D, E, F, or G.
	 * @param c tone class
	 */
	public static boolean isNatural(int c) { 
		switch(c) {
		case A:
		case B:
		case C:
		case D:
		case E:
		case F:
		case G: return true;
		default: return false;
		}
	}
	
	public static String name(int toneClass) { return name(toneClass, true); }	
	public static String name(int toneClass, boolean renderFlat) {
		if(toneClass == 12) toneClass = 0; 
		
		int nc = Math.abs(toneClass) % 12;
		
		if(nc != toneClass) System.err.println("WARNING: Note#name is changing " + toneClass + " => " + nc); 
		
		String [] arr = intToName.get(nc);
		
		if(arr == null) { System.err.println("Note#name: WTF is " + toneClass + "?"); return "FOOBAR"; } 
		
		if(arr.length == 1) return arr[0];
		else { 
			if(renderFlat) return arr[1];
			else return arr[0];
		}
	}
	
	public static int noteNumber(String name) { return nameToInt.get(name); }

	/** Controls whether notes are rendered as flat or sharp, if applicable */
	protected boolean renderFlat = true; 
	protected int toneClass = -1;
	
	/** MIDI octave number */
	protected int octave = 0; 
	
	public Note(int toneCl) { this(toneCl, 0, true); } 
	
	public Note(int toneCl, int octave) { this(toneCl, octave, true); } 
	
	public Note(int toneCl, int octave, boolean renderFlat) {
		this.toneClass = toneCl;
		this.octave = octave;
		this.renderFlat = renderFlat;
	}
	
	public boolean renderFlat() { return renderFlat; } 
	public Note apply(Interval i) { return i.apply(this); } 
	public Note apply(Integer interval) { return new Interval(interval).apply(this); } 
	
	/** Calculate the frequency of this note in Hz */
	public double getFrequency() { 
		double A_440 = 440.0;
		int dist = getDistance(new Note(Note.A, 0));		 
		double a = Math.pow((double)2, (double)1/(double)12);
		
		return A_440 * Math.pow(a, dist); 
	}
	
	/** Get the MIDI byte note name */
	public byte getMIDI() { 
		byte i = (byte)(60 + (octave * 12) + toneClass);
		if(i < 0) System.err.println("BAD MIDI BYTE " + i + " for " + this + " intValue " + (int)(60 + (octave*12) + toneClass));
		return i;
	}
	
	/** Return the distance from another note, in semitones.
	 * Note that this works by MIDI value math, so it is octave sensitive.
	 */
	public int getDistance(Note other) { 
		return getMIDI() - other.getMIDI(); 
	}
	
	public Note getRelativeMajor() { return new Note(Note.getRelativeMajor(toneClass), octave); } 
	public Note getRelativeMinor() { return new Note(Note.getRelativeMinor(toneClass), octave); } 
	public Note getDominant() { return new Note(Note.getDominant(toneClass), octave); }
	public Note getSubdominant() { return new Note(Note.getSubdominant(toneClass), octave); } 
	
	
	public int getToneClass() { return toneClass; } 
	public int getOctave() { return octave; } 
	public boolean isSharp() { return Note.isSharp(toneClass); } 
	public boolean isNatural() { return Note.isNatural(toneClass); } 
	public boolean equals(Object o) { 
		if(o == null) return false;
		if(!(o instanceof Note)) return false;
		Note n = (Note)o;
		
		return getToneClass() == n.getToneClass() && getOctave() == n.getOctave();
	}
	
	public String toString() {
		String n = Note.name(getToneClass(), renderFlat);
		if(n == null) n = ""+getToneClass() + "/";
		
		// The octave is a *MIDI* octave; in MIDI, A-440 is in octave 0,
		// but in the rest of the world, it's in the 4th octave.
		return n + (octave+4); 
	}
	
	public static void main(String [] args) throws Exception { 
		for(Note n : Scale.MAJOR.apply()) {
			System.out.println(n + " freq " + n.getFrequency()); 
			System.out.println("Relative major: " + Note.name(Note.getRelativeMajor(n.getToneClass())) + 
					" Relative minor: " + Note.name(Note.getRelativeMinor(n.getToneClass())));
		}
	}
	
	public Pattern toPattern(Syncopation sync) {
		Pattern p = new Pattern();
		p.addElement(new org.jfugue.Note(getMIDI(), sync.next()));
		return p;
	}
} // End Note
