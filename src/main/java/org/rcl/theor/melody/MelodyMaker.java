package org.rcl.theor.melody;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jfugue.Instrument;
import org.jfugue.Pattern;
import org.rcl.theor.NSequence;
import org.rcl.theor.Note;
import org.rcl.theor.TheorException;
import org.rcl.theor.TimedNote;
import org.rcl.theor.chord.Chord;
import org.rcl.theor.composer.ProgressionComposer;
import org.rcl.theor.composer.HarmonyChanges;
import org.rcl.theor.interval.Interval;
import org.rcl.theor.scale.Scale;

public class MelodyMaker {
	private static final Logger log = Logger.getLogger(MelodyMaker.class.getName());
	
	public static final int DEFAULT_OCTAVE = 1;
	protected NSequence melody = new NSequence();
	protected double pauseProbability = 0.20;
	protected Note tonic;
	protected Scale basis;
	protected int beats;
	protected HarmonyChanges changes;
	protected ToneNet tnet = null;
	protected Swing swing;
	
	public MelodyMaker(Note tonic, Scale basis, int beats, HarmonyChanges changes) { 
		this.tonic = tonic;
		this.basis = basis;
		this.beats = beats;
		this.changes = changes;
		tnet = new ToneNet(tonic, basis.apply(tonic));
		swing = new Swing(0.2);
	}
	
	public Note getLastNote() { 
		return (melody.size() >= 1 ? melody.get(melody.size()-1) : null); 
	}
	
	public Note chooseNote(float duration) { 
		int chosenToneClass = tnet.choose();
		int chosenOctave = pickOctave(getLastNote(), chosenToneClass);
		
		TimedNote tn = new TimedNote(chosenToneClass, chosenOctave, duration);
		
		log.fine("Choose note: " + tn + " OCTAVE " + chosenOctave + " MIDI " + tn.getMIDI() + " from last note " + getLastNote());
		
		return tn;
	}
	
	public Pattern planStage(int stage) throws TheorException { 
		double start = changes.whenStarts(stage);
		double end = changes.whenEnds(stage); 
		Chord current = changes.getCurrentChord(start); 
		Chord next = changes.getComingChord(start); 
		
		double stageLength = (end - start);		
		Pattern p = new Pattern();
		
		int notes = (Integer)ProgressionComposer.randomFrom(new Integer[] {2, 3, 4, 5, 8, 16});
		float duration = ((float)stageLength / (float)notes);
		//float duration = (float)0.25;		
		//notes = (int)(stageLength / duration);
		
		log.fine("Planning stage " + stage + " going from " + start + " to " + end + " (" + stageLength + ") chords " + 
				(current == null ? "N/A" : current.getName()) + " => " + (next == null ? "N/A" : next.getName()) + 
				" using " + notes + " notes with duration " + duration);
		
		double timePos = start;		
		
		for(int x=0; x<notes; x++) { 
			double pctRemaining = getPctStageRemaining(timePos, start, end); 
			/*
			if(ProgressionComposer.r.nextDouble() < pauseProbability) {
				org.jfugue.Note rest = makeRest(duration);
				log.fine("Made rest of duration " + duration + ": " + rest.getVerifyString()); 
				p.addElement(rest); 
				timePos += duration;				
				continue;
			}*/
			
			Chord affinity = chooseChordAffinity(timePos, start, end, current, next); 
			tnet.bias(affinity, getLastNote(), (1-pctRemaining));
			Note chosen = chooseNote(duration);
			melody.add(chosen);
			
			p.addElement(new org.jfugue.Note(chosen.getMIDI(), duration));
			
			timePos += duration;		
		}
		
		log.fine("Stage " + stage + " had sum duration " + (timePos-start)); 
		return p;
	}
	
	public org.jfugue.Note makeRest(double duration) { 
		org.jfugue.Note n = new org.jfugue.Note(); //new org.jfugue.Note((byte)1, duration);
		n.setDecimalDuration(duration);
		n.setRest(true);
		return n;		
	}
	
	public Pattern _makePattern(byte instrument) throws TheorException { 
		Pattern p = new Pattern("V1");
		p.addElement(new Instrument(instrument));
		
		int stages = changes.size();
		
		for(int s=0; s<stages; s++) { 
			p.add(planStage(s)); 
			p.addElement(new Instrument(instrument)); 
		} // End for		
		
		return p;
	}
	
