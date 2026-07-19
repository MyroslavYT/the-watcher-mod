# THE WATCHER — Minecraft Horror Mod
## Fabric 1.20.1

---

### HOW TO BUILD AND INSTALL

**Requirements:** Java 17, Git

1. Open a terminal in the `the-watcher/` folder
2. Run: `./gradlew build`   (Mac/Linux) or `gradlew.bat build` (Windows)
3. Find the compiled mod at: `build/libs/the-watcher-1.0.0.jar`
4. Drop that `.jar` into your `.minecraft/mods/` folder
5. Also install **Fabric Loader 0.14.22** and **Fabric API 0.92.0+1.20.1**

---

### REPLACING SOUNDS

Minecraft only supports **.ogg** format.  
To convert your MP3 / MP4 / WAV files, use the included scripts:

**Windows:** Run `convert_audio.bat` — drag your file onto it  
**Mac/Linux:** Run `./convert_audio.sh yourfile.mp3`

Requires **ffmpeg** installed. Download free at: https://ffmpeg.org

Then rename your converted file to match exactly:
```
ambient_scream.ogg      ← screaming/horror ambience
breathing.ogg           ← faint breathing sound
footsteps_echo.ogg      ← echo of footsteps
jumpscare_sting.ogg     ← loud sharp jumpscare sound
world_red_ambience.ogg  ← plays during red world event
disc_distorted.ogg      ← the cursed music disc
```
Drop them into: `src/main/resources/assets/thewatcher/sounds/`  
Then rebuild.

---

### REPLACING TEXTURES

All textures are standard **.png** files. Drop yours in and rebuild.

```
textures/entity/watcher/watcher.png   ← The Watcher's skin (64x64 PNG, same layout as player skin)
textures/gui/jumpscare.png            ← Full-screen jumpscare image (any size PNG)
textures/gui/skull_painting.png       ← Replaces all paintings during corruption (32x32 PNG)
```

---

### WHAT THE MOD DOES

See the full concept document from your conversation.  
Key events timeline: Day 3 start → Day 7 finale → Kick from server.
