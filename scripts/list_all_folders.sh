#!/bin/bash

find .. -not -path '*/.*/*' -not -name '*.*' | sort >folderlist.txt

echo press enter

read input

