#!/bin/bash

ffmpeg -f concat -i demux.txt -i testing.mp3 -vsync vfr -pix_fmt yuv420p  output.mp4