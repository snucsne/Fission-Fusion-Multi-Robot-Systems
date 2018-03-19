#!/bin/bash

# --------------------------------------------------------------
# Arguments
# 1: Job run count
# 2: Job name
# 3: Wall clock run time (hours:minutes:seconds)
# 4: Execution directory
# 5: Job command
# --------------------------------------------------------------
date=`date`

for i in `seq 1 $1`;
do
  run=`printf "%02d" $i`
  command=`echo $5`
  slurmoutput="#!/bin/bash
#
# Generated on $date
# ARGUMENTS $*
# Run: $run of $1
#
#SBATCH --partition=debug
#SBATCH --ntasks=1
#SBATCH --mem=2048
#SBATCH --output=slurm/$2_$run_%J_stdout.txt
#SBATCH --error=slurm/$2_$run_%J_stderr.txt
#SBATCH --time=$3
#SBATCH --job-name=$2
#SBATCH --mail-user=beskridge@snu.edu
#SBATCH --workdir=$4
#
#################################################
cd $4
pwd
export OSCER_RUN_NUMBER="$run"
date
time $5
date"

    filename="$2-$run.slurm"
    echo "$slurmoutput" > $filename
    sbatch $filename
#    rm $filename
    sleep 0.3
done

