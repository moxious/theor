## Synopsis

Theor is a small java library for performing experiments and analysis in 
music theory.  It provides a basic model for things like tones, scales, chords,
and so on, and permits analysis of certain tone sequences.  Theor also provides
hooks for generating MIDI from its classes.


## Getting Started

Theor uses maven for dependencies and packaging; it has one dependency though that can't be
managed directly through maven called [JFugue](http://www.jfugue.org/).   This JAR file must
be downloaded from that site (version 4.0.3) and installed in the local maven repository with
the following command:

	mvn install:install-file -Dfile=jfugue-4.0.3.jar -DgroupId=org.jfugue -DartifactId=jfugue -Dversion=4.0.3 -Dpackaging=jar
	
Without that command, theor will not compile as the jfugue dependency will be missing.