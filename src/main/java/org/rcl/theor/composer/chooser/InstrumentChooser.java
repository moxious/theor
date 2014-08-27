package org.rcl.theor.composer.chooser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rcl.theor.composer.DataFeed;
import org.rcl.theor.midi.InstrumentBank;

public class InstrumentChooser extends Chooser {
	public static List<Byte> harmonyInstruments = makeList(new Object[] {
		InstrumentBank.PIANO_FAMILY, InstrumentBank.GUITAR_FAMILY, 
		InstrumentBank.ENSEMBLE_FAMILY, 
		//InstrumentBank.SYNTH_PAD_FAMILY,
		InstrumentBank.ORGAN_FAMILY, InstrumentBank.STRINGS_FAMILY
	});
	
	public static List<Byte> melodyInstruments = makeList(new Object[] {
			InstrumentBank.PIANO_FAMILY, InstrumentBank.GUITAR_FAMILY, 
			InstrumentBank.BRASS_FAMILY, InstrumentBank.ORGAN_FAMILY, 
			InstrumentBank.PIPE_FAMILY, InstrumentBank.REED_FAMILY, 
			InstrumentBank.STRINGS_FAMILY, 
			//InstrumentBank.SYNTH_LEAD_FAMILY
		});
	
	public enum InstrumentFunction { HARMONY, MELODY };	
	protected InstrumentFunction func; 
	
	
	public InstrumentChooser(InstrumentFunction func) { 
		this.func = func;
	}
	
	/*
	public static final byte[] validInstruments = new byte[] {
			Instrument.ACCORDIAN, Instrument.ACOUSTIC_BASS, Instrument.ACOUSTIC_GRAND, 
			Instrument.PIANO, Instrument.GUITAR, Instrument.ALTO_SAX, Instrument.ATMOSPHERE,
			Instrument.BASSOON, Instrument.CELLO, Instrument.CHOIR, Instrument.ELECTRIC_PIANO,
			Instrument.
	};
	*/
	
	public Byte generateConstraint(DataFeed f) {
		byte b = f.next();		
		List<Byte> chooseFrom = (func == InstrumentFunction.HARMONY ? harmonyInstruments : melodyInstruments);
		int x = Math.abs(b) % chooseFrom.size();  		
		//System.out.println("Byte " + b + " chooses instrument " + Instrument.INSTRUMENT_NAME[i]);
		return chooseFrom.get(x);
	}
	
	private static final List<Byte> makeList(Object[]objs) { 
		ArrayList<Byte>list = new ArrayList<Byte>();				
		for(Object set : objs) list.addAll((Set<Byte>)set); 		
		return list;
	}
}
