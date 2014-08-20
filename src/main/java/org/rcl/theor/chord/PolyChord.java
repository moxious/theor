package org.rcl.theor.chord;

import java.util.Collection;

public class PolyChord extends Chord {
	private static final long serialVersionUID = -8501954914543555050L;
	protected Collection<Chord>components = null;
	
	public PolyChord(Collection<Chord>chords) {
		super();
		this.components = chords;
		
		for(Chord c : components) {	addAll(c);	}
		setTonic(null); 
	} // End PolyChord
} // End PolyChord
