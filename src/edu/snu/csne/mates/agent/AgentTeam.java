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

// Imports
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class AgentTeam
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            AgentTeam.class.getName() );
    
    /** This team's unique ID */
    private Object _id = null;
    
    /** This team's name */
    private String _name = null;

    /** This team's parent */
    private AgentTeam _parent = null;

    /** This team's full name */
    private String _fullName = null;

    /** This team's depth in the hierarchy */
    private int _depth = 0;

    /** Flag indicating that this team has been initialized */
    private boolean _initialized = false;

    public AgentTeam( Object id, String name, AgentTeam parent )
    {
        // Validate the parameters
        Validate.notNull( id, "Agent team ID may not be null" );
        Validate.notEmpty( name, "Agent team name may not be empty" );
        Validate.notNull( parent, "Agent team parent may not be null" );
        
        // Store them
        _id = id;
        _name = name;
        _parent = parent;
    }
    
    public void setParentTeam( AgentTeam parent )
    {
        // Validate and store the parent
        Validate.notNull( parent, "Agent team parent may not be null" );
        _parent = parent;
        
        // Update calculated attributes
        update();
    }

    /**
     * Returns the id for this agent team
     *
     * @return The id
     */
    public Object getID()
    {
        return _id;
    }

    /**
     * Returns the name for this agent team
     *
     * @return The name
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Returns the fullName for this agent team
     *
     * @return The fullName
     */
    public String getFullName()
    {
        return _fullName;
    }

    /**
     * Returns the depth for this agent team
     *
     * @return The depth
     */
    public int getDepth()
    {
        return _depth;
    }
    
    /**
     * Returns the parent for this agent team
     *
     * @return The parent
     */
    public AgentTeam getParent()
    {
        return _parent;
    }

    /**
     * Determines if this Team is a sub-team of the indicated
     * Team.
     *
     * @param team The Team against which this Team is being tested
     * @return Returns <code>true</code> if this Team is the same
     * Team or a sub-team of the indicated Team, otherwise,
     * <code>false</code>
     */
    public boolean isSubTeamOf( AgentTeam team )
    {
        // Validate the given team
        Validate.notNull( team, "Agent team may not be null" );

//        // Make sure we are initialized
//        if( !_initialized )
//        {
//            init();
//        }

        // Start with this team and go up the hierarchy
        AgentTeam current = this;
        boolean isSubTeam = false;
        while( (null != current) && !isSubTeam )
        {
            // Is the current team the same as the given one?
            if( team.getID().equals( current.getID() ) )
            {
                // Yup, this team is a sub-team
                isSubTeam = true;
            }
            else
            {
                // Nope, go up the hierarchy
                current = current.getParent();
            }
        }

        return isSubTeam;
    }

    
    /**
     * Updates calculated attributes of this agent team
     */
    private void update()
    {
        // Build the name and update the depth
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.insert( 0, _name );
        AgentTeam current = _parent;
        while( null != current )
        {
            _depth++;
            nameBuilder.insert( 0, current.getName() + "." );
            current = current.getParent();
        }
        _fullName = nameBuilder.toString();

    }
}
