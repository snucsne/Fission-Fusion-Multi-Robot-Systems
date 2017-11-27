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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jme3.math.Vector3f;
import ec.util.MersenneTwisterFast;
import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.forage.SimulationState;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class GoalSeekFlockingDecisionMaker extends AbstractAgentDecisionMaker
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            GoalSeekFlockingDecisionMaker.class.getName() );
    
    /** Random number generator */
    private MersenneTwisterFast _rng = null;
    
    /** The minimum distance to the goal destination before we pick a new one */
    private float _minDistanceToGoal = 0.0f;
    
    /** A list of all the patch IDs */
    private List<String> _patchIDs = null;
    
    
    /**
     * Initialize this agent decision-maker
     *
     * @param simState The state of the simulation
     * @param props The configuration properties
     * @see edu.snu.csne.forage.decision.AbstractAgentDecisionMaker#initialize(edu.snu.csne.forage.SimulationState, java.util.Properties)
     */
    @Override
    public void initialize( SimulationState simState, Properties props )
    {
        _LOG.trace( "Entering initialize( simState, props )" );

        // Call the superclass implementation
        super.initialize( simState, props );
        
        // Get the random number generator
        _rng = simState.getRNG();
        
        // Just hard code the minimum 
        _minDistanceToGoal = 1.0f;
        
        // Get all the patches
        _patchIDs = new LinkedList<String>( simState.getAllPatches().keySet() );
        _LOG.debug( "There are [" + _patchIDs.size() + "] patch IDs" );
        
        _LOG.trace( "Leaving initialize( simState, props )" );
    }

    /**
     * Makes a decision upon which the agent will act
     *
     * @param agent The agent making the decision
     * @return The decision
     * @see edu.snu.csne.forage.decision.AgentDecisionMaker#decide(edu.snu.csne.forage.Agent)
     */
    @Override
    public Decision decide( Agent agent )
    {
        _LOG.trace( "Entering decide( agent )" );

        Decision decision = null;

        // What is the agent's current decision?
        Decision current = agent.getDecision();
        _LOG.debug( "Current decision [" + current + "] for agent [" + agent.getID() + "]" );
        if( null != current )
        {
            _LOG.debug( "Current type [" + current.getType() + "]" );
        }
        
        // Is it the default of rest?
        if( (null == current) || (DecisionType.REST.equals( current.getType() ) ) )
        {
            // Yup.  Is there someone to follow?
            List<Agent> nonTeammates = agent.getSensedNonTeammates();
            if( 0 < nonTeammates.size() )
            {
                // Get all the sensed team leaders
                Map<String,Agent> sensedLeaders = agent.getSensedTeamLeaders();
                
                // Remove the agent's current team
                sensedLeaders.remove( agent.getTeam().getID() );
                
                // Randomly choose a team
                Object[] teamIDs = sensedLeaders.keySet().toArray();
                String teamID = (String) teamIDs[ _rng.nextInt( teamIDs.length ) ];
                Agent leader = sensedLeaders.get( teamID );
                decision = Decision.buildFollowDecision(
                        _simState.getCurrentSimulationStep(),
                        leader,
                        2.0f,
                        1.0f,
                        1.0f,
                        1.25f,
                        1.0f );
                _LOG.debug( "Agent [" + agent.getID() + "] is following agent [" + leader.getID() + "]" );
            }
            else
            {
                // Nope, navigate to a patch.
                String patchID = _patchIDs.remove( 0 );
                Patch patch = _simState.getPatch( patchID );
                _LOG.debug( "Agent [" + agent.getID() + "] is navigating to patch [" + patchID + "] at [" + patch.getPosition() + "]" );
                decision = Decision.buildNavigateDecision(
                        _simState.getCurrentSimulationStep(),
                        _simState.createNewTeam(),
                        patch,
                        1.5f,
                        1.0f,
                        1.0f,
                        1.5f,
                        1.0f );
                
                // Put the patch at the end of the list
                _patchIDs.add( patchID );
            }
        }
        // Nope.  Are we leading somewhere?
        else if( DecisionType.NAVIGATE.equals( current.getType() ) )
        {
            // Yup.  Are we at that destination?
            Vector3f destination = current.getDestination();
            float distanceToGoal = agent.getPosition().distance( destination );
            if( distanceToGoal < _minDistanceToGoal )
            {
                // Yup.  Choose a new one
                String patchID = _patchIDs.remove( 0 );
                Patch patch = _simState.getPatch( patchID );
                _LOG.debug( "Navigating to NEW  patch [" + patchID + "] at [" + patch.getPosition() + "]" );
                decision = Decision.buildNavigateDecision(
                        _simState.getCurrentSimulationStep(),
                        agent.getTeam(),
                        patch,
                        1.5f,
                        1.0f,
                        0.5f,
                        1.5f,
                        1.0f );
                
                // Put the patch at the end of the list
                _patchIDs.add( patchID );
            }
            else
            {
                // Nope.  Keep going.
                decision = current;
            }
        }
        else if( DecisionType.FOLLOW.equals( current.getType() ) )
        {
            // Keep doing it
            decision = current;
        }
        
        _LOG.debug( "Decision is [" + decision + "] of type [" + decision.getType() + "]" );
        
        _LOG.trace( "Leaving decide( agent )" );
        
        return decision;
    }

}
