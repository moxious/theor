package org.rcl.theor.composer.chooser;

import org.rcl.theor.Note;
import org.rcl.theor.composer.DataFeed;
import org.rcl.theor.composer.TonalCenter;

public class TonalCenterChooser extends Chooser {
	public TonalCenterChooser() { ; } 
	public TonalCenter generateConstraint(DataFeed f) {
		byte o = f.next();
		byte n = (byte)Math.abs(o);
		
		int noteConstant = n % 11;
		boolean major = !((n % 5) == 0);
	
		//System.out.println("Byte " + o + " chooses tonal center " + Note.name(noteConstant) + " " + (major ? "major" : "minor"));		
		return new TonalCenter(noteConstant, major);
	}
}
