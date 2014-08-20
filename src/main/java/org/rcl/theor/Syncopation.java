package org.rcl.theor;

import java.util.Random;

/**
 * A ring of note durations.   This permits expression of various syncopation patterns, e.g. 
 * quarter, quarter, half note.
 * @author moxious
 */
public class Syncopation extends Ring<Double> {
	/** JFugue duration for a whole note (1.00) */
	public static final double WHOLE_NOTE = 1.00;
	/** JFugue duration for a quarter note (0.25) */
	public static final double QUARTER_NOTE = 0.25;
	/** JFugue duration for a half note (0.5) */
	public static final double HALF_NOTE = 0.5;
	/** JFugue duration for a triplet (0.333) */
	public static final double TRIPLET = 0.33333;
	/** JFugue duration for an eighth note (half of a quarter) */
	public static final double EIGHTH_NOTE = QUARTER_NOTE/2;
	/** JFugue duration for a sixteenth note (half of an eigth) */
	public static final double SIXTEENTH_NOTE = EIGHTH_NOTE/2;
	/** JFugue duration for a thirty-second note (half of a sixteenth) */
	public static final double THIRTYSECOND_NOTE = SIXTEENTH_NOTE/2;
	/** JFugue duration for a sixty-fourth note (half of a thirty-second) */
	public static final double SIXTYFOURTH_NOTE = THIRTYSECOND_NOTE/2;
	
	protected static final Random r = new Random();
	
	public Syncopation(Double constantDuration) { 
		super(new Double[] { constantDuration });
	}
	
	public Syncopation(Double[] ring) {
		super(ring);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * "Jitter" is applying a tiny random variation to a given number.  This gives it a more "human" feel.
	 * When the computer plays perfect robotic quarter notes like an atomic clock, it just sounds weird.  This
	 * method simulates the imperfection of being human by adding small random amounts of jitter (positive and negative)
	 * to the note duration, so that the human-sounding player is sometimes slightly ahead or behind of the beat.
	 * @param constant the duration you want to jitter (like a quarter note)
	 * @return
	 */
	public static Syncopation makeJitteredConstant(double constant) { 
		return new Syncopation(new Double[]{
			jitter(constant),
			jitter(constant),
			jitter(constant),
			jitter(constant),
			jitter(constant),
			jitter(constant),
			jitter(constant),
			jitter(constant),
			jitter(constant),
			jitter(constant),
			jitter(constant),
			jitter(constant),
		});
	} // End makeJitteredConstant
	
	public static double jitter(double d) { 
		double rand = r.nextDouble();
		double val = (Syncopation.THIRTYSECOND_NOTE/2) * rand;
		
		if(r.nextBoolean()) return d - val;
		else return d + val;		
	}
	
	public static void main(String [] args) throws Exception { 
		for(int x=0; x<20; x++) { 
			System.out.println("JITTER WHOLENOTE: " + jitter(Syncopation.WHOLE_NOTE));
			System.out.println("JITTER QUARTER: " + jitter(Syncopation.QUARTER_NOTE));
		}
	}	
}
