#!/bin/bash
# THE WATCHER - Audio Converter (Mac/Linux)
# Usage: ./convert_audio.sh yourfile.mp3
if [ -z "$1" ]; then
  echo "Usage: ./convert_audio.sh <audiofile>"
  echo "Converts any audio to .ogg for Minecraft"
  exit 1
fi
INPUT="$1"
OUTPUT="${INPUT%.*}.ogg"
ffmpeg -i "$INPUT" -c:a libvorbis -q:a 4 "$OUTPUT"
echo ""
echo "Done! Rename $OUTPUT to one of these and drop in sounds/:"
echo "  ambient_scream.ogg"
echo "  breathing.ogg"
echo "  footsteps_echo.ogg"
echo "  jumpscare_sting.ogg"
echo "  world_red_ambience.ogg"
echo "  disc_distorted.ogg"
