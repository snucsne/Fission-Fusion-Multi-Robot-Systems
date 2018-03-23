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
my $minPredProb = shift( @ARGV );
my $maxPredProb = shift( @ARGV );
my $predProbStep = shift( @ARGV );

# -------------------------------------------------------------------
# Get all the fold files
my ($foldPrefix, $foldDir, $ignoreFold) = fileparse( $foldFilePrefix );
opendir( DIR, $foldDir ) or die "Unable to open fold dir [$foldDir]: $!\n";
my @foldFiles = sort( grep { /$foldPrefix.*parameters$/ } readdir( DIR ) );
closedir( DIR );
my $foldFileCount = scalar @foldFiles;
#print "Found [$foldFileCount] fold files in [$foldDir] with prefix [$foldPrefix]\n";

# -------------------------------------------------------------------
# Build the commands
for( my $predProb = $minPredProb; $predProb <= $maxPredProb; $predProb += $predProbStep )
{
    my $predProbStr = sprintf( "pred%05.3f", $predProb );
    $predProbStr =~ s/0\.//g;
    my $predOutputPrefix = $outputPrefix."-".$predProbStr;
    my $predOutputDir = $outputDir."-".$predProbStr;
    my $seed = 0;
    for( my $i = 0; $i < $foldFileCount; $i++ )
    {
        # Get the fold file
        my $foldFile = $foldFiles[$i];

        # Multiple runs per fold
        for( my $j = 0; $j < $runsPerFold; $j++ )
        {
            my $foldRunID = sprintf( "%s%s%02d%02d",
                    $runID,
                    $predProbStr,
                    $i,
                    $j );

            # Build the command
            my $cmd = "./submit-slurm-job.sh 1 $foldRunID \"$wallClockTime\" \\\n";
            $cmd .= "    /home/eskridge/fission-fusion \\\n";
            $cmd .= "    \"./evolve-predation-forage.sh $evoProps \\\n";
            $cmd .= "    $simProps \\\n";
            $cmd .= "    $foldDir$foldFile \\\n";
            $cmd .= "    $predOutputDir $outputPrefix $foldRunID $predProb $seed\"\n";

            # Print it
            print $cmd,"\n";

            # Increment the random seed
            $seed++;
        }
        print "\n";
    }
    print "\n# -------------------------------------------------------------\n\n";
}

