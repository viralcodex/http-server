#!/bin/bash
# Compile script for simple Java project

echo "Compiling Java files..."

find src/main/java -name "*.java" -exec javac -d out {} +

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed!"
    exit 1
fi