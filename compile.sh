#!/bin/bash
echo "Compilation du projet BRi..."
if [ ! -d "bin" ]; then
    mkdir bin
fi
javac -d bin -sourcepath src src/brilaunch/*.java src/services/*.java
if [ $? -eq 0 ]; then
    echo "Compilation reussie!"
else
    echo "Erreur de compilation"
fi
