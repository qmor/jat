#!/bin/bash


echo Before Clean
find .. -name "*.class"

find .. -name "*.class" -exec rm -rf {} \;

echo After Clean
find .. -name "*.class"


echo press enter

read input



