#!/bin/bash

for f in *.wav
do
  sox -r 44100 -e signed -b 16 -c 1 "$f" "$f_temp".wav
  mv tmp.wav "$f"
done
