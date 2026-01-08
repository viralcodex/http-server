#!/bin/bash
# Compile script for simple Java project

echo "Compiling Java files..."
javac -d out src/main/java/com/bootdev/**/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed!"
    exit 1
fi