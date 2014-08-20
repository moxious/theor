package org.rcl.theor.audio;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfugue.Player;
import org.rcl.theor.Ditty;
import org.rcl.theor.TheorException;

public class AudioConverter {
	private static Logger log = Logger.getLogger(AudioConverter.class.getName());
	
	static { log.setLevel(Level.INFO); }
	
	public static String makeID() { 
		return UUID.randomUUID().toString();
	}
	
	public static File saveMIDI(Ditty ditty) throws IOException, TheorException {
		long start = System.currentTimeMillis();
		File f = File.createTempFile(makeID(), ".mid"); 

		Player p = new Player(false);
		p.saveMidi(ditty.getPattern(), f); 
		
		if(!f.exists()) throw new TheorException("MIDI file doesn't exist."); 
		log.finer("saveMIDI(" + f + "): " + (System.currentTimeMillis()-start) + " ms elapsed."); 
		return f;
	} // End saveMIDI
	
	public static File saveMP3(File midiFile) throws IOException, TheorException { 
		long start = System.currentTimeMillis();
		File mp3 = File.createTempFile(makeID(), ".mp3"); 
		
		String cmd = "encodeMP3.sh " + midiFile + " " + mp3;
	
		Runtime run = Runtime.getRuntime() ;
		log.info("Executing " + cmd);
		Process pr = run.exec(cmd) ;
		try { pr.waitFor() ; } catch (InterruptedException e) {
			e.printStackTrace();
		}

		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getErrorStream()));

		String line = null;
		while ((line = buf.readLine() ) != null)  { log.severe(line); }		
		if(!mp3.exists()) throw new TheorException("MP3 conversion failed"); 
		
		log.warning("saveMIDI(" + mp3 + "): " + (System.currentTimeMillis()-start) + " ms elapsed."); 
		return mp3;
	} // End saveMP3
}
