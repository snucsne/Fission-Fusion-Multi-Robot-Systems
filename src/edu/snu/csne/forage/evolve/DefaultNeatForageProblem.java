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

// Imports
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.forage.Simulator;
import edu.snu.csne.forage.decision.ForagingDecisionMaker;
import edu.snu.csne.forage.decision.NeatProbabilityDecisionCalculator;
import edu.snu.csne.forage.event.PatchDepletionListener;
import edu.snu.csne.forage.evolve.FoldProperties.FoldType;
import edu.snu.csne.forage.evolve.FoldProperties.PropertyType;
import edu.snu.csne.util.MiscUtils;
import edu.snu.jyperneat.core.NeatIndividual;
import edu.snu.jyperneat.core.Network;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class DefaultNeatForageProblem extends Problem
implements SimpleProblemForm, IndividualDescriber
{
    /** Default serial version UID */
    private static final long serialVersionUID = 1L;

    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            DefaultNeatForageProblem.class.getName() );

    /** Parameter key for the fold properties file */
    private static final String _P_FOLD_PROPERTIES = "fold-properties";
    
    /** Parameter key for flag to force re-evaluation of individuals */
    private static final String _P_FORCE_REEVALUATION = "force-reevaluation";

    
    /** Default simulator properties */
    protected Properties _defaultSimProperties = null;
    
    /** Fold property files */
    protected FoldProperties _foldProps = new FoldProperties();
    
    /** Flag indicating that individuals should be re-evaluated every generation */
    private boolean _forceReevaluation = false;

    /**
     * Sets up the object by reading it from the parameters stored in
     * state, built off of the parameter base base.
     *
     * @param state
     * @param base
     * @see ec.Problem#setup(ec.EvolutionState, ec.util.Parameter)
     */
    @Override
    public void setup( EvolutionState state, Parameter base )
    {
        _LOG.trace( "Entering setup( state, base )" );

        // Call the superclass implementation
        super.setup( state, base );

        // Load the default simulator properties
        _defaultSimProperties = MiscUtils.loadProperties( Simulator.PROPS_FILE_KEY );

        // Load the fold properties
        Validate.isTrue( state.parameters.exists(
                base.push( _P_FOLD_PROPERTIES ), null ),
                "Fold properties file is required " );
        String foldPropsDefFile = state.parameters.getString(
                 base.push( _P_FOLD_PROPERTIES ),
                 null );
        _LOG.info( "Using foldPropsDefFile=[" + foldPropsDefFile + "]" );
        _foldProps.initialize( foldPropsDefFile );

        // Do we force reevaluation?
        Validate.isTrue( state.parameters.exists(
                base.push( _P_FORCE_REEVALUATION ), null ),
                "Force reevaluation flag is required " );
        _forceReevaluation = state.parameters.getBoolean(
                 base.push( _P_FORCE_REEVALUATION ),
                 null,
                 false );
        _LOG.info( "Using _forceReevaluation=[" + _forceReevaluation + "]" );

        _LOG.trace( "Leaving setup( state, base )" );
    }

    /**
     * Evaluates the individual in ind, if necessary (perhaps not
     * evaluating them if their evaluated flags are true), and sets
     * their fitness appropriately.
     *
     * @param state
     * @param ind
     * @param subpopulation
     * @param threadnum
     * @see ec.simple.SimpleProblemForm#evaluate(ec.EvolutionState, ec.Individual, int, int)
     */
    @Override
    public void evaluate( EvolutionState state,
            Individual ind,
            int subpopulation,
            int threadnum )
    {
        _LOG.warn( "Evaluating individual..." );
        
        // Has the individual already been evaluated?
        if( ind.evaluated && !_forceReevaluation )
        {
            // Yup, bail out early
            return;
        }

        // Is it the correct type of individual
        if( !(ind instanceof NeatIndividual) )
        {
            // Nope, complain
            _LOG.error( "Individual is not of correct type ["
                    + ind.getClass().getCanonicalName()
                    + "]" );
            state.output.fatal( "Individual is not the correct type" );
        }

        evaluate( state, ind, FoldType.TRAINING );
        
        _LOG.warn( "Done evaluating..." );
    }

    /**
     * Evaluates an individual in all the environments of a particular fold type
     *
     * @param state The current state of evolution
     * @param ind The individual to evaluate
     * @param foldType The type of fold
     */
    public void evaluate( EvolutionState state,
            Individual ind,
            FoldType foldType )
    {
        // Cast it to the proper type
        NeatIndividual neatInd = (NeatIndividual) ind;

        // Build the network
        Network network = neatInd.createPhenotype();
        
        // Get fold training properties
        String[] foldAgentProperties = _foldProps.getProperties(
                foldType,
                PropertyType.AGENT );
        _LOG.debug( "Agent fold properties: type=["
                + foldType.name()
                + "] count=["
                + foldAgentProperties.length
                + "]" );
        String[] foldPatchProperties = _foldProps.getProperties(
                foldType,
                PropertyType.PATCH );
        _LOG.debug( "Patch fold properties: type=["
                + foldType.name()
                + "] count=["
                + foldPatchProperties.length
                + "]" );

        // Iterate through all the training properties
        float totalResourcesForaged = 0.0f;
        float[] fitnessValues = new float[foldAgentProperties.length];
        for( int i = 0; i < foldAgentProperties.length; i++ )
        {
//            _LOG.info( "Running sim [" + i + "]" );
            
            // Get the default properties
            Properties simProps = new Properties();
            simProps.putAll( _defaultSimProperties );
            
            // Override the fold specific properties
            simProps.setProperty( SimulationState._AGENT_PROPS_FILE_KEY,
                    foldAgentProperties[i] );
            simProps.setProperty( SimulationState._PATCH_PROPS_FILE_KEY,
                    foldPatchProperties[i] );
            
            // Create the simulator
            Simulator sim = new Simulator();
            sim.initialize( simProps );
            
            // Change the probability decision calculator
            SimulationState simState = sim.getSimState();
            NeatProbabilityDecisionCalculator probDecisionCalculator =
                    new NeatProbabilityDecisionCalculator( network );
            probDecisionCalculator.initialize( simState );
            ForagingDecisionMaker decisionMaker = (ForagingDecisionMaker)
                    simState.getAgentDecisionMaker();
            decisionMaker.setProbabilityDecisionCalculator( probDecisionCalculator );

            // Add our own patch depletion listener
            PatchDepletionListener patchListener = new PatchDepletionListener();
            simState.addEventListener( patchListener );
            
            // Run it
            sim.run();
            
            // Get the resources foraged
            float resourcesForaged = patchListener.getTotalResourcesForaged();
            totalResourcesForaged += resourcesForaged;
            fitnessValues[i] = resourcesForaged;

//            _LOG.debug( "Run ["
//                    + i
//                    + "] resourcesForaged=["
//                    + resourcesForaged
//                    + "]" );
        }
        
        // Compute the mean resources foraged
        float meanResourcesForaged = totalResourcesForaged / foldAgentProperties.length;
        _LOG.debug( "Fitness ["
                + foldType.name()
                + "]: totalResourcesForaged=["
                + totalResourcesForaged
                + "] foldAgentProperties.length=["
                + foldAgentProperties.length
                + "] meanResourcesForaged=["
                + meanResourcesForaged
                + "]" );

        // Save the fitness
        CrossValidationFitness cvFitness = (CrossValidationFitness) ind.fitness;
        if( FoldType.TRAINING.equals( foldType ) )
        {
            cvFitness.setTrainingResults( fitnessValues );
            cvFitness.setFitness( state, cvFitness.getTrainingFitnessMean(), false );
        }
        else if( FoldType.TESTING.equals( foldType ) )
        {
            cvFitness.setTestingResults( fitnessValues );
        }
        else if( FoldType.VALIDATION.equals( foldType ) )
        {
            cvFitness.setValidationResults( fitnessValues );
        }
        else
        {
            _LOG.error( "Unknown fold type ["
                    + foldType
                    + "]" );
            state.output.fatal( "Unknown fold type ["
                    + foldType
                    + "]" );
        }
        
        // Mark the individual as evaluated
        ind.evaluated = true;
    }
    
    /**
     * Returns a description of the specified individual using the specified
     * line prefix.
     *
     * @param ind The individual to describe
     * @param prefix The prefix for every line in the description
     * @return A description of the individual
     * @see edu.snu.csne.forage.evolve.IndividualDescriber#describe(ec.Individual, java.lang.String)
     */
    @Override
    public String describe( Individual ind, String prefix )
    {
        StringBuilder builder = new StringBuilder();
        builder.append( prefix );

        builder.append( System.getProperty( "line.separator" ) );

        return builder.toString();
    }

}
