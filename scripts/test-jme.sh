#!/bin/bash

# =========================================================
# Put all the jar files in the lib dir in the classpath
CLASSPATH=fission-fusion.jar:./:lib/:data/:cfg/
for jarFile in lib/*.jar; do
	CLASSPATH=${jarFile}:$CLASSPATH
done
for jarFile in lib/jme3/*.jar; do
	CLASSPATH=${jarFile}:$CLASSPATH
done

# =========================================================
MEMORYSIZE=1024M

java -cp $CLASSPATH \
    -Xmx$MEMORYSIZE \
    edu.snu.csne.mates.test.BasicJMEApp
