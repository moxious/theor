package org.rcl.theor.chord;

import java.util.ArrayList;
import java.util.List;

import org.jfugue.Pattern;
import org.jfugue.Player;
import org.rcl.theor.Note;
import org.rcl.theor.Syncopation;
import org.rcl.theor.TheorException;
import org.rcl.theor.interval.Interval;

public class ChordProgression {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 6238994969781023078L;
	public enum Direction { UP, DOWN, ALTERNATE, RANDOM };

	// MAJOR CHORD PROGRESSIONS:   I - ii - iii - IV - V - vi - viidim	
	public static final ChordProgression MI_IV_V = makeProgression("I IV V", true); 
	public static final ChordProgression MI_V_IV = makeProgression("I V IV", true); 
	public static final ChordProgression MI_I_IV_V = makeProgression("I I IV V", true);
	public static final ChordProgression MI_IV_V_IV = makeProgression("I - IV - V - IV", true);	
	public static final ChordProgression MI_IV_I_V7 = makeProgression("I - IV - I - V7", true);
	public static final ChordProgression MI_vi_IV_V = makeProgression("I - vi - IV - V", true);
	public static final ChordProgression MI_V_vi_IV = makeProgression("I-V-vi-IV", true);
	public static final ChordProgression Mvi_IV_I_V = makeProgression("vi-IV-I-V", true);
	public static final ChordProgression MI_vi_ii_IV_V7 = makeProgression("I-vi-ii-IV-V7", true);
	public static final ChordProgression MI_vi_ii_V7_ii = makeProgression("I-vi-ii-V7-ii", true);
	public static final ChordProgression BY_STEPS_TO_FIFTH = makeProgression("By steps to fifth", "I - ii - iii - IV - V", true);
	public static final ChordProgression MI_IV_vii_iii_vi_ii_V_I = makeProgression("I - IV - viio - iii - vi - ii - V - I", true);
	
	// MINOR CHORD PROGRESSIONS:   i - iidim - III - iv - v - VI - VII
	public static final ChordProgression mi_iv_v = makeProgression("i - iv - v", false); 
	public static final ChordProgression mi_v_iv = makeProgression("i - v - iv", false); 
	public static final ChordProgression mi_i_iv_v = makeProgression("i - i - iv - v", false);
	public static final ChordProgression mi_iv_v_iv = makeProgression("i - iv - v - iv", false);	
	public static final ChordProgression mi_iv_i_v7 = makeProgression("i - iv - i - v7", false);
	public static final ChordProgression mi_vi_iv_V = makeProgression("i - vi - iv - V", false);
	public static final ChordProgression mi_v_vi_iv = makeProgression("i - v - vi - iv", false);
	public static final ChordProgression mvVI_iv_i_v = makeProgression("VI - iv - i - v", false);
	public static final ChordProgression mi_VI_iidim_iv_v7 = makeProgression("i - VI - iidim - iv - v7", false);
	public static final ChordProgression mi_VI_iidim_v7_iidim = makeProgression("i - VI - iidim - v7 - iidim", false);
	
	public static final ChordProgression ANDALUSIAN_CADENCE = makeProgression("Andalusian Cadence", "i VII VI V", false);	
	public static final ChordProgression DESCENDING_MINOR_CIRCLE = makeProgression("Descending minor circle", "iv - VII - III - i", false);
	
	protected String formula = null;
	protected boolean major = true;
	protected ChordTemplate[]templates = null;
	protected String name = null;
		
	public ChordProgression(String name, String tokens, boolean major) throws TheorException {
		this.major = major;
		this.name = name;
		this.formula = tokens.replaceAll(" ", "");
		templates = ChordTemplate.parse(tokens, major);				
	}
	
	public String getFormula() { return formula; } 
	
	public int size() { return templates.length; } 
	public boolean isMajor() { return major; } 
	public boolean isMinor() { return !isMajor(); } 
	
	public List<Chord> apply(Note tonic) throws TheorException { return apply(tonic, Direction.UP); } 
	
	public List<Chord> apply(Note tonic, Direction dir) throws TheorException {
		ArrayList<Chord> results = new ArrayList<Chord>();

		Direction d = dir;
		int x=0; 
				
		for(ChordTemplate ct : templates) {
			if(dir == Direction.RANDOM) { 
				boolean up = (int)(Math.random()*1000) % 2 == 0;
				if(up) d = Direction.UP;
				else d = Direction.DOWN;
			} else if(dir == Direction.ALTERNATE) {
				if(x % 2 == 0) d = Direction.UP;
				else d = Direction.DOWN;
			}
		
			// Guarantee that we're going the right direction.
			Interval i = ct.getDistanceFromTonic();
			if(d == Direction.UP) { 
				if(i.isReverse()) i = i.getInversion();
			} else { 
				if(i.isForward()) i = i.getInversion();
			}
						
			Note chordBaseNote = i.apply(tonic);
			results.add(new Chord(chordBaseNote, ct.getIntervals()));
			x++;
		}
		
		return results;
	} // End apply
	
	public String toString() { return getName(); } 
	public String getName() { return name; } 
	public void setName(String name) { this.name = name; } 
		
	public static void main(String [] args) throws Exception { 		 
		try { 
			Player p = new Player();
			Pattern pat = new Pattern();
			
			ChordProgression prog = MI_IV_V;
			Note n = new Note(Note.C, -1); 
			
			for(int x=0; x<3; x++) { 
				for(Chord c : prog.apply(n, Direction.DOWN))
					pat.add(c.toPattern(new Syncopation(Syncopation.QUARTER_NOTE))); 
				n = Interval.FIFTH.apply(n);
			}
			
			p.play(pat);
		} catch(Exception e) { 
			e.printStackTrace();
			System.err.println("CAUSE:"); 
			e.getCause().printStackTrace();
		}
	}

	protected static ChordProgression makeProgression(String tokens, boolean major) { return makeProgression(tokens, tokens, major); } 	
	protected static ChordProgression makeProgression(String name, String tokens, boolean major) {
		try { 
			return new ChordProgression(name, tokens, major); 
		} catch(Exception exc) { 
			exc.printStackTrace();
		}
		
		return null;
	}
} // End ChordProgression
