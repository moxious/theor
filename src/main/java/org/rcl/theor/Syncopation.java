package org.rcl.theor;

import java.util.Random;

/**
 * A ring of note durations.
 * @author DMALLEN
 */
public class Syncopation extends Ring<Double> {
	public static final double WHOLE_NOTE = 1.00;
	public static final double QUARTER_NOTE = 0.25;
	public static final double HALF_NOTE = 0.5;
	public static final double TRIPLET = 0.33333;
	public static final double EIGHTH_NOTE = QUARTER_NOTE/2;
	public static final double SIXTEENTH_NOTE = EIGHTH_NOTE/2;
	public static final double THIRTYSECOND_NOTE = SIXTEENTH_NOTE/2;
	
	protected static final Random r = new Random();
	
	public Syncopation(Double constantDuration) { 
		super(new Double[] { constantDuration });
	}
	
	public Syncopation(Double[] ring) {
		super(ring);
		// TODO Auto-generated constructor stub
	}
	
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
