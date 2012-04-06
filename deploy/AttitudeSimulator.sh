

export LD_LIBRARY_PATH=/usr/lib/jni
export LD_LIBRARY_PATH=/usr/lib/jni

ECHO $LD_LIBRARY_PATH=/home/user/Desktop/j3d-1_5_2-linux-i586/lib/i386

java -cp ../../jat_src.jar:../lib/plot.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/vecmath.jar   jat.application.AttitudeSimulator.AttitudeSimulator

echo press enter

read input


