package org.rcl.theor.midi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.sound.midi.InvalidMidiDataException;

import org.jfugue.Pattern;
import org.jfugue.Player;
import org.rcl.theor.TheorException;
import org.rcl.theor.chord.Chord;
import org.rcl.theor.note.NSequence;
import org.rcl.theor.note.Note;
import org.rcl.theor.note.NoteCollection;
import org.rcl.theor.note.TimedNote;

public class MLoad {
	public static Map<Integer,NoteCollection> midi2NSequence(File midi) throws IOException, InvalidMidiDataException, TheorException {
		Player p = new Player();
		Pattern pat = p.loadMidi(midi);
		String data = pat.toString();
		
		HashMap<Integer,NoteCollection> moments = new HashMap<Integer,NoteCollection>();
		NoteCollection active = null;
		int activeMoment = 0;
		
		String [] toks = data.split("\\s+"); 
		
		java.util.regex.Pattern mFinder = java.util.regex.Pattern.compile("^\\@([0-9]+)$");
		java.util.regex.Pattern noteFinder = java.util.regex.Pattern.compile("\\b([A-G][\\#b])(\\d+)\\/([0-9\\.]+)");
		
		for(String tok : toks) {
			Matcher mmt = mFinder.matcher(tok);
			if(mmt.find()) { 
				activeMoment = Integer.parseInt(mmt.group(1));
				
				if(moments.containsKey(activeMoment)) active = moments.get(activeMoment);
				else {
					active = new NSequence();
					moments.put(activeMoment, active);
				}
				
				continue;
			}
			
			NSequence nseq = new NSequence();
			Matcher m = noteFinder.matcher(tok);
			while(m.find()) { 
				String note = m.group(1);
				Integer octave = Integer.parseInt(m.group(2)); 
				Float duration = Float.parseFloat(m.group(3));
				int toneClass = MLoad.parse(note);
				active.add(new TimedNote(toneClass, octave, duration)); 
			}
			
			if(active != null && active.countNotes() >= 3 && active instanceof NSequence && ((NSequence)active).getToneClasses().size()>=3) { 
				Chord c = new Chord(active);
				moments.put(activeMoment, c); 
			}
		}
		
		return moments;
	}
	
	protected static Integer parse(String noteName) throws TheorException {
		String n = noteName.trim().toLowerCase();
		char c = n.charAt(0); 
		boolean sharp = n.endsWith("#") || n.endsWith("s");
		boolean flat = n.endsWith("b") || n.endsWith("f");
		
		int toneClass = 0;
		
		switch(c) { 
		case 'a': toneClass = Note.A; break;
		case 'b': toneClass = Note.B; break;
		case 'c': toneClass = Note.C; break;
		case 'd': toneClass = Note.D; break;
		case 'e': toneClass = Note.E; break;
		case 'f': toneClass = Note.F; break;
		case 'g': toneClass = Note.G; break;
		default: throw new TheorException("Invalid note name '" + noteName + "'"); 
		}
		
		if(sharp) toneClass++;
		if(flat) toneClass--; 
		
		return toneClass;
	} // End parse
		
	public static void main(String [] args) throws Exception {
		Map<Integer,NoteCollection> moments = MLoad.midi2NSequence(new File("midi/schubert_D850_1.mid"));
		
		System.out.println(moments.size() + " distinct moments");
		int max = 0;
		ArrayList<Integer> list = new ArrayList<Integer>(moments.keySet()); 
		Collections.sort(list);
		
		for(Integer k : list) { 
			if(moments.get(k).countNotes() > 0) { 
				if(moments.get(k) instanceof Chord) { 
					System.out.println("MOMENT " + k + " + is a chord: " + moments.get(k) + " / " + ((Chord)moments.get(k)).getName());
				} else 				
					System.out.println("MOMENT " + k + " has " + moments.get(k).countNotes() + " notes: " + moments.get(k)); 
				if(moments.get(k).countNotes() > max) max = moments.get(k).countNotes(); 
			}
		}
		
		System.out.println("Maximum was " + max); 		
	}
}
