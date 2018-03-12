#!/usr/bin/perl
use strict;
use File::Basename;

# -------------------------------------------------------------------
# Save the command as is so we can put it in the file for logging
my $commandLineArgs = join( " ", @ARGV );
my $fullCommandLine = "$0 $commandLineArgs";

# Get the command line arguments
my $foldCount = shift( @ARGV );
my $outputFilePrefix = shift(@ARGV);
my $fullAgentFilePrefix = shift( @ARGV );
my $fullPatchFilePrefix = shift( @ARGV );

# -------------------------------------------------------------------
my ($agentFilePrefix, $agentDir, $ignoreAgent) = fileparse( $fullAgentFilePrefix );
opendir( DIR, $agentDir ) or die "Unable to open agent dir [$agentDir]: $!\n";
my @agentFiles = sort( grep { /$agentFilePrefix.*parameters$/ } readdir( DIR ) );
closedir( DIR );
my $agentFileCount = scalar @agentFiles;
print "Found [$agentFileCount] agent files in [$agentDir]\n";

#print join( ", ", @agentFiles );
#print "\n\n";

# -------------------------------------------------------------------
my ($patchFilePrefix, $patchDir, $ignorePatch) = fileparse( $fullPatchFilePrefix );
opendir( DIR, $patchDir ) or die "Unable to open patch dir [$patchDir]: $!\n";
my @patchFiles = sort( grep { /$patchFilePrefix.*parameters$/ } readdir( DIR ) );
closedir( DIR );
my $patchFileCount = scalar @patchFiles;
print "Found [$patchFileCount] patch files in [$patchDir]\n";

#print join( ", ", @patchFiles );
#print "\n\n";

# -------------------------------------------------------------------
# Ensure the array lengths match
if( $agentFileCount > $patchFileCount )
{
#    $#agentFiles = $patchFileCount;
    splice( @agentFiles, $patchFileCount);
    $agentFileCount = (scalar @agentFiles);
}
elsif( $patchFileCount > $agentFileCount )
{
    $#patchFiles = $agentFileCount;
    $patchFileCount = (scalar @patchFiles);
}

# -------------------------------------------------------------------
# Build the folds
my $itemsPerFold = $agentFileCount / $foldCount;
my @agentFileFolds = ();
my @patchFileFolds = ();
my @currentAgentFold = ();
my @currentPatchFold = ();
for( my $i = 0; $i < $agentFileCount; $i++ )
{
    # Add to the current folds
    push( @currentAgentFold, $agentDir."/".$agentFiles[$i] );
    push( @currentPatchFold, $patchDir."/".$patchFiles[$i] );

    # Have we reached the limit?
    if( $itemsPerFold <= (scalar @currentAgentFold) )
    {
        # Yup, store them
        push( @agentFileFolds, [ @currentAgentFold ] );
        push( @patchFileFolds, [ @currentPatchFold ] );

        # Reset the current folds
        @currentAgentFold = ();
        @currentPatchFold = ();
    }
}

# -------------------------------------------------------------------
# Build the files describing the folds
for( my $i = 0; $i < $foldCount; $i++ )
{
    # Build the output file name
    my $outputFile = sprintf( "%s-fold-%02d.parameters", $outputFilePrefix, $i );

    #print "$outputFile\n";

    # Open the output file and add the header
    open( OUTPUT, "> $outputFile" ) or die "Unable to open [$outputFile]: $!\n";
    print OUTPUT "# -------------------------------------------------------------------\n";
    print OUTPUT "# $fullCommandLine\n";
    print OUTPUT "# ".localtime."\n";
    print OUTPUT "#   foldCount=[$foldCount]\n";
    print OUTPUT "#   outputFilePrefix=[$outputFilePrefix]\n";
    print OUTPUT "#   agentFiles=[",join( " ", @agentFiles),"]\n";
    print OUTPUT "#   agentFileCount=[$agentFileCount]\n";
    print OUTPUT "#   patchFiles=[",join( " ", @patchFiles),"]\n";
    print OUTPUT "#   patchFileCount=[$patchFileCount]\n";
    print OUTPUT "#   itemsPerFold=[$itemsPerFold]\n";
    print OUTPUT "# -------------------------------------------------------------------\n";
    print OUTPUT "# Fold [".sprintf( "%02d", $i )."] of [".sprintf( "%02d", $foldCount )."]\n\n";

    # Build the training data
    my $trainingAgentFiles = "";
    my $trainingPatchFiles = "";
    my $foldIDX = 0;
    for( my $j = 0; $j < ($foldCount - 2); $j++ )
    {
        # Compute the current index into the locations folds
        $foldIDX = ($i + $j) % $foldCount;

        if( 0 < length($trainingAgentFiles) )
        {
            $trainingAgentFiles .= " ";
            $trainingPatchFiles .= " ";
        }
        $trainingAgentFiles .= join( " ", @{@agentFileFolds[$foldIDX]} );
        $trainingPatchFiles .= join( " ", @{@patchFileFolds[$foldIDX]} );
    }
    print OUTPUT "training.agent.prop-files   = $trainingAgentFiles\n";
    print OUTPUT "training.patch.prop-files   = $trainingPatchFiles\n\n";

    # Build the testing ata
    $foldIDX = ($foldIDX + 1) % $foldCount;
#    print( "Testing data at fold [$foldIDX]\n" );
    print OUTPUT "testing.agent.prop-files    = ".join( " ", @{@agentFileFolds[$foldIDX]} )."\n";
    print OUTPUT "testing.patch.prop-files    = ".join( " ", @{@patchFileFolds[$foldIDX]} )."\n\n";

    # Build the validation data
    $foldIDX = ($foldIDX + 1) % $foldCount;
#    print( "Validation data at fold [$foldIDX]\n" );
    print OUTPUT "validation.agent.prop-files = ".join( " ", @{@agentFileFolds[$foldIDX]} )."\n";
    print OUTPUT "validation.patch.prop-files = ".join( " ", @{@patchFileFolds[$foldIDX]} )."\n";

    close( OUTPUT );
}

