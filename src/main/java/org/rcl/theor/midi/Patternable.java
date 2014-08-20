package org.rcl.theor.midi;

import org.jfugue.Pattern;
import org.rcl.theor.Syncopation;

/**
 * An interface for objects that can be represented as JFugue MIDI patterns.
 * @author DMALLEN
 */
public interface Patternable {
	public Pattern toPattern(Syncopation sync);
}
