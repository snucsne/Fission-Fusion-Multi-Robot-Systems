#!/bin/bash

# ===================================================
# Get the command line arguments
EVOPROPS=$1
SIMPROPS=$2
FOLDPROPS=$3
DIR=$4
OUTPUTPREFIX=$5
RUNID=$6
PREDPROB=$7
SEED=$8

# ===================================================
# Is there an OSCER run number?
RUNID=$RUNID${OSCER_RUN_NUMER:-}

# Build the directory if it doesn't exist
if [ ! -d "$DIR" ]; then
    mkdir -p $DIR
fi

# ===================================================

./evolve.sh --params $EVOPROPS \
        --seed $SEED \
        --logprefix $RUNID \
        --runid $RUNID \
        --jargs -Dsim-properties=$SIMPROPS \
        --jargs -Dpursuit-predation-probability=$PREDPROB \
        --ecargs "-p eval.problem.fold-properties=$FOLDPROPS" \
        --statfile $PWD/$DIR/$OUTPUTPREFIX-$RUNID.dat > $DIR/$OUTPUTPREFIX-$RUNID.out 2>&1

