#!/usr/bin/perl
# -------------------------------------------------------------------
use strict;
use POSIX;
use Math::Trig;

# -------------------------------------------------------------------
use constant TWO_PI => 2 * pi;

# -------------------------------------------------------------------
# Get the command line arguments
my $filePrefix = shift(@ARGV);
my $fileCount = shift(@ARGV);
my $minPatchCount = shift(@ARGV);
my $maxPatchCount = shift(@ARGV);
my $minDistance = shift(@ARGV);
my $maxDistance = shift(@ARGV);
my $minPredationProb = shift(@ARGV);
my $maxPredationProb = shift(@ARGV);

# -------------------------------------------------------------------
# Build the files
for( my $i = 0; $i < $fileCount; $i++ )
{
    # Get the random number generator

    # How many patches are in this file?
    my $patchDiff = $maxPatchCount - $minPatchCount;
    my $filePatchCount = 1 * $patchDiff + $minPatchCount;

    # Build the patche locations
    my @patchLocations;
    my $currentPatch = 0;
    while( $currentPatch < ($filePatchCount - 1) )
    {

        $currentPatch++;
    }
}
