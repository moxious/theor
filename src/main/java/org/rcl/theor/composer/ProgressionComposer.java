package org.rcl.theor.composer;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.jfugue.Instrument;
import org.jfugue.Pattern;
import org.jfugue.Player;
import org.jfugue.Tempo;
import org.rcl.theor.Syncopation;
import org.rcl.theor.TheorException;
import org.rcl.theor.chord.Chord;
import org.rcl.theor.chord.ChordProgression;
import org.rcl.theor.composer.chooser.ChordProgressionChooser;
import org.rcl.theor.composer.chooser.InstrumentChooser;
import org.rcl.theor.composer.chooser.InstrumentChooser.InstrumentFunction;
import org.rcl.theor.composer.chooser.OctaveChooser;
import org.rcl.theor.composer.chooser.ScaleChooser;
import org.rcl.theor.composer.chooser.TempoChooser;
import org.rcl.theor.composer.chooser.TonalCenterChooser;
import org.rcl.theor.melody.MelodyMaker;
import org.rcl.theor.note.Note;
import org.rcl.theor.scale.Scale;

public class ProgressionComposer extends Composer {
	private static final Logger log = Logger.getLogger(ProgressionComposer.class.getName());
	public static final int TIMEFRAME_SECS = 7;
		
	protected byte[] shaHash = null;
	protected DataFeed df;
	protected TonalCenter center;
	protected Integer harmonyOctave;
	protected Integer melodyOctave;
	protected byte melodyInstrument;
	protected byte harmonyInstrument;
	protected Scale scale;
	protected ChordProgression cProg;
	protected Integer tempo;
	protected Note tonic;
	protected int beats;
	protected int beatsPerMeasure = 4;	
	protected HarmonyChanges harmonyChanges = new HarmonyChanges(); 
		
	public ProgressionComposer() {  } 

	protected void initializeProgression(DataFeed df) throws TheorException { 
		this.df = df;		
		Composer.init(df); 
		
		center = new TonalCenterChooser().generateConstraint(df);		
		melodyInstrument = new InstrumentChooser(InstrumentFunction.MELODY).generateConstraint(df);
		harmonyInstrument = new InstrumentChooser(InstrumentFunction.HARMONY).generateConstraint(df);
		scale = new ScaleChooser(center.major).generateConstraint(df);
		cProg = new ChordProgressionChooser(center.major).generateConstraint(df); 
		tempo = new TempoChooser().generateConstraint(df);
		harmonyOctave = new OctaveChooser().generateConstraint(df);
		melodyOctave = new OctaveChooser().generateConstraint(df); 
		
		tonic = new Note(center.noteConstant, harmonyOctave); 
		
		// Figure out how many beats we can do in the timeframe.
		int maxBeats = getMaxBeatsForTimeframe(tempo);
		beats = greatestEvenBarCountLessThan(4, maxBeats);
		log.fine("With a tempo of " + tempo + " and a constraint of " + TIMEFRAME_SECS + 
				           " sec, we could get " + maxBeats + " beats.");
		log.fine("But we'll actually use " + beats);		
	}
	
	public Properties getProperties() { 
		Properties p = new Properties();
		
		p.setProperty("Data Length", ""+df.data.length);
		p.setProperty("Tonal Center", ""+center);
		p.setProperty("Progression", cProg.getFormula()); 
		p.setProperty("Harmony Instrument", Instrument.INSTRUMENT_NAME[harmonyInstrument]); 
		p.setProperty("Melody Instrument", Instrument.INSTRUMENT_NAME[melodyInstrument]);
		p.setProperty("Tempo", ""+tempo);
		p.setProperty("Melody Octave", ""+melodyOctave);
		p.setProperty("Harmony Octave", ""+harmonyOctave);
		p.setProperty("Scale", scale.name); 
		
		return p;
	}
	
