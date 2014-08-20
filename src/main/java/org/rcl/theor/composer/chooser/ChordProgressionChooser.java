package org.rcl.theor.composer.chooser;

import org.rcl.theor.chord.ChordProgression;
import org.rcl.theor.composer.DataFeed;

public class ChordProgressionChooser extends Chooser {
	public static ChordProgression [] majorChoices = new ChordProgression [] { 
		ChordProgression.MI_I_IV_V, 
		ChordProgression.MI_IV_V, 
		ChordProgression.MI_IV_V_IV,
		ChordProgression.MI_IV_I_V7,
		ChordProgression.MI_vi_IV_V,
		ChordProgression.MI_V_vi_IV,
		ChordProgression.Mvi_IV_I_V,
	};
	
	public static ChordProgression [] minorChoices = new ChordProgression [] { 
		ChordProgression.mi_iv_v, 
		ChordProgression.mi_v_iv, 
		ChordProgression.mi_i_iv_v,
		ChordProgression.mi_iv_v_iv,	
		ChordProgression.mi_iv_i_v7,
		ChordProgression.mi_vi_iv_V,
		ChordProgression.mi_v_vi_iv,
		ChordProgression.mvVI_iv_i_v,
	};
	
	protected boolean major = true;
	
	public ChordProgressionChooser(boolean major) { 
		this.major = major;
	}
	
	public ChordProgression generateConstraint(DataFeed df) {
		byte o = df.next();
		
		ChordProgression cp = null;
		
		if(major) 
			cp = majorChoices[Math.abs(o) % majorChoices.length];
		else 
			cp = minorChoices[Math.abs(o) % minorChoices.length];
		
		//System.out.println("Byte " + o + " chooses " + (major ? "major" : "minor") + " chord progression " + cp); 
		return cp;
	}
}
