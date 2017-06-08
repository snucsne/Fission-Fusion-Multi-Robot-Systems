#!/bin/bash

# =========================================================
# Default values
LOG_CONFIG=log/log4j-config-debug.xml
MEMORYSIZE=1024M
SEED=42


# =========================================================
# Get the host
if [ -z $HOST ]; then
  HOST=$HOSTNAME
fi

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

java -cp $CLASSPATH \
    -Xmx$MEMORYSIZE \
    -Dhostname=$HOST \
    -Dlog4j.configurationFile=$LOG_CONFIG \
    -Drandom-seed=$SEED \
    edu.snu.csne.mates.sim.Simulator
