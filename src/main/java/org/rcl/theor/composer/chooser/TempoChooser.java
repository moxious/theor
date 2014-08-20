package org.rcl.theor.composer.chooser;

import org.rcl.theor.composer.DataFeed;

public class TempoChooser extends Chooser {
	public Integer generateConstraint(DataFeed df) {
		byte o = df.next();
		int t = Math.abs(o);
		
		// The slowest we want is about 90, the fastest we want is about 150.
		int bpm = 80 + (t % 71);
		//System.out.println("Byte " + o + " chooses tempo " + bpm + " BPM");
		return bpm;
	}
}
