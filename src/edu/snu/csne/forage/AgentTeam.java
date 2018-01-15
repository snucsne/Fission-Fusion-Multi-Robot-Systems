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
package edu.snu.csne.forage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class AgentTeam
{
    /** This team's unique ID */
    private String _id = null;
    
    /** The members of this team */
    private Map<String,Agent> _members = new HashMap<String,Agent>();
    
    /**
     * Builds this AgentTeam object
     *
     * @param id The team's unique identifier
     */
    public AgentTeam( String id )
    {
        // Validate and store
        Validate.notEmpty( id,
                "Agent team ID may not be null or empty ["
                + id
                + "]" );
        _id = id;
    }
    
    /**
     * Returns the unique ID for this agent team
     *
     * @return The unique ID
     */
    public String getID()
    {
        return _id;
    }

    /**
     * TODO Method description
     *
     * @param other
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( AgentTeam other )
    {
        // Simply compare the ID's
        return _id.equals( other._id );
    }
    
    /**
     * Signals the team that an agent has joined
     *
     * @param agent The joining agent
     */
    public void join( Agent agent )
    {
        Validate.notNull( agent, "Joining agent may not be null" );
        _members.put( agent.getID(), agent );
    }
    
    /**
     * Signals the team that an agent is leaving
     *
     * @param agent The leaving agent
     */
    public void leave( Agent agent )
    {
        Validate.notNull( agent, "Leaving agent may not be null" );
        Agent leaver = _members.remove( agent.getID() );
        Validate.notNull( leaver, "Leaving agent ["
                + agent.getID()
                + "] was not a member of team ["
                + getID()
                + "]" );
    }
    
    /**
     * Returns the size of the team
     *
     * @return The size
     */
    public int getSize()
    {
        return _members.size();
    }
    
    /**
     * Returns all the members of this team
     *
     * @return All the members
     */
    public List<Agent> getMembers()
    {
        return new ArrayList<Agent>( _members.values() );
    }
    
    /**
     * Indicates whether or not the team is active with members
     *
     * @return Returns <code>true</code> if the team is active and has members,
     * otherwise <code>false</code>
     */
    public boolean isActive()
    {
        return (_members.size() > 0);
    }
}
