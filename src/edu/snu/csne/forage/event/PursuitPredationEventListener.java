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
package edu.snu.csne.forage.event;

//Imports
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.AgentTeam;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.util.MiscUtils;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class PursuitPredationEventListener extends AbstractSimulationEventListener
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            PursuitPredationEventListener.class.getName() );

    /** Key for the predation probability */
    private static final String _PURSUIT_PREDATION_PROBABILITY = "pursuit-predation-probability";
    
    
    /** Base predation probability per team per timestep */
    private float _probability = 0.0f;
    
    
    /**
     * Initializes this event listener
     *
     * @param simState The simulation state
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#initialize(edu.snu.csne.forage.SimulationState)
     */
    @Override
    public void initialize( SimulationState simState )
    {
        _LOG.trace( "Entering initialize( simState )" );

        // Call the superclass implementation
        super.initialize( simState );
        
        // Determine the base predation probability
        _probability = MiscUtils.loadNonEmptyFloatProperty( simState.getProps(),
                _PURSUIT_PREDATION_PROBABILITY,
                "Base pursuit predation probability" );
        _LOG.debug( "_probability=["
                + _probability
                + "]" );
        
        _LOG.trace( "Leaving initialize( simState )" );
    }

    /**
     * Performs any cleanup after a simulation step
     *
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#simStepTearDown()
     */
    @Override
    public void simStepTearDown()
    {
        _LOG.trace( "Entering simStepTearDown()" );

        // Iterate through all the active teams
        Map<String,AgentTeam> teams = _simState.getAllActiveTeams();
        Iterator<AgentTeam> teamIter = teams.values().iterator();
        while( teamIter.hasNext() )
        {
            AgentTeam current = teamIter.next();
            
            // Get the size of the team
            int teamSize = current.getSize();
            
            // Did predation occur?
            float predationValue = _simState.getRNG().nextFloat(); 
            if( _probability > predationValue )
            {
                // Yup, determine which agent was the prey
                List<Agent> teamMembers = current.getMembers();
                
                // At this point randomly choose one, regardless of position
                int preyIdx = _simState.getRNG().nextInt( teamSize );
                Agent prey = teamMembers.get( preyIdx );
                prey.terminate();
                
                _LOG.info( "Agent ["
                        + prey.getID()
                        + "] of team ["
                        + current.getID()
                        + "] became prey of a pursuit predator ["
                        + _probability
                        + " > "
                        + predationValue
                        + "]" );
            }
        }
        
        _LOG.trace( "Leaving simStepTearDown()" );
    }
    
    
}
