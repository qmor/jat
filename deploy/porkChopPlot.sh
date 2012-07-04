#!/bin/bash
# To run from within Eclipse, use Run->External Tools->External Tools Configurations and add this shell script.


export LD_LIBRARY_PATH=/usr/lib/jni

echo $LD_LIBRARY_PATH

JARDISTPATH="/media/data/workspace/jat/dist"
JARLIBPATH="/media/data/workspace/jat/lib"

pwd

#=/home/user/Desktop/j3d-1_5_2-linux-i586/lib/i386


#java -cp ../dist/jatapplication.jar:../dist/jatcore.jar:../lib/plot.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/vecmath.jar:../lib/jdatepicker-1.3.2.jar   jat.application.porkChopPlot.pcplot_Jat3D_main

#From within Eclipse:
java -cp $JARDISTPATH/jatapplication.jar:$JARDISTPATH/jatcore.jar:$JARLIBPATH/j3dcore.jar:$JARLIBPATH/j3dutils.jar:$JARLIBPATH/vecmath.jar:$JARLIBPATH/jdatepicker-1.3.2.jar   jat.application.porkChopPlot.pcplot_Jat3D_main

echo press enter

read input


