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

// Imports
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.snu.csne.mates.agent.module.AgentModule;
import edu.snu.csne.mates.sim.SimulationState;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class StandardAgent implements Agent
{
    
    /** Data class for combining a module and its rank */
    protected class RankedModule
    {
        /** The agent module itself */
        public final AgentModule module;

        /** The module's rank */
        public final int rank;

        /**
         * Builds this RankedModule object
         *
         * @param module The agent module
         * @param rank The module's rank
         */
        public RankedModule( AgentModule module, int rank )
        {
            this.module = module;
            this.rank = rank;
        }
    }

    /** Utility class for sorting ranked modules */
    protected class RankedModuleComparator implements Comparator<RankedModule>
    {
        public int compare( RankedModule obj1, RankedModule obj2 )
        {
            return obj1.rank - obj2.rank;
        }
    }

    
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            StandardAgent.class.getName() );
    
    /** This agent's unique identifier */
    private Object _id = null;
    
    /** This agent's name */
    private String _name = null;
    
    /** This agent's team */
    private AgentTeam _team = null;
    
    /** A set of all the modules for this agent ordered by their rank */
    private Set<RankedModule> _modules = new TreeSet<RankedModule>(
            new RankedModuleComparator() );

    /** This agent's blackboard */
    private AgentBlackboard _blackboard = new AgentBlackboard();
    
    /** Flag indicating whether or not this agent is active */
    private boolean _active = false;
    
    /** Flag indicating whether or not this agent has been initialized */
    private boolean _initialized = false;
    
    
    public StandardAgent( Object id, String name, AgentTeam team, boolean active )
    {
        // Validate and store the id
        Validate.notNull( id, "ID may not be null" );
        _id = id;
        
        // Validate and store the name
        Validate.notBlank( name, "Name may not be blank" );
        _name = name;
        
        // Validate and store the team
        Validate.notNull( team, "Team may not be null" );
        _team = team;
        
        // Store the active flag
        _active = active;
    }
    
    /**
     * Adds a module to this agent
     *
     * @param module The agent module to add
     * @param rank The rank of the module with respect to the other modules
     * @see edu.snu.csne.mates.agent.Agent#addModule(edu.snu.csne.mates.agent.module.AgentModule, int)
     */
    @Override
    public void addModule( AgentModule module, int rank )
    {
        // Validate the module and rank
        Validate.notNull( module, "Module may not be null" );
        Validate.isTrue( rank > 0, "Rank must be positive" );
        
        // Store the module
        _modules.add( new RankedModule( module, rank ) );
        
        _LOG.debug( "Added module ["
                + module.toString()
                + "] with rank ["
                + rank
                + "] to agent ["
                + getFullName()
                + "]" );
    }


    /**
     * Returns the unique ID of this agent
     *
     * @return The unique ID
     * @see edu.snu.csne.mates.agent.Agent#getID()
     */
    @Override
    public Object getID()
    {
        return _id;
    }

    /**
     * Returns the name of this agent
     *
     * @return The name
     * @see edu.snu.csne.mates.agent.Agent#getName()
     */
    @Override
    public String getName()
    {
        return _name;
    }
    
    /**
     * Returns the full name of this agent
     * 
     * @return The full name
     * @see edu.snu.csne.mates.agent.Agent#getFullName()
     */
    @Override
    public String getFullName()
    {
        return _team.getFullName() + "." + getName();
    }


    /**
     * Returns the team of this agent
     *
     * @return The team
     * @see edu.snu.csne.mates.agent.Agent#getTeam()
     */
    @Override
    public AgentTeam getTeam()
    {
        return _team;
    }

    
    /**
     * Returns the blackboard for this agent
     *
     * @return The blackboard
     * @see edu.snu.csne.mates.agent.Agent#getBlackboard()
     */
    @Override
    public AgentBlackboard getBlackboard()
    {
        return _blackboard;
    }

    /**
     * Initializes this agent
     *
     * @param simState The simulation state
     * @see edu.snu.csne.mates.agent.Agent#initialize(edu.snu.csne.mates.sim.SimulationState)
     */
    public void initialize( SimulationState simState )
    {
        _LOG.trace( "Entering initialize( simState )" );

        _LOG.debug( "Initializing agent ["
                + getFullName()
                + "]" );
        
        // Initialize all the modules
        Iterator<RankedModule> moduleIter = _modules.iterator();
        while( moduleIter.hasNext() )
        {
            moduleIter.next().module.initialize( this, simState );
        }
        
        _LOG.trace( "Leaving initialize( simState )" );
    }

    /**
     * Executes this agent
     *
     * @see edu.snu.csne.mates.agent.Agent#execute()
     */
    @Override
    public void execute()
    {
        // Execute all the modules
        AgentModule module = null;
        Iterator<RankedModule> moduleIter = _modules.iterator();
        while( moduleIter.hasNext() )
        {
            // Execute the module
            module = moduleIter.next().module;
            module.execute();
        }
    }

    /**
     * Destroys this agent
     *
     * @see edu.snu.csne.mates.agent.Agent#destroy()
     */
    @Override
    public void destroy()
    {
        _LOG.trace( "Entering destroy()" );

        // Only destroy if it is active
        if( isActive() )
        {
            _LOG.debug( "Destroying agent=["
                    + getFullName()
                    + "]" );
            
            // Destroy all the modules
            Iterator<RankedModule> moduleIter = _modules.iterator();
            while( moduleIter.hasNext() )
            {
                moduleIter.next().module.destroy();
            }
            
            // Get rid of all the modules
            _modules.clear();
            
            // Clear the blackboard
            if( null != _blackboard )
            {
                _blackboard.clear();
                _blackboard = null;
            }
            
            // We aren't active anymore
            _active = false;
        }
        
        _LOG.trace( "Leaving destroy()" );
    }

    /**
     * Indicates whether or not the agent is active
     *
     * @return <code>true</code> if the agent is active, otherwise,
     * <code>false</code>
     * @see edu.snu.csne.mates.agent.Agent#isActive()
     */
    @Override
    public boolean isActive()
    {
        return _active;
    }

    /**
     * Indicates whether or not the agent has been initialized
     *
     * @return <code>true</code> if the agent has been initialized, otherwise,
     * <code>false</code>
     * @see edu.snu.csne.mates.agent.Agent#isInitialized()
     */
    @Override
    public boolean isInitialized()
    {
        return _initialized;
    }

    /**
     * 
     * Compares this agent to the other, specified agent
     *
     * @param other THe other agent object
     * @return <code>true</code> if the agents are equal, otherwise,
     * <code>false</code>
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( Agent other )
    {
        // Compare the names
        return getFullName().compareTo( other.getFullName() );
    }


}
