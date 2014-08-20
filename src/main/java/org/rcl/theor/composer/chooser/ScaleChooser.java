package org.rcl.theor.composer.chooser;

import org.rcl.theor.composer.DataFeed;
import org.rcl.theor.scale.Scale;

public class ScaleChooser extends Chooser {
	public static Scale[] majorChoices = new Scale []{
		Scale.MAJOR,
		Scale.MAJOR_BEBOP,
		Scale.MAJOR_PENTATONIC
	};

	public static Scale[] minorChoices = new Scale[] {		
		Scale.NATURAL_MINOR,
		Scale.HARMONIC_MINOR,
		//Scale.MINOR_PENTATONIC
	};
		
	protected boolean major = true;
	
	public ScaleChooser(boolean major) { 
		this.major = major;
	}
	
	public Scale generateConstraint(DataFeed f) {
		byte o = f.next();
		Scale scale = null;
		
		if(major)
			scale = majorChoices[Math.abs(o) % majorChoices.length];
		else
			scale = minorChoices[Math.abs(o) % minorChoices.length];
		
		//System.out.println("Byte " + o + " chooses scale " + scale.getName());
		return scale;
	}
}
