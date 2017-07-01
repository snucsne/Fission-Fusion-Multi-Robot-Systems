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
package edu.snu.csne.mates.sim;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.scene.Node;

import edu.snu.csne.mates.agent.Agent;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class WorldObject implements Comparable<WorldObject>
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            WorldObject.class.getName() );
    
    /** The unique ID of this world object */
    private Object _id = null;
    
    /** This world object's name */
    private String _name = null;
    
    /** The associated agent */
    private Agent _agent = null;
    
    /** The root node of the associated spatials */
    private Node _node = null;
    
    /** The spatial category for this world object */
    private SpatialCategory _spatialCategory = null;
    
    /** Flag indicating whether or not this object can collide with other objects */
    private boolean _isCollidable = true;
    
    
    /**
     * Builds this WorldObject object
     *
     * @param id The unique ID of this world object
     * @param name The name of this world object
     */
    public WorldObject( Object id, String name )
    {
        // Chain to the full constructor
        this( id, name, true );
    }
    
    /**
     * Builds this WorldObject object
     *
     * @param id The unique ID of this world object
     * @param name The name of this world object
     * @param isCollidable Flag indicating whether or not this world object
     * can collide with other objects
     */
    public WorldObject( Object id, String name, boolean isCollidable )
    {
        // Validate the parameters
        Validate.notNull( id, "ID may not be null" );
        Validate.notBlank( name, "Name may not be null or blank" );
        
        // Store them
        _id = id;
        _name = name;
        _isCollidable = isCollidable;
    }
    
    
    
    /**
     * Returns the id for this world object
     *
     * @return The id
     */
    public Object getID()
    {
        return _id;
    }

    /**
     * Returns the name for this world object
     *
     * @return The name
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Returns the node for this world object
     *
     * @return The node
     */
    public Node getNode()
    {
        return _node;
    }

    /**
     * Returns the flag denoting whether or not this world object can collide
     * with others
     *
     * @return <code>true</code> if it can collide, otherwise, <code>false</code>
     */
    public boolean isCollidable()
    {
        return _isCollidable;
    }

    /**
     * Returns the associated agent
     *
     * @return The associated agent
     */
    public Agent getAgent()
    {
        return _agent;
    }
    
    /**
     * Sets the associated agent
     *
     * @param agent The associated agent
     */
    public void setAgent( Agent agent )
    {
        Validate.notNull( agent, "Agent may not be null" );
        _agent = agent;
    }
    
    /**
     * Returns the spatial category of this world object
     *
     * @return The spatial category
     */
    public SpatialCategory getSpatialCategory()
    {
        return _spatialCategory;
    }
    
    /**
     * Sets the spatial category of this world object
     *
     * @param spatialCategory The spatial category
     */
    public void setSpatialCategory( SpatialCategory spatialCategory )
    {
        Validate.notNull( spatialCategory, "Spatial category may not be null" );
        _spatialCategory = spatialCategory;
    }
    
    /**
     * Returns the unique key for this world object
     *
     * @return The unique key
     */
    public String getUniqueKey()
    {
        return _agent.getTeam().getID() + "." + getName(); 
    }
    
    /**
     * Compare this world object to another for order
     *
     * @param other The object to be compared
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( WorldObject other )
    {
        return getUniqueKey().compareTo( other.getUniqueKey() );
    }

    /**
     * Destroys this world object
     */
    public void destroy()
    {
        _LOG.trace( "Entering destroy()" );
        
        // Only proceed if we have an agent
        if( null != _agent )
        {
            _LOG.debug( "Destroying world object ["
                    + getUniqueKey()
                    + "]" );
            
            _agent.destroy();
            _agent = null;
            _node = null;
        }
        
        _LOG.trace( "Leaving destroy()" );

    }
}
