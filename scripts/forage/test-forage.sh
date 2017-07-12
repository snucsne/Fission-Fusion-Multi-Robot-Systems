#!/bin/bash

# =========================================================


# =========================================================
# Get the host
if [ -z $HOST ]; then
  HOST=$HOSTNAME
fi

# Default values
LOG_CONFIG=log/log4j-config-debug.xml
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
        -Dsim-properties=cfg/forage/forage-example.parameters \
        edu.snu.csne.forage.Simulator

