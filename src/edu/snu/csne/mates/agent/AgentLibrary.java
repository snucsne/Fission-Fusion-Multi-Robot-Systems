/*
 *  The Fission-Fusion in Multi-Robot Systems Toolkit is open-source
 *  software for investigating fission-fusion processes in
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
package edu.snu.csne.mates.agent;

//Imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class AgentLibrary
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            AgentLibrary.class.getName() );
    
    /** Mapping of agent ID to agent */
    private Map<Object, Agent> _agents = new HashMap<Object, Agent>();
    
    
    /**
     * Adds an agent to this library
     *
     * @param agent The agent to add
     */
    public void addAgent( Agent agent )
    {
        // Validate and store the agent
        Validate.notNull( agent );
        _agents.put( agent.getID(), agent );
        _LOG.debug( "Added agent with ID=["
                + agent.getID()
                + "] to library" );
    }
    
    /**
     * Returns the agent with the specified id
     *
     * @param name The id of the agent to return
     * @return The agent
     */
    public Agent getAgent( Object id )
    {
        Validate.notNull( "Agent id may not be null" );
        return _agents.get( id );
    }
    
    /**
     * Returns all the agents in this library
     *
     * @return All the agents
     */
    public List<Agent> getAllAgents()
    {
        return new ArrayList<Agent>( _agents.values() );
    }
    
    /**
     * Clears all the agents out of this library
     */
    public void clear()
    {
        _agents.clear();
    }
}
