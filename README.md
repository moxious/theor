## Synopsis

Theor is a small java library for performing experiments and analysis in 
music theory.  It provides a basic model for things like tones, scales, chords,
and so on, and permits analysis of certain tone sequences.  Theor also provides
hooks for generating MIDI from its classes.

## Getting Started

Theor is going to be primarily useful to people who have some existing knowledge of music theory.
Rather than memorizing every possible key, scale, chord, and so on, it provides a framework for
computing various musical transformations.

### Dependencies

Theor uses maven for dependencies and packaging; it has one dependency though that can't be
managed directly through maven called [JFugue](http://www.jfugue.org/).   This JAR file must
be downloaded from that site (version 4.0.3) and installed in the local maven repository with
the following command:

	mvn install:install-file -Dfile=jfugue-4.0.3.jar -DgroupId=org.jfugue \
	    -DartifactId=jfugue -Dversion=4.0.3 -Dpackaging=jar
	
Without that command, theor will not compile as the jfugue dependency will be missing.

## Simple Usage Examples

The following text notations generally follow the outline provided by JFugue's [MusicStrings](http://jfugue.org/jfugue-chapter2.pdf).
Numbers generally indicate octaves (middle C is referred to as C4).

```
// Make a C major chord.   This is done by applying a set 
// of intervals (the major triad) to a note.
Chord c = new Chord(Note.MIDDLE_C, Interval.MAJOR_TRIAD);
```
   
Printing this chord yields the string "C4 E4 G4".

```
// Make a D major scale, starting at octave 0.
NSequence ns = Scale.MAJOR.apply(new Note(Note.D, 0));
```
   
The NSequence object is just a sequence of notes.  In this case, it contains D4 E4 F#4 G4 A4 B4 C#5 D5

```
// Make a simple I, IV, V chord progression starting with F.
List<Chord> chords = ChordProgression.MI_I_IV_V.apply(new Note(Note.F, 0));
```   

This would result in three separate Chord objects:
* F4 A4 C5 
* Bb4 D5 F5 
* C5 E5 G5

By calling the `.getName()` method on the Chord objects, they would be named F, Bb, and C respectively since they are all major triads. 