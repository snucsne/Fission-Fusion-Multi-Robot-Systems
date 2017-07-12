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

//Imports
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.AgentTeam;
import edu.snu.csne.forage.SimulationState;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class FlockingDecisionMaker extends AbstractAgentDecisionMaker
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            FlockingDecisionMaker.class.getName() );
    
    
    /**
     * Makes a decision upon which the agent will act
     *
     * @param agent The agent making the decision
     * @return The decision
     * @see edu.snu.csne.forage.decision.AbstractAgentDecisionMaker#initialize(edu.snu.csne.forage.SimulationState, java.util.Properties)
     */
    @Override
    public void initialize( SimulationState simState, Properties props )
    {
        _LOG.trace( "Entering initialize( simState, props )" );

        // Call the superclass implementation
        super.initialize( simState, props );
        
        _LOG.trace( "Leaving initialize( simState, props )" );
    }

    /**
     * Initialize this agent decision-maker
     *
     * @param simState The state of the simulation
     * @param props The configuration properties
     * @see edu.snu.csne.forage.decision.AgentDecisionMaker#decide(edu.snu.csne.forage.Agent)
     */
    @Override
    public Decision decide( Agent agent )
    {
        _LOG.trace( "Entering decide( agent )" );

        // There should be only one team
        Map<String,AgentTeam> allTeams = _simState.getAllTeams();
        AgentTeam team = allTeams.values().iterator().next();
        
        // Always do the same thing and simply flock
        Decision decision = new Decision( DecisionType.NO_CHANGE,
                team,
                null,
                1.0f,
                1.0f,
                1.0f,
                0.0f );
        
        
        _LOG.trace( "Leaving decide( agent )" );
        
        return decision;
    }


}
