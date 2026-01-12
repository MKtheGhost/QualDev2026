@echo off
echo Compilation du projet BRi...
if not exist bin mkdir bin
javac -d bin -sourcepath src src/brilaunch/*.java src/exemple/*.java
if %errorlevel% == 0 (
    echo Compilation reussie!
) else (
    echo Erreur de compilation
)
pause
