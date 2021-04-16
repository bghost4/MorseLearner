#!/bin/bash

for f in *.wav
do
  sox -r 44100 -e signed -b 16 -c 1 "$f" tmp.wav
  mv tmp.wav "$f"
done
