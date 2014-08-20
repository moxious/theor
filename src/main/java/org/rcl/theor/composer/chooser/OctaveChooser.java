package org.rcl.theor.composer.chooser;

import org.rcl.theor.composer.DataFeed;

public class OctaveChooser extends Chooser {
	public Integer generateConstraint(DataFeed f) {
		byte orig = f.next();
		byte n = (byte)Math.abs(orig);
		int o = n % 3;
		
		if(orig < 0) o *= -1;
		
		//System.out.println("Byte " + orig + " chooses ocatve " + o);
		return o;
	}
}
