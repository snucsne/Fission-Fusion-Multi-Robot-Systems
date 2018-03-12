/*
 *  The Fission-Fusion in Multi-Robot Systems Toolkit is open-source
 *  software for for investigating fission-fusion processes in
 *  multi-robot systems.
 *  Copyright (C) 2017 Southern Nazarene University
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.snu.csne.forage.evolve;

//Imports
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.Statistics;
import ec.Subpopulation;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class ParseableStatistics extends Statistics
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            ParseableStatistics.class.getName() );
    
    /** Default serial version uid */
    private static final long serialVersionUID = 1L;

    /** Do we compress the output? */
    public static final String P_COMPRESS = "gzip";

    /** Statistics log file parameter */
    public static final String P_STATISTICS_FILE = "file";

    /** Random seed properties key */
    public static final String _RANDOM_SEED_KEY = "random-seed";

    /** System newline */
    public static final String NEWLINE = System.getProperty( "line.separator" );

    /** Our decimal formatter for 2 digits */
    protected static final DecimalFormat _2_DIGIT_FORMATTER =
            new DecimalFormat( "00" );

    /** Our decimal formatter for 5 digits */
    protected static final DecimalFormat _5_DIGIT_FORMATTER =
            new DecimalFormat( "00000" );

    /** The Statistics' log */
    protected int _statLog = 0;

    /** The number of sub-populations */
    protected int _subPopCount = 0;

    /** The best individual found in a run */
    protected Individual[] _bestFound = null;

    /** The start time of the job */
    protected long _jobStartTime;

    /** The generation that the best individual was found in */
    protected int[] _bestFoundGen = null;

   /** The current amount of time spent in breeding */
    protected long _breedTotalTime = 0L;

    /** The start time of the last breeding phase */
    protected long _breedStartTime;

   /** The current amount of time spent in evaluation */
    protected long _evalTotalTime = 0L;

    /** The start time of the last evaluation phase */
    protected long _evalStartTime;

    /** The directory of the stat file */
    protected String _statDir = null;

    
    /**
     * Sets up this statistics object.
     *
     * @param state The current state of evolution
     * @param base The parameter base
     * @see ec.Statistics#setup(ec.EvolutionState, ec.util.Parameter)
     */
    @Override
    public void setup( EvolutionState state, Parameter base )
    {
        _LOG.trace( "Entering setup( state, base )" );

        // Call the superclass impl
        super.setup( state, base );

        // Get our statistics file
        File statFile = state.parameters.getFile( base.push( P_STATISTICS_FILE ),
                null );
        _LOG.info( "Using statFile=[" + statFile + "]" );
        _statDir = statFile.getParent();

        // Add it as our log
        if( null != statFile )
        {
            try
            {
                // Do we compress?
                boolean compress = state.parameters.getBoolean(
                        base.push(P_COMPRESS),
                        null,
                        false );

                if( compress )
                {
                    _statLog = state.output.addLog( statFile,
                            false,
                            compress );
                }
                else
                {
                    _statLog = state.output.addLog( statFile,
                            false );
                }
            }
            catch( IOException ioe )
            {
                // Oops, something went foobar
                state.output.fatal( "Error creating log["
                        + statFile
                        + "]: "
                        + ioe );
            }
            
            _LOG.info( "Statistics: _statLog=[" + _statLog + "]" );
        }

        _LOG.trace( "Leaving setup( state, base )" );
    }

    /**
     * Called immediately after initialization.
     *
     * @param state The current state of evolution
     * @see ec.Statistics#postInitializationStatistics(ec.EvolutionState)
     */
    @Override
    public void postInitializationStatistics( EvolutionState state )
    {
        // Call the superclass implementation
        super.postInitializationStatistics( state );

        // Display the parameters
        String newline = System.getProperty("line.separator");
        StringBuilder paramBuilder = new StringBuilder();
        paramBuilder.append( "# =========================================================" );
        paramBuilder.append( newline );
        paramBuilder.append( "# Evolution parameters" );
        paramBuilder.append( newline );
        paramBuilder.append( newline );
        paramBuilder.append( "# " );
        StringWriter writer = new StringWriter();
        state.parameters.list( new PrintWriter( writer) );
        String paramStr = writer.toString();
        paramBuilder.append( paramStr.replaceAll("\n", "\n# ") );
        paramBuilder.append( newline );
        paramBuilder.append( "# =========================================================" );
        paramBuilder.append( newline );
        println( paramBuilder.toString(),
                state,
                false );
        
        // Display the start time
        println( "start-time = " + (new Date()), state, false );

        // Set up our best of job data
        _subPopCount = state.population.subpops.length;
        _bestFound = new Individual[ _subPopCount ];
        _bestFoundGen = new int[ _subPopCount ];

        // Find out our population size
        long popSize = 0L;
        for( int i = 0; i < _subPopCount; i++ )
        {
            popSize += state.population.subpops[i].individuals.length;
        }
        println( "popsize = " + popSize, state, false );

        // Display the total max number of generations for this run
        // (including the initial)
        println( "max-generations = " + state.numGenerations, state, false );

        // See if the seed was saved for us
        String randSeedStr = System.getProperty( _RANDOM_SEED_KEY );
        if( null != randSeedStr )
        {
            println( "random-seed = " + randSeedStr, state, false );
        }

        // Get the start time
        _jobStartTime = System.currentTimeMillis();

        state.output.flush();

    }

    /**
     * Called immediate before breeding occurs
     *
     * @param state The current state of evolution
     * @see ec.Statistics#preBreedingStatistics(ec.EvolutionState)
     */
    @Override
    public void preBreedingStatistics( EvolutionState state )
    {
        // Call the superclass impl
        super.preBreedingStatistics( state );

        // Get the breeding start time
        _breedStartTime = System.currentTimeMillis();
    }

    /**
     * Called immediate after breeding occurs
     *
     * @param state The current state of evolution
     * @see ec.Statistics#postPreBreedingExchangeStatistics(ec.EvolutionState)
     */
    @Override
    public void postPreBreedingExchangeStatistics( EvolutionState state )
    {
        // Call the superclass impl
        super.postBreedingStatistics( state );

        // Find out how much time we spent breeding
        long breedTime = ( System.currentTimeMillis() - _breedStartTime );
        _breedTotalTime += breedTime;
    }

    /**
     * Called immediately before evaluation occurs.
     *
     * @param state The current state of evolution
     * @see ec.Statistics#preEvaluationStatistics(ec.EvolutionState)
     */
    @Override
    public void preEvaluationStatistics( EvolutionState state )
    {
        // Call the superclass impl
        super.preEvaluationStatistics( state );

        // Get the evaluation start time
        _evalStartTime = System.currentTimeMillis();
    }

    /**
     * TODO Method description
     *
     * @param state
     * @see ec.Statistics#postEvaluationStatistics(ec.EvolutionState)
     */
    @Override
    public void postEvaluationStatistics( EvolutionState state )
    {
        // Before we do anything, get the time
        long evalTime = ( System.currentTimeMillis() - _evalStartTime );
        println( "eval-time = "
                + evalTime,
                state );
        println( "eval-time-human = "
                + TimeUnit.MILLISECONDS.toMinutes( evalTime )
                + "m "
                + TimeUnit.MILLISECONDS.toSeconds( evalTime )
                + "s",
                state );
        _evalTotalTime += evalTime;

        // Call the superclass impl
        super.postEvaluationStatistics( state );
        
        // Define the variables to prevent a lot of gc
        Individual bestOfGenInd = null;
        Individual currentInd = null;
        Subpopulation subPop = null;
        int subPopSize = 0;
        String prefix = null;
        double indFitness = 0.0d;

        // Get the statistics objects
        DescriptiveStatistics fitnessStats = new DescriptiveStatistics();

        // Iterate over the sub-populations
        for( int i = 0; i < state.population.subpops.length; i++ )
        {
            // Save some commonly accessed variables here
            subPop = state.population.subpops[i];
            subPopSize = subPop.individuals.length;
            prefix = "subpop["
                + _2_DIGIT_FORMATTER.format( i )
                + "].";

            // Iterate over all the individuals in the sub-population
            bestOfGenInd = null;
//            _bestFound[i] = bestOfGenInd;
//            _bestFoundGen[i] = state.generation;
            for( int j = 0; j < subPopSize; j++ )
            {
                // Get the current individual
                currentInd = subPop.individuals[ j ];

                // Get the fitness statistic
                indFitness = ((SimpleFitness) currentInd.fitness).fitness();
                fitnessStats.addValue( indFitness );

                // Is this individual the best found for this subpopulation
                // for this generation?
                if( (null == bestOfGenInd)
                        || (currentInd.fitness.betterThan( bestOfGenInd.fitness )) )
                {
                    bestOfGenInd = currentInd;

                    // Is it the best of the run?
                    if( (_bestFound[i] == null) ||
                            (currentInd.fitness.betterThan(_bestFound[i].fitness)) )
                    {
                        // Yup
                        _bestFound[i] = currentInd;
                        _bestFoundGen[i] = state.generation;
                    }
                }
            }

            // Compute and log the mean values and variance of the fitness stats
            println( prefix + "fitness-mean = " + fitnessStats.getMean(), state );
            println( prefix + "fitness-variance = " + fitnessStats.getVariance(), state );
            println( prefix + "fitness-std-dev = " + fitnessStats.getStandardDeviation(), state );

            // Display the best individual's stats
            print( buildIndDescription( bestOfGenInd,
                    state,
                    true,
                    prefix + "best-individual."),
                    state );

            println( prefix + "best-individual-found-so-far.fitness = "
                    + ((SimpleFitness) _bestFound[i].fitness).fitness(),
                    state );
            println( prefix + "best-individual-found-so-far.generation = "
                    + _bestFoundGen[i],
                    state );
        }

        state.output.flush();
    }

    /**
     * Called immediately after the job has completed.
     *
     * @param state The current state of evolution
     * @param result The result of the run
     * @see ec.Statistics#finalStatistics(ec.EvolutionState, int)
     */
    @Override
    public void finalStatistics( EvolutionState state, int result )
    {
        println( "", state, false );

        // Before we do everything, get the time for the job
        long jobTime = System.currentTimeMillis() - _jobStartTime;

        // Call the superclass impl
        super.finalStatistics( state, result );

        // Display the string
        println( "job-time = " + jobTime,
                state,
                false );
        long totalJobSeconds = TimeUnit.SECONDS.convert( jobTime, TimeUnit.MILLISECONDS);
        long minutes = TimeUnit.MINUTES.convert( totalJobSeconds, TimeUnit.SECONDS );
        long seconds = totalJobSeconds - TimeUnit.SECONDS.convert( minutes, TimeUnit.MINUTES );
        println( "job-time-human = "
                + minutes
                + "m "
                + seconds
                + "s",
                state,
                false );

        println( "breed-time = " + _breedTotalTime,
                state,
                false );
        println( "eval-time = " + _evalTotalTime,
                state,
                false );

        // Display the end time
        println( "end-time = " + (new Date()), state, false );

        // Print out the best individuals and the generation they occured in
        for( int i = 0; i < _bestFound.length; i++ )
        {
            if( null != _bestFound[i] )
            {
                println( "subpop["
                        + _2_DIGIT_FORMATTER.format( i )
                        + "].best-individual-found.generation = "
                        + _bestFoundGen[i],
                        state,
                        false );
                print( buildIndDescription( _bestFound[i],
                            state,
                            false,
                            "subpop["
                                + _2_DIGIT_FORMATTER.format( i )
                                + "].best-individual-found."),
                        state );
            }
        }
        state.output.flush();
    }

    /**
     * Builds the description of an individual
     *
     * @param ind The individual to describe
     * @param state The current state of evolution
     * @param useGen Flag indicating whether or not the generation is included
     * @param indPrefix String to add to the prefix describing the individual
     */
    protected String buildIndDescription( Individual ind,
            EvolutionState state,
            boolean useGen,
            String indPrefix )
    {
        String linePrefix = buildStdPrefix( state, useGen ) + indPrefix;

        StringBuilder builder = new StringBuilder();

        // Describe the fitness
        builder.append( linePrefix );
        builder.append( "fitness = " );
        builder.append( ((SimpleFitness) ind.fitness).fitness() );
        builder.append( NEWLINE );

        // Describe the genotype
        builder.append(  linePrefix );
        builder.append( "genotype = " );
        builder.append( ind.genotypeToStringForHumans() );
        builder.append( NEWLINE );

        Problem problem = state.evaluator.p_problem;
        if( !useGen && ( problem instanceof IndividualDescriber ) )
        {
            builder.append( ((IndividualDescriber) problem).describe(
                    ind,
                    linePrefix ) );
        }

        return builder.toString();
    }

    /**
     * Builds the prefix used for output
     *
     * @param state The current state of evolution
     * @param useGen Flag indicating that the generation should be used
     */
    protected String buildStdPrefix( EvolutionState state, boolean useGen )
    {
        StringBuilder builder = new StringBuilder();

        // Start with the job
        builder.append( "job[" );
        builder.append( _2_DIGIT_FORMATTER.format( state.job[0] ) );
        builder.append( "]." );

        // Use the generation
        if( useGen )
        {
            builder.append( "gen[" );
            builder.append( _5_DIGIT_FORMATTER.format( state.generation ) );
            builder.append( "]." );
        }

        return builder.toString();
    }

    /**
     * Prints a full line, prepending the job
     *
     * @param str The string to print
     * @param state The current state of evolution
     */
    protected void println( String str, final EvolutionState state )
    {
        println( str, state, true );
    }

    /**
     * Prints a full line, prepending the job
     *
     * @param str The string to print
     * @param state The current state of evolution
     * @param useGen Flag indicating that the generation should be used
     */
    protected void println( String str,
            final EvolutionState state,
            boolean useGen )
    {
        // Prepend the job number if it isn't a comment
        StringBuilder builder = new StringBuilder();
        if( !str.startsWith( "#" ) )
        {
            builder.append( "job[" );
            builder.append( _2_DIGIT_FORMATTER.format( state.job[0] ) );
            builder.append( "]." );
            if( useGen )
            {
                builder.append( "gen[" );
                builder.append( _5_DIGIT_FORMATTER.format( state.generation ) );
                builder.append( "]." );
            }
        }
        builder.append( str );

        // Display the string
        state.output.println( builder.toString(),
                _statLog );
    }

    /**
     * Prints a string, with no newline, prepending the job
     *
     * @param str The string to print
     * @param state The current state of evolution
     */
    protected void print( String str, final EvolutionState state )
    {
        // Display the string
        state.output.print( str,
                _statLog );
    }

}
