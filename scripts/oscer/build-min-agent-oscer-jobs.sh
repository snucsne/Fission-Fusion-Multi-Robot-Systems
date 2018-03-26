#!/usr/bin/perl
use strict;
use File::Basename;

# -------------------------------------------------------------------
# Get the command line arguments
my $foldFilePrefix = shift( @ARGV );
my $runsPerFold = shift( @ARGV );
my $wallClockTime = shift( @ARGV );
my $evoProps = shift( @ARGV );
my $simProps = shift( @ARGV );
my $outputDir = shift( @ARGV );
my $outputPrefix = shift( @ARGV );
my $runID = shift( @ARGV );

# -------------------------------------------------------------------
# Get all the fold files
my ($foldPrefix, $foldDir, $ignoreFold) = fileparse( $foldFilePrefix );
opendir( DIR, $foldDir ) or die "Unable to open fold dir [$foldDir]: $!\n";
my @foldFiles = sort( grep { /$foldPrefix.*parameters$/ } readdir( DIR ) );
closedir( DIR );
my $foldFileCount = scalar @foldFiles;
#print "Found [$foldFileCount] fold files in [$foldDir] with prefix [$foldPrefix]\n";

my @boolean = ("false", "true");

# -------------------------------------------------------------------
# Build the commands
foreach my $enforceFlag (@boolean)
{
    my $shortEnforceFlag = substr( $enforceFlag, 0, 1 );
    foreach my $useIndValue (@boolean)
    {
        my $shortUseIndValue = substr( $useIndValue, 0, 1 );

        # Run through each fold
        my $currentOutputDir = $outputDir."-enforce-".$shortEnforceFlag."-use-ind-".$shortUseIndValue;
        my $seed = 0;
        for( my $i = 0; $i < $foldFileCount; $i++ )
        {
            # Get the fold file
            my $foldFile = $foldFiles[$i];

            # Multiple runs per fold
            for( my $j = 0; $j < $runsPerFold; $j++ )
            {
                my $foldRunID = sprintf( "%s%s%s%02d%02d",
                        $runID,
                        $shortEnforceFlag,
                        $shortUseIndValue,
                        $i,
                        $j );

                # Build the command
                my $cmd = "./submit-slurm-job.sh 1 $foldRunID \"$wallClockTime\" \\\n";
                $cmd .= "    /home/eskridge/fission-fusion \\\n";
                $cmd .= "    \"./evolve-min-agent-forage.sh $evoProps \\\n";
                $cmd .= "    $simProps \\\n";
                $cmd .= "    $foldDir$foldFile \\\n";
                $cmd .= "    $currentOutputDir $outputPrefix $foldRunID \\\n";
                $cmd .= "    $enforceFlag $useIndValue $seed\"\n";

                # Print it
                print $cmd,"\n";

                # Increment the random seed
                $seed++;
            }
            print "\n\n";
        }
        print "\n# -------------------------------------------------------------\n\n";
    }
}

