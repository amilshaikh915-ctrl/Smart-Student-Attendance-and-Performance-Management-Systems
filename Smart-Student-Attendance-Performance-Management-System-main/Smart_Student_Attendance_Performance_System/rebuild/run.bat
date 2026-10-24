@echo off
setlocal
cd /d "%~dp0"
if not exist out mkdir out
where javac >nul 2>nul
if errorlevel 1 (
  echo JDK not found. Install JDK 17 or later and ensure javac is on PATH.
  pause
  exit /b 1
)
echo Compiling SmartCampus...
javac -encoding UTF-8 -d out src\main\java\com\smartcampus\Main.java
if errorlevel 1 (
  echo Compilation failed. Check the error messages above.
  pause
  exit /b 1
)
java -cp out com.smartcampus.Main
