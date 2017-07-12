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
import org.apache.commons.lang3.Validate;
import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.decision.Decision;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class DecisionEvent
{
    /** The decision */
    private Decision _decision = null;
    
    /** The agent making the decision */
    private Agent _agent = null;
    
    /** The timestep of the decision */
    private long _timestep = 0;
    

    public DecisionEvent( Decision decision, Agent agent, long timestep )
    {
        // Validate and store
        Validate.notNull( decision, "Decision may not be null" );
        Validate.notNull( agent, "Agent may not be null" );
        Validate.isTrue( 0 <= timestep, "timestep may not be negative" );
        _decision = decision;
        _agent = agent;
        _timestep = timestep;
    }
    
    /**
     * Returns the decision associated with this event
     *
     * @return The decision
     */
    public Decision getDecision()
    {
        return _decision;
    }
    
    /**
     * Returns the agent that made this decision
     * 
     * @return the agent
     */
    public Agent getAgent()
    {
        return _agent;
    }
    
    /**
     * Returns the timestep of this decision
     *
     * @return The timestep
     */
    public long getTimestep()
    {
        return _timestep;
    }
    

}
