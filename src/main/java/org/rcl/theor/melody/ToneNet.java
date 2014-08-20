package org.rcl.theor.melody;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.rcl.theor.NSequence;
import org.rcl.theor.Note;
import org.rcl.theor.TheorException;
import org.rcl.theor.chord.Chord;
import org.rcl.theor.composer.ProgressionComposer;
import org.rcl.theor.interval.Interval;

public class ToneNet {
	public static final double BASE_WEIGHT = (double)1/(double)12;
	protected HashSet<Integer> homeSequence = null;
	protected double [] weights = null;
	protected int chooseIDX = 0;
	protected Note tonic = null;
	
	protected static final double BIG_ENCOURAGE = (double)2.0;
	protected static final double LITTLE_ENCOURAGE = (double)1.5;
	protected static final double LITTLE_DISCOURAGE = (double)0.5;
	protected static final double BIG_DISCOURAGE = (double)0.1;
	protected static final double VETO = (double)0.0;
	
	public ToneNet(Note tonic, NSequence notes) {
		clear();
		this.tonic = tonic;
		homeSequence = notes.getToneClasses();
	}
	
	public List<Double> getWeights() {
		ArrayList<Double> w = new ArrayList<Double>();
		for(double we : weights) w.add(we);
		return w;
	}
	
	public void clear() { 
		weights = new double[] {
				BASE_WEIGHT, BASE_WEIGHT, BASE_WEIGHT, BASE_WEIGHT,   
				BASE_WEIGHT, BASE_WEIGHT, BASE_WEIGHT, BASE_WEIGHT, 
				BASE_WEIGHT, BASE_WEIGHT, BASE_WEIGHT, BASE_WEIGHT
		};
	} // End clear
	
	public Integer choose() {
		chooseIDX = ProgressionComposer.randomBetween(0, 11);
		int increment = 1; // (ProgressionComposer.r.nextBoolean() ? 1 : -1); 
		
		while(true) {
			chooseIDX = (chooseIDX + increment + 12) % 12;
			double r = ProgressionComposer.r.nextDouble();
			//System.out.println("ToneNet#choose: idx=" + Note.name(chooseIDX) + "(" + chooseIDX +") weight " + weights[chooseIDX] + " rand=" + r + " " + (r<= weights[chooseIDX] ? "succeeds" : "fails"));
			if(r <= weights[chooseIDX]) return chooseIDX;			
		}
	}
	
	protected int getPreviousInHomeSequence(int toneClass) throws TheorException { 
		for(int x=-1; x>-5; x--) { 
			int y = (toneClass + x);
			if(y < 0) y += 12;
			if(homeSequence.contains(y)) return y;
		}
		
		throw new TheorException("WTF: Can't find previous of " + Note.name(toneClass));
	}
	
	protected int getNextInHomeSequence(int toneClass) throws TheorException { 
		for(int x=1; x<5; x++) { 
			int y = (toneClass + x) % 12;
			if(homeSequence.contains(y)) return y;
		}
		
		throw new TheorException("WTF: next tone from " + Note.name(toneClass) + " couldn't be found!"); 
	}
	
	public int highestProb() { 
		double max = 0;
		int idx = 0;
		
		for(int x=0; x<weights.length; x++) { 
			if(weights[x] > max) { max = weights[x]; idx = x; } 
		}
		
		return idx;
	}
	
	protected double influence(Note n, double encouragement) { 
		return influence(n.getToneClass(), encouragement); 
	}
	
	protected double influence(int toneClass, double encouragement) { 
		weights[toneClass] *= encouragement;
		return weights[toneClass];
	}
	
	public void bias(Chord c, Note last, double resolutionBias) throws TheorException {
		if(resolutionBias > 1) resolutionBias = 1;
		else if(resolutionBias < 0) resolutionBias = 0;
		
		clear();
		Note chordTonic = c.getTonic();
		
		weights[chordTonic.getToneClass()] = (BASE_WEIGHT * 8 * resolutionBias); 		
		weights[tonic.getToneClass()] *= BIG_ENCOURAGE;
		
		NSequence toDiscourage = new NSequence(new Note[] {
				Interval.TRITONE.apply(chordTonic), Interval.AUGMENTED_FIFTH.apply(chordTonic),
				Interval.TRITONE.apply(tonic), Interval.AUGMENTED_FIFTH.apply(tonic),
				Interval.MINOR_SECOND.apply(tonic), Interval.SEVENTH.apply(tonic),
				Interval.MINOR_SECOND.apply(chordTonic)
		});
		
		for(Note n : toDiscourage) influence(n, BIG_DISCOURAGE); 
		
		int next=-1;
		int prev=-1;
		
		if(last != null) { 
			next = getNextInHomeSequence(last.getToneClass()); 
			prev = getPreviousInHomeSequence(last.getToneClass());
		}
		
		/*
		System.out.println("ToneNet#bias: biasing to chord " + c.getName() + 
				(next != -1 ? " and " + Note.name(next) + ", " + Note.name(prev) : "") + 				
				" from last note " + last + 
				" discourage on " + toDiscourage + " resolution bias " + resolutionBias); 
			*/
				
		// Everything in the biased chord gets encouragement.
		if(c != null) for(Note n : c) influence(n, LITTLE_ENCOURAGE);			
		
		// Everything in the home scale gets a little encouragement just for its membership.
		//Iterator<Integer> it = homeSequence.iterator();	
		//while(it.hasNext()) influence(it.next(), LITTLE_ENCOURAGE);
		
		for(int x=0; x<12; x++) { 
			if(homeSequence.contains(x)) influence(x, LITTLE_ENCOURAGE); 
			else influence(x, VETO); 
		}
		
		// De-emphasize the last note we just played.
		if(last != null) influence(last, LITTLE_DISCOURAGE); 		
		
		// Emphasize the next sequential notes in either direction on the scale.
		// Melodies that in part make short hops are nice.
		if(next != -1 && prev != -1) { 
			influence(next, BIG_ENCOURAGE);
			influence(prev, BIG_ENCOURAGE); 
		}
	}
	
	public String toString() { 
		int i = highestProb();
		StringBuffer b = new StringBuffer("ToneNet: highest prob " + weights[i] + " (" + Note.name(i) + ")\n");   
		for(int x=0; x<12; x++) { 
			String notestr = Note.name(x);
			
			b.append(notestr);
			if(notestr.length() == 2) b.append("    ");
			else b.append("     ");
			b.append(pct(weights[x]) + "\n"); 
			//b.append(Note.name(x) + " prob=" + pct(weights[x]) + " ");
			//b.append(Note.name(x) + "," + pct(weights[x]) + "\n");
		}
		
		return b.toString();
	}	
	
	public String pct(double d) { 
		d *= 100;
		DecimalFormat dec = new DecimalFormat("0.00");
		return dec.format(d) + "%";
	}
} // End ToneNet