	public Pattern makePattern(byte instrument) throws TheorException {		
		ArrayList<List<Double>> weightHistory = new ArrayList<List<Double>>();
		NSequence choices =  new NSequence();
		Pattern p = new Pattern("V1");
		p.addElement(new Instrument(instrument)); 
		
		double timePos = 0.0;
		int beatsAdded = 0;
		
		NSequence melody = new NSequence();
		
		Chord present = null;
		Chord coming = null;
		double lastChange = 0.0;
		double endTime = (double)beats * (double)0.25;
		
		while(timePos < endTime) {  // Still need to add more notes. 			
			Note lastNote = (melody.size() >= 1 ? melody.get(melody.size()-1) : null);
			present = changes.getCurrentChord(timePos);
			coming = changes.getComingChord(timePos);
			double whenChange = changes.getComingChordTiming(timePos);
			double pctRemaining = getPctStageRemaining(timePos, lastChange, whenChange); 
			
			Chord affinity = chooseChordAffinity(timePos, lastChange, whenChange, present, coming); 
						
			log.fine("\nMelodyMaker loop: so far " + beatsAdded + " of " + beats + " time sequence " + timePos + " swing " + swing + 
					           " present " + (present == null ? "N/A" : present.getName()) + " future " + 
							   (coming == null ? "N/A" : coming.getName()) + " at " + whenChange + 
							   " AFFINITY " + affinity.getName()); 
									
			tnet.bias(affinity, lastNote, (1-pctRemaining));
			weightHistory.add(tnet.getWeights()); 

			float duration = getNoteDuration();
						
			if(ProgressionComposer.r.nextDouble() <= pauseProbability && (duration * 2) <= whenChange) duration *= 2;
			
			int chosenToneClass = tnet.choose();
			int chosenOctave = pickOctave(lastNote, chosenToneClass);
			
			Note chosen = new TimedNote(chosenToneClass, chosenOctave, duration);
			melody.add(chosen);
			
			choices.add(chosen);
			p.addElement(new org.jfugue.Note(chosen.getMIDI(), duration));
			
			log.fine("CHOSEN " + chosen + 
					(lastNote != null ? " " + new Interval(lastNote, chosen).getName() + " from " + lastNote : "")); 										
			
			timePos += duration;
			if(timePos >= whenChange) {				
				lastChange = whenChange;
				swing.modify();
			}
			beatsAdded++;
		}
		
		//System.out.print("CHOSEN_NOTE,");
		//for(int x=0; x<12; x++) { System.out.print(Note.name(x)+","); }
		//log.fine();
		
		/*
		for(int x=0; x<choices.size(); x++) { 
			System.out.print(choices.get(x) + ",");
			for(int y=0; y<12; y++) { 
				System.out.print(weightHistory.get(x).get(y)+","); 
			}
			System.out.println(); 
		}*/
		
		return p;
	}
	
	public float getNoteDuration() { 
		return (float)0.25/(float)swing.getSwing();
	}
	
	public double getPctStageRemaining(double timePos, double lastChange, double whenNextChange) throws TheorException {
		if(whenNextChange == -1) return 0.0; 
		if(whenNextChange == lastChange) return 1.0;
		
		if(timePos > whenNextChange) throw new TheorException("Timepos " + timePos + " cannot be greater than next change " + whenNextChange);
		if(timePos < lastChange) throw new TheorException("Timepos " + timePos + " cannot be less than previous change " + lastChange);
		if(whenNextChange < lastChange) throw new TheorException("Next change " + whenNextChange + " cannot be smaller than previous change " + lastChange); 
		
		double difference = whenNextChange - lastChange;
		// What percentage of the total gap in time has been crossed?
		double pctRemaining = (whenNextChange - timePos) / difference;
		return pctRemaining;
	}
	
	/**
	 * At any given time, the melody is biased towards an affinity either for the present chord, or the chord that's coming.
	 * This function helps pick which to be biased towards at any given point in time.   The bias should increase with proximity
	 * to the chord in time, but still be flexible to move.
	 * @param timePos the time position you're at when you want to find what your bias should be.
	 * @param lastChange when the present chord started playing.
	 * @param whenNextChange when the next chord change is coming.
	 * @param present the chord presently being played or ringing.
	 * @param coming the chord that will be played at whenChange
	 * @return the chord that the ToneNet should be biased towards.
	 */
	public Chord chooseChordAffinity(double timePos, double lastChange, double whenNextChange, Chord present, Chord coming) throws TheorException { 
		if(present != null && coming == null) return present; // only one choice.
		else if(present == null && coming != null) return coming; // only one choice.
		else if(lastChange == whenNextChange) return present;
		else {			
			if(timePos > whenNextChange) throw new TheorException("Timepos " + timePos + " cannot be greater than next change " + whenNextChange);
			if(timePos < lastChange) throw new TheorException("Timepos " + timePos + " cannot be less than previous change " + lastChange);
			if(whenNextChange < lastChange) throw new TheorException("Next change " + whenNextChange + " cannot be smaller than previous change " + lastChange); 
			
			double pctRemaining = getPctStageRemaining(timePos, lastChange, whenNextChange); 
			double randVal = ProgressionComposer.r.nextDouble();
			
			//System.out.println("chooseChordAffinity: timepos=" + timePos + " last=" + lastChange + " next=" + whenNextChange + " pctDone=" + pctRemaining + " choice=" + randVal); 
			
			if(randVal > pctRemaining) return coming;  // Increasingly unlikely as pctDone gets bigger, but likely early on.
			return present; // Very likely as pctDone gets bigger, unlikely early on.
		}
	}
	
	public int pickOctave(Note lastNote, int chosenToneClass) { 
		if(lastNote == null) return DEFAULT_OCTAVE;
		int lnm = lastNote.getMIDI();		
		
		int thisOct = new Note(chosenToneClass, lastNote.getOctave()).getMIDI();
		int nextOct = new Note(chosenToneClass, lastNote.getOctave()+1).getMIDI(); 
		int prevOct = new Note(chosenToneClass, lastNote.getOctave()-1).getMIDI(); 
				
		int dist1 = (lnm > thisOct ? lnm - thisOct : thisOct-lnm); 
		int dist2 = nextOct-lnm; 
		int dist3 = lnm-prevOct; 
		
		//System.out.println("From " + lastNote + " to " + Note.name(chosenToneClass) + " dists are " + dist1 + ", " + dist2 + ", " + dist3);
		
		int chosen = -1;
		if(dist1 < 6) chosen = lastNote.getOctave();
		if(dist2 < 6) chosen = lastNote.getOctave()+1;
		if(dist3 < 6) chosen = lastNote.getOctave()-1;
		
		if(chosen < (DEFAULT_OCTAVE-1)) chosen = DEFAULT_OCTAVE-1;
		if(chosen > (DEFAULT_OCTAVE+1)) chosen = DEFAULT_OCTAVE+1;
		
		return chosen;		
	}
}
