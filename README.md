## Synopsis

Theor is a small java library for performing experiments and analysis in 
music theory.  It provides a basic model for things like tones, scales, chords,
and so on, and permits analysis of certain tone sequences.  Theor also provides
hooks for generating MIDI from its classes.

## Getting Started

Theor is going to be primarily useful to people who have some existing knowledge of music theory.
Rather than memorizing every possible key, scale, chord, and so on, it provides a framework for
computing various musical transformations.

## Automatic Composition Examples

```
mvn exec:java -Dexec.mainClass=org.rcl.theor.composer.ProgressionComposer
```

This will create a number of `*.mid` files in the current directory.

## Unit Tests

`mvn surefire:test -Dtest=*`

## Basic Usage Examples

The following text notations generally follow the outline provided by JFugue's [MusicStrings](http://jfugue.org/jfugue-chapter2.pdf).
Numbers generally indicate octaves (middle C is referred to as C4).

Notes are pretty simple.  They contain a "tone class" (for example the note C) and an octave.  Middle C is C4.  Notes know what their 
respective MIDI numbers are, and also what frequency their sound vibrates at.

```
// Create a new A note (by default, its octave makes this A 440)
Note a = new Note(Note.A);
System.out.println(a);
System.out.println(a.getFrequency());
System.out.println(a.isSharp());
```

This code produces three lines of output:
* A4
* 440.0
* false

```
// Make a C major chord.   This is done by applying a set 
// of intervals (the major triad) to a note.
Chord c = new Chord(Note.MIDDLE_C, Interval.MAJOR_TRIAD);
```
   
Printing this chord yields the string "C4 E4 G4".

Scales are thought of as a collection of intervals.  For example, there's the abstract idea of a major scale, separate from 
D major.  In the `Scale` class, there are collections of intervals describing many common scales.  To create an actual scale,
you do this by taking a collection of intervals, and "applying" them to a particular tonic.  

```
// Make a D major scale, starting at octave 0.
NSequence ns = Scale.MAJOR.apply(new Note(Note.D, 0));
```
   
The NSequence object is just a sequence of notes.  In this case, it contains D4 E4 F#4 G4 A4 B4 C#5 D5

Theor also supports chord progressions, using a miniature language that musicians may be familiar with.  You 
can create your own chord progression using a string such as "IV - V - viidim - I" or you can use the library
of built-in chord progressions.  Again, these chord progressions are specified in the language of intervals.
To make a particular chord progression, you have to apply those collections of intervals to a particular tonic.

```
// Make a simple I, IV, V chord progression starting with F.
List<Chord> chords = ChordProgression.MI_I_IV_V.apply(new Note(Note.F, 0));
```   

This would result in three separate Chord objects:
* F4 A4 C5 
* Bb4 D5 F5 
* C5 E5 G5

By calling the `.getName()` method on the Chord objects, they would be named F, Bb, and C respectively since they are all major triads.

## Basic MIDI

Theor works with the MIDI system, either by generating the actual bytes needed for a MIDI stream, or via JFugue.  Here's a code example
on how to create a MIDI pattern of a chord progression, and play it:

```
// Create a blank MIDI pattern from JFugue.
Pattern container = new Pattern();
container.addElement(new Tempo(80));   // 80bpm.
				
// Make a simple I, IV, V chord progression, starting at F major.
List<Chord> chords = ChordProgression.MI_IV_V.apply(new Note(Note.F, 0));
for(Chord ch : chords) { 
	System.out.println("Progression:  " + ch + " => " + ch.getName());
			
	// Add this chord (played as a quarter note) to the main pattern.
	container.add(ch.toPattern(new Syncopation(Syncopation.QUARTER_NOTE)));
}
		
// Create a JFugue player, and play the resulting pattern.
Player player = new Player();
player.play(container);
		
// Save the resulting data as a MIDI file
player.saveMidi(container, new File("MyChordProgression.mid"));
```		

## FAQ

### Does Theor support temperments other than equal temperment?

No, not at the moment.  Theor assumes equal temperment, and an A that vibrates at 440Hz. 