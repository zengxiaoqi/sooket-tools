@echo off
set "JAVA_CMD=%JAVA_HOME%/bin/java"
if "%JAVA_HOME%" == "" goto noJavaHome
if exist "%JAVA_HOME%\bin\java.exe" goto mainEntry
:noJavaHome
echo ---------------------------------------------------
echo WARN: JAVA_HOME environment variable is not set. 
echo ---------------------------------------------------
set "JAVA_CMD=java"
:mainEntry


@echo on
call stop_ide.bat 2>stdout.log

echo stop over

"%JAVA_CMD%" -jar socket-tools-0.0.1-SNAPSHOT.jar >>stdout.log