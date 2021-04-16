#!/bin/bash

for f in *.wav
do
  sox "$f" -r 44100 -e signed -b 16 -c 1 "$f"_temp.wav
  mv "$f"_temp.wav "$f"
done
