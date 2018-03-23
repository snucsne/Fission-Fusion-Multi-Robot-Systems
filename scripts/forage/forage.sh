#!/bin/bash

# =========================================================
SIMPROPS=$1
AGENTPROPS=$2
PATCHPROPS=$3
CALCPROPS=$4

# =========================================================
# Get the host
if [ -z $HOST ]; then
  HOST=$HOSTNAME
fi

# Default values
LOG_CONFIG=log/log4j-config.xml
MEMORYSIZE=1024M

# =========================================================
# Put all the jar files in the lib dir in the classpath
CLASSPATH=fission-fusion.jar:./:lib/:data/:cfg/
for jarFile in lib/*.jar; do
	CLASSPATH=${jarFile}:$CLASSPATH
done
for jarFile in lib/jme3/*.jar; do
	CLASSPATH=${jarFile}:$CLASSPATH
done

java -cp $CLASSPATH \
        -Xmx$MEMORYSIZE \
        -server \
        -Dhostname=$HOST \
        -Dlog4j.configurationFile=$LOG_CONFIG \
        -Dsim-properties=$SIMPROPS \
        -Dagent-properties-file=$AGENTPROPS \
        -Dpatch-properties-file=$PATCHPROPS \
        -Ddecision-calc-props-file=$CALCPROPS \
        edu.snu.csne.forage.Simulator

