#!/bin/bash

# =========================================================
# Get the min neighbor separation
MINSEP=$1

# Get the max position radius
#MAXRADIUS=$2

# Get the file prefix
FILEPREFIX=$2

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

# =========================================================
for AGENTCOUNT in $(seq 10 10 100)
do

    FORMATTEDAGENTCOUNT=$(printf "%03d" $AGENTCOUNT)
    FORMATTEDFILEPREFIX="$FILEPREFIX-agents-$FORMATTEDAGENTCOUNT-seed"

    MAXRADIUS=$(echo "$MINSEP * sqrt( $AGENTCOUNT / 2 )" | bc -l)

    for SEED in $(seq 0 1 50)
    do
        FORMATTEDSEED=$(printf "%03d" $SEED)
        FILENAME="$FORMATTEDFILEPREFIX-$FORMATTEDSEED.parameters"

        # Run the builder
        java -cp $CLASSPATH \
            -Xmx$MEMORYSIZE \
            -server \
            -Dhostname=$HOST \
            -Dlog4j.configurationFile=$LOG_CONFIG \
            edu.snu.csne.forage.util.AgentPositionGenerator \
            $AGENTCOUNT \
            $MINSEP \
            $MAXRADIUS \
            $SEED \
            $FILENAME

    done

done

