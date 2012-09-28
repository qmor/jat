#!/bin/bash
# To run from within Eclipse, use Run->External Tools->External Tools Configurations and add this shell script.


export LD_LIBRARY_PATH=/usr/lib/jni
echo $LD_LIBRARY_PATH

#From within Eclipse:
#JARDISTPATH="/media/data/workspace/jat/dist"
#JARLIBPATH="/media/data/workspace/jat/lib"

# From command line:
JARDISTPATH="../dist"
JARLIBPATH="../lib"

pwd

java -cp $JARDISTPATH/jatapplication.jar:$JARDISTPATH/jatcore.jar:$JARLIBPATH/j3dcore.jar:$JARLIBPATH/j3dutils.jar:$JARLIBPATH/vecmath.jar:$JARLIBPATH/jdatepicker-1.3.2.jar:$JARLIBPATH/jgoodies-forms-1.5.0.jar:$JARLIBPATH/jgoodies-common-1.3.0.jar:$JARLIBPATH/opencsv-2.3.jar jat.application.missionPlan.MissionPlanMain

echo press enter

read input


