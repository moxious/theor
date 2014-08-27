package org.rcl.theor.chord;

import java.util.Collection;

/**
 * A poly chord is more than one chord stacked on top of one another, without a tonic.  Because they're poly, by nature
 * they lack a tonic.
 * @author moxious
 */
public class PolyChord extends Chord {
	private static final long serialVersionUID = -8501954914543555050L;
	protected Collection<Chord>components = null;
	
	public PolyChord(Collection<Chord>chords) {
		super();
		this.components = chords;
		
		for(Chord c : components) {	addAll(c);	}
		setTonic(null); 
	} // End PolyChord
	
	public Collection<Chord> getComponents() { return components; } 
} // End PolyChord
