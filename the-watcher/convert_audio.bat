@echo off
echo THE WATCHER - Audio Converter
echo Converts any audio file to OGG for Minecraft
echo.
echo Drag your audio file onto this script, OR run:
echo   convert_audio.bat yourfile.mp3
echo.
if "%~1"=="" (echo No file provided. & pause & exit)
set INPUT=%~1
set OUTPUT=%~n1.ogg
ffmpeg -i "%INPUT%" -c:a libvorbis -q:a 4 "%OUTPUT%"
echo.
echo Done! Rename %OUTPUT% to one of these and put it in sounds/:
echo   ambient_scream.ogg
echo   breathing.ogg
echo   footsteps_echo.ogg
echo   jumpscare_sting.ogg
echo   world_red_ambience.ogg
echo   disc_distorted.ogg
pause
