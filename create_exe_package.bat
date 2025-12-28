@echo off
echo Project build...
call mvn clean package

echo.
echo Creating EXE using jpackage...

mkdir target\jpackage-input 2>nul
copy target\Abstract_Machine_Project-1.0-SNAPSHOT-jar-with-dependencies.jar target\jpackage-input\ 1>nul

call mvn jpackage:jpackage ^
  --name "FSM-Simulator" ^
  --input target/jpackage-input ^
  --main-jar Abstract_Machine_Project-1.0-SNAPSHOT-jar-with-dependencies.jar ^
  --main-class app.abstract_automaton_project.ConsoleApplication ^
  --type exe ^
  --win-console ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --app-version 1.0 ^
  --vendor "Daniil4639" ^
  --copyright "Copyright 2025" ^
  --description "Final State Machine Simulator" ^
  --dest target/dist

echo.
echo Ready! EXE file: target/dist/FSM-Simulator.exe
pause