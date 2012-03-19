#!/bin/bash


echo Before Clean
find .. -name ".svn"

find .. -name ".svn" -exec rm -rf {} \;

echo After Clean
find .. -name ".svn"


echo press enter

read input



