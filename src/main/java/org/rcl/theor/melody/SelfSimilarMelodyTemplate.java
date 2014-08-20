package org.rcl.theor.melody;

import java.util.ArrayList;
import java.util.List;

import org.jfugue.Pattern;
import org.jfugue.Player;
import org.rcl.theor.Syncopation;
import org.rcl.theor.TheorException;
import org.rcl.theor.note.NSequence;
import org.rcl.theor.note.Note;
import org.rcl.theor.scale.Scale;

/**
 * http://www.algorithmiccomposer.com/2011/06/tom-johnsons-self-similar-melodies.html
 * @author DMALLEN
 */
public class SelfSimilarMelodyTemplate {
	protected List<Integer> template = null;
	protected Scale scale = null;
	protected int applyIDX = 0;
	
	public SelfSimilarMelodyTemplate(Scale s) { 
		template = new ArrayList<Integer>();
		this.scale = s;
	}
	
	public SelfSimilarMelodyTemplate(Scale s, int[]tmpls) { 
		this(s);
		for(int t : tmpls) add(t); 
	}
	
	public void add(Integer i) { template.add(i); } 
	
	protected int normalize(int idx, int len) { 
		if(idx < 0) return len + idx;
		if(idx >= len) return idx % len;
		return idx;
	}
	
	public NSequence apply(Note base) throws TheorException { 
		NSequence ns = new NSequence();		
		NSequence scaleSeq = scale.apply(base);
				
		int idx = normalize(applyIDX, scaleSeq.size()); 
		
		for(int x=0; x<template.size(); x++) { 
			int pos = template.get(x); 
			idx = normalize(idx + pos, scaleSeq.size()); 			
			Note n = scaleSeq.get(idx);
			ns.add(n);
		}
		
		applyIDX++;		
		return ns;
	}
	
	public static void main(String [] args) throws Exception { 
		SelfSimilarMelodyTemplate tmpl = new SelfSimilarMelodyTemplate(Scale.MAJOR, new int[] {0, 1, -1, 0});
		
		Player p = new Player();
		Pattern pat = new Pattern();
		
		for(int x=0; x<5; x++) 
			pat.add(tmpl.apply(new Note(Note.C, 0)).toPattern(new Syncopation(0.25)));
		
		p.play(pat); 
	}
}
