#!/usr/bin/perl
use strict;
use List::Util qw[min max];

# =========================================================
my $resourcesInitial = shift( @ARGV );
my $areaPatch = shift( @ARGV );
my $agentCount = shift( @ARGV );
my $consumptionRateMax = shift( @ARGV );
my $areaForaging = shift( @ARGV );
my $maxTimesteps = shift( @ARGV );


# =========================================================
my $nowString = localtime;
print "# Time ............. [$nowString]\n";
print "# resourcesInitial . [$resourcesInitial]\n";
print "# areaPatch ........ [$areaPatch]\n";
print "# agentCount ....... [$agentCount]\n";
print "# consumptionRateMax [$consumptionRateMax]\n";
print "# areaForaging ..... [$areaForaging]\n";
print "# maxTimesteps ..... [$maxTimesteps]\n";
print "# =========================================================\n\n";

printf( "# Time    Density    A_Eff      R_ga       R_gt       Remain     Foraged    Percentage\n" );
printf( "%04d      %07.3f    %07.3f    %07.3f    %07.3f    %07.3f    %07.3f    %07.3f\n",
        0,
        0,
        0,
        0,
        0,
        $resourcesInitial,
        0,
        0 );

# =========================================================
my $resourcesRemaining = $resourcesInitial;
my $resourcesForaged = 0;
for( my $i = 1; $i < $maxTimesteps; $i++ )
{
    my $resourceDensity = $resourcesRemaining / $areaPatch;
    my $areaForagingEffective = min( $areaForaging, $areaPatch / $agentCount );
    my $resourcesGainedPerAgent = min( $consumptionRateMax,
            $areaForagingEffective * $resourceDensity );
    my $totalResourcesGained = $resourcesGainedPerAgent * $agentCount;
    $resourcesRemaining -= $totalResourcesGained;
    $resourcesForaged += $totalResourcesGained;
    my $percentageOfTotalForaged = $resourcesForaged / $resourcesInitial;

    printf( "%04d      %07.3f    %07.3f    %07.3f    %07.3f    %07.3f    %07.3f    %07.5f\n",
            $i,
            $resourceDensity,
            $areaForagingEffective,
            $resourcesGainedPerAgent,
            $totalResourcesGained,
            $resourcesRemaining,
            $resourcesForaged,
            $percentageOfTotalForaged );
}

