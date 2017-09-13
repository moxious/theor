package org.rcl.theor.composer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.rcl.theor.TheorException;
import org.rcl.theor.chord.Chord;

public class HarmonyChanges {
	private static Logger log = Logger.getLogger(HarmonyChanges.class.getName());
	protected List<Chord> chords = new ArrayList<Chord>();
	protected List<Double> durations = new ArrayList<Double>();
	protected double total = 0.0;

	public HarmonyChanges() {
	}

	public int size() {
		return chords.size();
	}

	public void addChange(Chord c, double duration) throws TheorException {
		if (duration <= 0)
			throw new TheorException("No negative or zero durations permitted");

		total += duration;
		chords.add(c);
		durations.add(duration);

		log.fine(c.getName() + " for " + duration + " -> " + total + " so far.");
	}

	public double whenEnds(int chord) throws TheorException {
		if (chord < 0 || chord >= chords.size())
			throw new TheorException(chord + " out of range");

		double tot = 0.0;
		for (int x = 0; x <= chord; x++)
			tot += durations.get(x);

		//log.fine("Chord " + chord + " ends at " + tot + " with durations " + qlist()); 

		return tot;
	}

	protected String qlist() {
		StringBuffer b = new StringBuffer("");
		for (double d : durations)
			b.append("" + d + " ");
		return b.toString();
	}

	public double whenStarts(int chord) throws TheorException {
		if (chord < 0 || chord >= chords.size())
			throw new TheorException(chord + " out of range");

		double tot = 0.0;
		for (int x = 0; x < chord; x++)
			tot += durations.get(x);

		//log.fine("Chord " + chord + " starts at " + tot + " with durations " + qlist()); 

		return tot;
	}

	public Chord getCurrentChord(double timePos) {
		int idx = getIndex(timePos);
		return chords.get(idx);
	}

	public double getComingChordTiming(double timePos) {
		int idx = getIndex(timePos);
		if (idx == durations.size() - 1)
			return -1;

		double tot = 0.0;
		for (int x = 0; x < (idx + 1); x++) {
			tot += durations.get(x);
		}

		return tot;
	}

	public Chord getComingChord(double timePos) {
		int idx = getIndex(timePos);
		if (idx >= chords.size() - 1)
			return null; // Nothing's coming, you're out of road.		
		return chords.get(idx + 1);
	}

	protected int getIndex(double timePos) {
		if (timePos < 0)
			return 0;

		double tot = 0;

		for (int x = 0; x < durations.size(); x++) {
			if (timePos <= (durations.get(x) + tot) && timePos >= tot)
				return x;

			tot = tot + durations.get(x);
		}

		return durations.size() - 1;
	}
}
