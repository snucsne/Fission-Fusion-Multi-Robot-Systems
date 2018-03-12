#!/bin/bash

seed=42
runid=forage-test
rundir=$PWD/data/$runid

mkdir $rundir

./evolve.sh --params cfg/forage/forage-evolve-example.parameters \
        --seed $seed \
        --logprefix forage-text \
        --runid $runid \
        --jargs -Dsim-properties=cfg/forage/forage-headless-sim.parameters \
        --statfile $rundir/forage-test.dat > $rundir/forage-test.out 2>&1