	public String getName() { 
		int bytes = df.data.length;
				
		
		return "Len-" + bytes + "_" + center + "_PROG_" + cProg.getFormula() + "_harmony_"+Instrument.INSTRUMENT_NAME[harmonyInstrument] + 
				"_melody_" + Instrument.INSTRUMENT_NAME[melodyInstrument] + "_tempo_" + tempo + 
				"_mOctave_" + melodyOctave + "_hOctave_" + harmonyOctave + "_scale_" + scale.name;
	}
		
	public Ditty compose(DataFeed df) throws TheorException {
		initializeProgression(df); 
		
		log.fine("Beginning composition."); 
		Pattern container = new Pattern();
		container.addElement(new Tempo(tempo)); 
				
		container.add(makeHarmony()); 
				
		MelodyMaker mm = new MelodyMaker(tonic, scale, beats, harmonyChanges);
		container.add(mm.makePattern(melodyInstrument));  
		
		Ditty ditty = new Ditty(container);
		ditty.setProperties(getProperties()); 
		return ditty;
	}
		
	protected int getMaxBeatsForTimeframe(int bpmTempo) { 
		double bps = (double)bpmTempo/(double)60;		
		double maxTotal = (double)TIMEFRAME_SECS * bps;		
		return (int) maxTotal;
	}
	
	public double countBeats(double [] list) { 
		double d = 0;
		for(double r : list) d+=r;
		return d * beatsPerMeasure;
	}
	
	public Pattern makeHarmony() throws TheorException { 
		Pattern p = new Pattern("V0");
		p.addElement(new Instrument(harmonyInstrument)); 
		
		// Divide this many chords over the available beats.
		int chordCount = cProg.size();
		double [] durations = new double[chordCount];
		for(int x=0; x<chordCount; x++) { durations[x] = Syncopation.QUARTER_NOTE; } 		
		log.fine("Hmmm...I've got " + chordCount + " to play in " + beats);
		
		int idx = 0;
		while((int)countBeats(durations) < beats) {
			//log.fine("Increasing chord " + idx + " by a quarter...");
			durations[idx] += Syncopation.QUARTER_NOTE;
			idx = (idx + 1) % durations.length;
		}
				
		// Make last one ring for a while.
		durations[durations.length-1] = randomBetween(2,5) * durations[durations.length-1]; 
		
		List<Chord>chords = cProg.apply(new Note(tonic.getToneClass(), harmonyOctave));		
		double total = 0.0;
		
		for(int x=0; x<chords.size(); x++) {
			Chord c = chords.get(x);
			//log.fine("Adding chord " + c + " with timing " + durations[x]); 
			p.add(c.toPattern(new Syncopation(durations[x])));
			
			harmonyChanges.addChange(c, durations[x]); 
			total += durations[x];
		}
		
		return p;
	} // End makeHarmony

	private int greatestEvenBarCountLessThan(int barCount, int max) {
		int start = (max/barCount)+1;
		
		for(int x=start; x>=1; x--) { 
			int beats = barCount * x;
			if(beats < max) return beats;
		}
		
		return 0;
	}
			
	public static void main(String [] args) throws Exception {
		String [] strings = new String [] { "David Rafner", "Hedge Fund", "a", "mdavidallen", "th3j35t3r", "pca", "testing", "twitter", "FACEBOOK", 
				"just_for_yuks", "meatbus", "12132143213" };
		
		Player p = new Player();
		
		ProgressionComposer cmp = new ProgressionComposer();
		
		for(String s : strings) { 			
			Pattern pat = cmp.compose(new DataFeed(s.getBytes())).getPattern();
			
			log.fine(pat.toString());
			
			log.fine("======================");
			log.fine("PLAYING " + s); 
			log.fine("======================");
			//p.play(pat);
		
			p.saveMidi(pat, new File("c:\\users\\dmallen\\desktop\\midi\\" + s + "-" + cmp.getName() + ".mid"));
			
			log.fine("ProgressionComposer " + cmp.getName());
		} 
	}
	
	public static Object randomFrom(Object[]set) { 
		return set[randomBetween(0, set.length-1)];
	}
	
	public static int randomBetween(int min, int max) { 
		return min + (int)(r.nextDouble() * ((max - min) + 1));
	}
}
