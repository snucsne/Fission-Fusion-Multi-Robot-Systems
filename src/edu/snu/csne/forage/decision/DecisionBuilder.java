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
package edu.snu.csne.forage.decision;

// Imports
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.GroupBehavior;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.util.MiscUtils;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class DecisionBuilder
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            DecisionBuilder.class.getName() );
    
    /** The simulation state */
    private SimulationState _simState = null;
    
    /** Default behavior weightings */
    Map<GroupBehavior, Float> defaultWeights =
            new EnumMap<GroupBehavior, Float>( GroupBehavior.class );
    
    /** Behavior weightings organized by decision type */
    private Map<DecisionType, Map<GroupBehavior, Float>> _behaviorWeights =
            new EnumMap<DecisionType, Map<GroupBehavior, Float>>( DecisionType.class );

    /**
     * Initialize this decision-builder
     *
     * @param simState The state of the simulation
     * @param props The configuration properties
     * @see edu.snu.csne.forage.decision.AbstractAgentDecisionMaker#initialize(edu.snu.csne.forage.SimulationState, java.util.Properties)
     */
    public void initialize( SimulationState simState, Properties props )
    {
        _LOG.trace( "Entering initialize( simState, props )" );

        // Save the simulation state
        _simState = simState;
        
        // Load the default behavior weights
        for( GroupBehavior gb : GroupBehavior.values() )
        {
            String key = "default-" + gb.getKey() + "-weight";
            float weight = MiscUtils.loadNonEmptyFloatProperty( props,
                    key,
                    "Behavior weight (key=" + key + ") may not be empty" );
            defaultWeights.put( gb, Float.valueOf( weight ) );
        }
        
        // See if there are any overrides for specific decision types
        for( DecisionType dt : DecisionType.values () )
        {
            // Create the map
            Map<GroupBehavior, Float> weights = 
                    new EnumMap<GroupBehavior, Float>( GroupBehavior.class );
            _behaviorWeights.put( dt, weights );
            
            // Iterate through all the group behaviors
            for( GroupBehavior gb : GroupBehavior.values() )
            {
                // Get the default value
                float defaultValue = defaultWeights.get( gb ).floatValue();
                
                // Get the property
                String key = dt.getKey() + "-" + gb.getKey() + "-weight";;
                float weight = MiscUtils.loadOptionalFloatProperty( props,
                        key,
                        defaultValue );
                
                // Add it to the map
                weights.put( gb, Float.valueOf( weight ) );
                _LOG.debug( "Weight: "
                        + key
                        + "=["
                        + weight
                        + "] type=["
                        + dt
                        + "]" );
            }

        }

        _LOG.trace( "Leaving initialize( simState, props )" );
    }

    
    public Decision buildPatchNavigateDecision( Agent agent, Patch patch, float probabiltiy )
    {
        // Get the navigation weights
        Map<GroupBehavior, Float> navWeights = _behaviorWeights.get( DecisionType.NAVIGATE );

        // Build the decision
        Decision decision = Decision.buildNavigateDecision( _simState.getCurrentSimulationStep(),
//                _simState.createNewTeam(),
                null,
                patch,
                navWeights.get( GroupBehavior.SEPARATION ).floatValue(),
                navWeights.get( GroupBehavior.COHESION ).floatValue(),
                navWeights.get( GroupBehavior.ALIGNMENT ).floatValue(),
                navWeights.get( GroupBehavior.GOAL_SEEK ).floatValue(),
                probabiltiy );
        
        // Log it
        _LOG.debug( "Agent=["
                + agent.getID()
                + "] NAVIGATE Decision=["
                + decision
                + "]" );
        
        return decision;
    }
    
    public Decision buildFollowDecision( Agent agent, Agent leader, float probabiltiy )
    {
        // Get the follow weights
        Map<GroupBehavior, Float> followWeights = _behaviorWeights.get( DecisionType.FOLLOW );

        // Build the decision
        Decision decision = Decision.buildFollowDecision( _simState.getCurrentSimulationStep(),
                leader,
                followWeights.get( GroupBehavior.SEPARATION ).floatValue(),
                followWeights.get( GroupBehavior.COHESION ).floatValue(),
                followWeights.get( GroupBehavior.ALIGNMENT ).floatValue(),
                followWeights.get( GroupBehavior.GOAL_SEEK ).floatValue(),
                probabiltiy );
        
        // Log it
        _LOG.debug( "Agent=["
                + agent.getID()
                + "] FOLLOW Decision=["
                + decision
                + "]" );
        
        return decision;
    }
    
    public Decision buildForageDecision( Agent agent, Patch patch, float probabiltiy )
    {
        // Get the forage weights
        Map<GroupBehavior, Float> forageWeights = _behaviorWeights.get( DecisionType.FORAGE );

        // Build the decision
        Decision decision = Decision.buildForageDecision( _simState.getCurrentSimulationStep(),
                patch.getForagingTeam(),
                null,
                patch,
                forageWeights.get( GroupBehavior.SEPARATION ).floatValue(),
                forageWeights.get( GroupBehavior.COHESION ).floatValue(),
                forageWeights.get( GroupBehavior.ALIGNMENT ).floatValue(),
                forageWeights.get( GroupBehavior.GOAL_SEEK ).floatValue(),
                probabiltiy );
        
        // Log it
        _LOG.debug( "Agent=["
                + agent.getID()
                + "] FORAGE Decision=["
                + decision
                + "]" );
        
        return decision;
    }
    

}
