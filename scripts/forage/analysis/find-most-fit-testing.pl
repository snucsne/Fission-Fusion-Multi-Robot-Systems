#!/usr/bin/perl
use strict;

# -------------------------------------------------------------------
# Get the data directory
my $dataDir = shift( @ARGV );
if( $dataDir !~ /\/$/ )
{
    $dataDir .= "/";
}

# Get the output file
my $outputFile = shift( @ARGV );

# -------------------------------------------------------------------
# Get each of the data files
opendir( DIR, $dataDir ) or die "Unable to open data directory [$dataDir]: $!\n";
my @dataFiles = grep { /\.dat$/ } readdir( DIR );
@dataFiles = sort( @dataFiles );
closedir( DIR );

my $dataFileCount = scalar @dataFiles;
#print "Found $dataFileCount files\n";

# -------------------------------------------------------------------
my @bestFoundResults = ();

# -------------------------------------------------------------------
# Read the data files
my %data;
foreach my $dataFile (@dataFiles)
{
    # Create some handy variables
    my %current = ( 'generation' => -1,
                    'testing-fitness-mean' => -1,
                    'validation-fitness-mean' => -1,
                    'training-fitness-mean' => -1 );
    my %bestFound = ( 'generation' => -1,
                      'testing-fitness-mean' => -1,
                      'validation-fitness-mean' => -1,
                      'training-fitness-mean' => -1 );

    # Open the file
    open( DATA, "$dataDir/$dataFile" ) or die "Unable to open data file [$dataDir/$dataFile]: $!\n";

    # Read each line
    while( <DATA> )
    {
        # Before removing comments, look for the fold properties
        if( /fold-properties\ =\ (.+)$/ )
        {
            $bestFound{'fold-properties'} = $1;
        }

        # Remove any other comments or whitespace
        s/#.*//;
        s/^\s+//;
        s/\s+$//;
        next unless length;

        my ($key,$value) = split( /\s+=\s+/, $_ );

        # We are only interested in per-generation statistics
        if( $key =~ /gen\[(\d+)\]/ )
        {
            $current{'generation'} = $1;
        }
        else
        {
            next;
        }

        # Is it the testing fitness?
        if( $key =~ /testing-fitness-mean/ )
        {
            $current{'testing-fitness-mean'} = $value;
        }

        # Is it the validation fitness?
        elsif( $key =~ /validation-fitness-mean/ )
        {
            $current{'validation-fitness-mean'} = $value;
        }

        # Is it the training fitness?
        elsif( $key =~ /training-fitness-mean/ )
        {
            $current{'training-fitness-mean'} = $value;
        }

        # Is it the last statistic for the generation?
        elsif( $key =~ /best-individual-found-so-far.generation/ )
        {
            # Is this the new best?
            if( $current{'testing-fitness-mean'} > $bestFound{'testing-fitness-mean'} )
            {
                # Yup, copy over the values
                @bestFound{ keys %current } = @current{ keys %current };
            }
        }
    }

    # Clean up
    close( DATA );

    # Build the file name for the best found properties
    $dataFile =~ /(.*)\.dat/;
    my $propFile = $1."-best-found-gen-".sprintf( "%02d", $bestFound{'generation'} )."-00.properties";
    $bestFound{'solution-properties'} = $dataDir.$propFile;

    # Save the results for this file
    push( @bestFoundResults, \%bestFound );

    # Print out the best found
#    print "Best testing: Gen=[",
#            $bestGeneration,
#            "] TestingFitnessMean=[",
#            $bestTestingFitnessMean,
#            "] ValidationFitnessMean=[",
#            $bestValidationFitnessMean,
#            "] file=[",
#            $dataFile,
#            "]\n";
#    print "  props=[$dataDir/$propFile]\n";
#    print "  fold=[$foldFile]\n\n";

}

# -------------------------------------------------------------------
open( OUTPUT, "> $outputFile" ) or die "Unable to open output file [$outputFile]: $!\n";

# Print out important details
my $nowString = localtime;
print OUTPUT "# Generated on: $nowString\n";
print OUTPUT "# Data dir: $dataDir\n";
print OUTPUT "# ------------------------------------------------------------------------------\n\n";


# -------------------------------------------------------------------
my $bestFoundCount = scalar @bestFoundResults;
print OUTPUT "best-found.count = $bestFoundCount\n\n";
for( my $i = 0; $i < $bestFoundCount; $i++ )
{
    my $runStr = sprintf("%02d", $i);
    print OUTPUT "# ===== Run $runStr ===================================\n";
    my $prefix = "best-found.$runStr.";

    my %bestFound = %{ $bestFoundResults[$i] };
    foreach my $key (sort keys %bestFound)
    {
        print OUTPUT $prefix.$key." = ".$bestFound{$key}."\n";
    }
    print OUTPUT "\n";
}

# Close the output file
close( OUTPUT );
