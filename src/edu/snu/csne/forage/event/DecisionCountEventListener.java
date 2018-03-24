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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.forage.decision.Decision;
import edu.snu.csne.forage.decision.DecisionType;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class DecisionCountEventListener extends AbstractSimulationEventListener
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            DecisionCountEventListener.class.getName() );
    
    /** History of the decision counts */
    private List<int[]> _decisionCountHistory = new LinkedList<int[]>();
    
    /** The different types of the decisions */
    private DecisionType[] _decisionTypes = DecisionType.values();
    
    
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
        
        _LOG.trace( "Leaving initialize( simState )" );
    }

    /**
     * Prepares a simulation run for execution
     *
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#simRunSetup()
     */
    @Override
    public void simRunSetup()
    {
        // Clear out the old decision counts
        _decisionCountHistory = new LinkedList<int[]>();
    }

    /**
     * Performs any cleanup after a simulation step
     *
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#simStepTearDown()
     */
    @Override
    public void simStepTearDown()
    {
        // Create an array to hold all the counts of each decision type
        int[] decisionCounts = new int[_decisionTypes.length];
        
        // Iterate through every agent to determin their current decision
        Iterator<Agent> agentIter = _simState.getAllAgents().values().iterator();
        while( agentIter.hasNext() )
        {
            Agent agent = agentIter.next();
            Decision decision = agent.getDecision();
            
            // Increment the associated type
            decisionCounts[ decision.getType().ordinal() ]++;
        }
        
        // Save it
        _decisionCountHistory.add( decisionCounts );
    }

    /**
     * Returns the history of all the decision counts
     *
     * @return The history of all the decision counts
     */
    public List<int[]> getDecisionCountHistory()
    {
        return _decisionCountHistory;
    }
}
