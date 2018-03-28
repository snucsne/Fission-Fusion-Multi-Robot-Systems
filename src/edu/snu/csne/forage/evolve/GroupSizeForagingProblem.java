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

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import ec.EvolutionState;
import ec.Individual;
import ec.vector.BitVectorIndividual;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.forage.Simulator;
import edu.snu.csne.forage.event.PatchDepletionListener;
import edu.snu.csne.forage.event.TeamSizeEventListener;
import edu.snu.csne.forage.evolve.FoldProperties.FoldType;
import edu.snu.csne.forage.evolve.FoldProperties.PropertyType;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class GroupSizeForagingProblem extends DefaultForageProblem
{
    /** Default serial version UID */
    private static final long serialVersionUID = 1L;

    
    /**
     * Evaluates an individual in all the environments of a particular fold type
     *
     * @param state The current state of evolution
     * @param ind The individual to evaluate
     * @param foldType The type of fold
     * @see edu.snu.csne.forage.evolve.DefaultForageProblem#evaluate(ec.EvolutionState, ec.Individual, edu.snu.csne.forage.evolve.FoldProperties.FoldType)
     */
    @Override
    public void evaluate( EvolutionState state,
            Individual ind,
            FoldType foldType )
    {
        // Cast it to the proper type
        BitVectorIndividual bitInd = (BitVectorIndividual) ind;

        // Decode the genome
        Properties genomeProps = decodeGenome( bitInd.genome );
        
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
        float allMeanGroupSizesSum = 0.0f;
        float[] meanGroupSizeSums = new float[foldAgentProperties.length];
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
            
            // Override the genome specific properties
            simProps.putAll( genomeProps );
            
            // Create the simulator
            Simulator sim = new Simulator();
            sim.initialize( simProps );
            
            // Add our own patch depletion listener
            PatchDepletionListener patchListener = new PatchDepletionListener();
            SimulationState simState = sim.getSimState();
            simState.addEventListener( patchListener );
            
            // Add our own team size event listener
            TeamSizeEventListener teamSizeListener = new TeamSizeEventListener();
            simState.addEventListener( teamSizeListener );

            // Run it
            sim.run();
            
            // Get the group size history
            List<int[]> teamSizeHistory = teamSizeListener.getTeamSizeHistory();
            float meanTeamSizeSum = 0.0f;
            Iterator<int[]> iter = teamSizeHistory.iterator();
            while( iter.hasNext() )
            {
                // Compute the mean team size
                int[] teamSizes = iter.next();
                int teamSizeSum = 0;
                for( int j = 0; j < teamSizes.length; j++ )
                {
                    teamSizeSum += teamSizes[j];
                }
                meanTeamSizeSum += teamSizeSum / (float) teamSizes.length;
            }
            allMeanGroupSizesSum += meanTeamSizeSum;
            meanGroupSizeSums[i] = meanTeamSizeSum;

//            _LOG.debug( "Run ["
//                    + i
//                    + "] resourcesForaged=["
//                    + resourcesForaged
//                    + "]" );
        }
        
        // Compute the mean resources foraged
        float meanMeanGroupSizesSum = allMeanGroupSizesSum / foldAgentProperties.length;
        _LOG.debug( "Fitness ["
                + foldType.name()
                + "]: meanMeanGroupSizesSum=["
                + meanMeanGroupSizesSum
                + "] foldAgentProperties.length=["
                + foldAgentProperties.length
                + "] allMeanGroupSizesSum=["
                + allMeanGroupSizesSum
                + "]" );
        
        // Save the fitness
        CrossValidationFitness cvFitness = (CrossValidationFitness) ind.fitness;
        if( FoldType.TRAINING.equals( foldType ) )
        {
            cvFitness.setTrainingResults( meanGroupSizeSums );
            cvFitness.setFitness( state, cvFitness.getTrainingFitnessMean(), false );
        }
        else if( FoldType.TESTING.equals( foldType ) )
        {
            cvFitness.setTestingResults( meanGroupSizeSums );
        }
        else if( FoldType.VALIDATION.equals( foldType ) )
        {
            cvFitness.setValidationResults( meanGroupSizeSums );
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
        bitInd.evaluated = true;
    }

}
