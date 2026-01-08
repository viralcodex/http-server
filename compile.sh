#!/bin/bash
# Compile script for simple Java project

echo "Compiling Java files..."
javac src/main/java/com/bootdev/httpserver/*.java -d out

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed!"
    exit 1
fi