package org.rcl.theor.composer;

import java.util.HashSet;
import java.util.Set;

/**
 * Classifications of various MIDI instruments.
 * Source information: http://www.midi.org/techspecs/gm1sound.php
 */
public class InstrumentBank {	
	public static Set<Byte>PIANO_FAMILY = makeRange(1, 8); 
	public static Set<Byte>CHROMATIC_PERCUSSION_FAMILY = makeRange(9, 16); 
	public static Set<Byte>ORGAN_FAMILY = makeRange(17, 24); 
	public static Set<Byte>GUITAR_FAMILY = makeRange(25, 32); 
	public static Set<Byte>BASS_FAMILY = makeRange(33, 40); 
	public static Set<Byte>STRINGS_FAMILY = makeRange(41, 48); 
	public static Set<Byte>ENSEMBLE_FAMILY = makeRange(49, 56); 
	public static Set<Byte>BRASS_FAMILY = makeRange(57, 64); 
	public static Set<Byte>REED_FAMILY = makeRange(65, 72); 
	public static Set<Byte>PIPE_FAMILY = makeRange(73, 80); 
	public static Set<Byte>SYNTH_LEAD_FAMILY = makeRange(81, 88); 
	public static Set<Byte>SYNTH_PAD_FAMILY = makeRange(89, 96); 
	public static Set<Byte>SYNTH_EFFECTS_FAMILY = makeRange(97, 104); 
	public static Set<Byte>ETHNIC_FAMILY = makeRange(105, 112); 
	public static Set<Byte>PERCUSSIVE_FAMILY = makeRange(113, 120); 
	public static Set<Byte>SOUND_EFFECTS_FAMILY = makeRange(121, 128); 
	
	private static Set<Byte> makeRange(int midiLow, int midiHighInclusive) { 
		HashSet<Byte> set = new HashSet<Byte>();		
		for(int x=midiLow; x<=midiHighInclusive; x++) set.add(new Byte((byte)x)); 
		return set;
	}
}
