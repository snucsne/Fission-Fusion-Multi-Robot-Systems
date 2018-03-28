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

// Imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.AgentTeam;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.forage.decision.DecisionType;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class TeamSizeEventListener extends AbstractSimulationEventListener
{
    /** History of all the active team sizes */
    private List<int[]> _teamSizeHistory = new LinkedList<int[]>();

    /** History of all the active number of agents */
    private List<Integer> _activeAgentHistory = new LinkedList<Integer>();

    
    /**
     * Initializes this event listener
     *
     * @param simState The simulation state
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#initialize(edu.snu.csne.forage.SimulationState)
     */
    @Override
    public void initialize( SimulationState simState )
    {
        // Call the superclass implementation
        super.initialize( simState );
    }

    
    /**
     * Prepares a simulation run for execution
     *
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#simRunSetup()
     */
    @Override
    public void simRunSetup()
    {
        // Clear out the old team size history values
        _teamSizeHistory = new LinkedList<int[]>();
        _activeAgentHistory = new LinkedList<Integer>();
    }


    /**
     * Performs any cleanup after a simulation step
     *
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#simStepTearDown()
     */
    @Override
    public void simStepTearDown()
    {
        // Save the sizes of all the non-empty teams
        Collection<AgentTeam> teams = _simState.getAllActiveTeams().values();
        List<Integer> tmpTeamSizes = new ArrayList<Integer>( teams.size() );
        Iterator<AgentTeam> teamIter = teams.iterator();
        while( teamIter.hasNext() )
        {
            AgentTeam team = teamIter.next();
            
            // Ensure there are agents in the team
            if( team.isActive() )
            {
                // Ensure that the team isn't resting
                Agent leader = team.getMembers().get( 0 );
                if( !DecisionType.REST.equals( leader.getDecision().getType() ) )
                {
                    tmpTeamSizes.add( Integer.valueOf( team.getSize() ) );
                }
            }
        }
        
        // Convert it to an array
        int[] teamSizes = new int[tmpTeamSizes.size()];
        for( int i = 0; i < teamSizes.length; i++ )
        {
            teamSizes[i] = tmpTeamSizes.get(i).intValue();
        }
        _teamSizeHistory.add( teamSizes );

        // Calculate the number of active agents
        int activeAgentCount = 0;
        Collection<Agent> allAgents = _simState.getAllAgents().values();
        Iterator<Agent> agentIter = allAgents.iterator();
        while( agentIter.hasNext() )
        {
            Agent agent = agentIter.next();
            if( agent.isActive() )
            {
                activeAgentCount++;
            }
        }
        _activeAgentHistory.add( Integer.valueOf( activeAgentCount ) );
    }

    /**
     * Returns the history of all active team sizes
     *
     * @return The history of all active team sizes
     */
    public List<int[]> getTeamSizeHistory()
    {
        return _teamSizeHistory;
    }

    /**
     * Returns the history of the active agent counts
     *
     * @return The history of all the active agent counts
     */
    public List<Integer> getActiveAgentCountHistory()
    {
        return _activeAgentHistory;
    }
}