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
    
    
}