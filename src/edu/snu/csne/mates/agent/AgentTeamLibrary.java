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
package edu.snu.csne.mates.agent;

// Import
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Library of all the agent teams in the simulation
 *
 * @author Brent Eskridge
 */
public class AgentTeamLibrary
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            AgentTeamLibrary.class.getName() );
    
    /** Mapping from ID to agent team */
    private Map<Object, AgentTeam> _teams = new HashMap<Object, AgentTeam>();
    
    /**
     * Adds a team to the library
     * 
     * @param team The team to add to the library
     */
    public void addTeam( AgentTeam team )
    {
        // Validate it
        Validate.notNull( team, "Agent team may not be null" );
        _teams.put( team.getID(), team );
        
        _LOG.debug( "Added team=[" + team.getID() + "] to library" );
    }
    
    /**
     * Retrieves a team from the library given an id
     * 
     * @param id The team's id
     * @return The team, if it exists
     */
    public AgentTeam getTeam( Object id )
    {
        return _teams.get( id );
    }

    public void clear()
    {
        // Clear the map
        _teams.clear();
    }
}
