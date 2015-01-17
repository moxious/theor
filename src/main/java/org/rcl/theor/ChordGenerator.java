package org.rcl.theor;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.rcl.theor.note.Note;

public class ChordGenerator {
	public static final Integer [] toneClasses = new Integer[] {
		Note.C, Note.CS, Note.D, Note.DS, Note.E, Note.F, 
		Note.FS, Note.G, Note.GS, Note.A, Note.AS, Note.B
	};

	public static void main(String [] args) throws Exception { 
		ICombinatoricsVector<Integer> originalVector = Factory.createVector(toneClasses);
		Generator<Integer> gen = Factory.createSimpleCombinationGenerator(originalVector, 3);
		for (ICombinatoricsVector<Integer> combination : gen) {
			System.out.println(combination);
		}
	} // End main
}
