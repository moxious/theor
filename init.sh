#!/bin/sh

aplaymidi -l
aoss java MidiClassTest

modprobe snd-dummy ; modprobe snd-pcm-oss ; modprobe snd-mixer-oss ; modprobe snd-seq-oss

java -jar theor-0.0.2-jar-with-dependencies.jar
ls *.mid
