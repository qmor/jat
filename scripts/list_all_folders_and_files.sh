#!/bin/bash

find .. -not -path '*/.*/*' -not -name '.*' -not -name '*.*~' | sort >filelist.txt

echo press enter

read input

