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
package edu.snu.csne.mates.agent.module;

//Imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.snu.csne.mates.agent.Agent;
import edu.snu.csne.mates.agent.behavior.Behavior;
import edu.snu.csne.mates.sim.SimulationState;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class BehaviorModule extends AbstractAgentModule
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            BehaviorModule.class.getName() );
    
    /** Mapping between a behavior's ID and the list of IDs of its
     * sub-behaviors */
    private Map<Object, List<Object>> _subBehaviors = 
            new HashMap<Object, List<Object>>();
    
    /** Mapping between a behavior's ID and the behavior */
    private Map<Object,Behavior> _behaviors = new HashMap<Object,Behavior>();
    
    /** The behaviors organized by levels into the hierarchy */
    private List<List<Behavior>> _hierarchy =
            new ArrayList<List<Behavior>>();

    
    /**
     * Adds the specified behavior to this module
     *
     * @param behavior The behavior
     * @param subBehaviorIDs Any sub-behaviors of the behavior
     */
    public void addBehavior( Behavior behavior, List<Object> subBehaviorIDs )
    {
        _LOG.trace( "Entering addBehavior( behavior, subBehaviorIDs )" );

        // Validate the behavior and the sub-behavior IDs
        Validate.notNull( behavior, "Behavior may not be null" );
        Validate.notNull( subBehaviorIDs, "List of sub-behavior IDs may not be null" );
        
        // Store them
        _behaviors.put( behavior.getID(), behavior );
        _subBehaviors.put( behavior.getID(), subBehaviorIDs );
        
        _LOG.debug( "Added behavior=["
                + behavior.getID()
                + "] with sub-behaviors=["
                + subBehaviorIDs
                + "]" );
        
        _LOG.trace( "Leaving addBehavior( behavior, subBehaviorIDs )" );
    }
    
    /**
     * Initializes this module
     *
     * @param agent The agent to which this module belongs
     * @param simState The current state of the simulation
     * @see edu.snu.csne.mates.agent.module.AbstractAgentModule#initialize(edu.snu.csne.mates.agent.Agent, edu.snu.csne.mates.sim.SimulationState)
     */
    @Override
    public void initialize( Agent agent, SimulationState simState )
    {
        _LOG.trace( "Entering initialize( agent, simState )" );

        // Call the superclass implementation
        super.initialize( agent, simState );
        
        // Build the hierarchy of behaviors
        buildHierarchy();
        
        // Initialize all the behaviors in the order determined by the hierarchy
        Iterator<List<Behavior>> hierarchyIter = _hierarchy.iterator();
        while( hierarchyIter.hasNext() )
        {
            Iterator<Behavior> behaviorIter = hierarchyIter.next().iterator();
            while( behaviorIter.hasNext() )
            {
                behaviorIter.next().initialize( agent, simState );
            }
        }

        _LOG.trace( "Leaving initialize( agent, simState )" );
    }

    /**
     * Executes this module
     *
     * @see edu.snu.csne.mates.agent.module.AbstractAgentModule#execute()
     */
    @Override
    public void execute()
    {
        _LOG.trace( "Entering execute()" );

        // Execute all the behaviors in the order determined by the hierarchy
        Iterator<List<Behavior>> hierarchyIter = _hierarchy.iterator();
        while( hierarchyIter.hasNext() )
        {
            Iterator<Behavior> behaviorIter = hierarchyIter.next().iterator();
            while( behaviorIter.hasNext() )
            {
                behaviorIter.next().execute();
            }
        }

        _LOG.trace( "Leaving execute()" );
    }

    /**
     * Destroys this module
     *
     * @see edu.snu.csne.mates.agent.module.AbstractAgentModule#destroy()
     */
    @Override
    public void destroy()
    {
        _LOG.trace( "Entering destroy()" );

        // Destroy all the behaviors
        Iterator<Behavior> behaviorIter = _behaviors.values().iterator();
        while( behaviorIter.hasNext() )
        {
            behaviorIter.next().destroy();
        }
        
        // Clear out all our mappings
        _behaviors.clear();
        _subBehaviors.clear();
        _hierarchy.clear();
        
        // Call the superclass implementation
        super.destroy();
        
        _LOG.trace( "Leaving destroy()" );
    }

    private void buildHierarchy()
    {
        _LOG.trace( "Entering buildHierarchy()" );

        // Get all the behavior ids
        Set<Object> behaviorIDSet = new TreeSet<Object>( _behaviors.keySet() );

        // Build the hierarchy while we have behaviors left to process
        while( !behaviorIDSet.isEmpty() )
        {
            /* Get a working copy of the set.  This set represents all the
             * behaviors not put into a behavior level of the hierarchy
             */
            Set<Object> workingIDSet = new TreeSet<Object>( behaviorIDSet );
            
            // Iterate over each behavior in the working set
            Iterator<Object> idIter = behaviorIDSet.iterator();
            while( idIter.hasNext() )
            {
                /* For each of these behaviors, get its sub-behaviors and
                 * remove them from the working set.  Since they depend on
                 * other behaviors, they won't be in this level.
                 */
                Iterator<Object> subBehaviorIDIter =
                        _subBehaviors.get( idIter.next() ).iterator();
                while( subBehaviorIDIter.hasNext() )
                {
                    workingIDSet.remove( subBehaviorIDIter.next() );
                }
            }

            // Whatever is left, goes in this level
            if( workingIDSet.isEmpty() )
            {
                // Oops.  We blowed up and removed all of the behaviors
                _LOG.error( "Unable to build hierarchy: Empty level found" );

                // TODO Throw a better exception
                throw new RuntimeException( "Unable to build hierarchy" );
            }
            else
            {
                // Add all the remaining behaviors to the current level
                List<Behavior> level =
                        new ArrayList<Behavior>( workingIDSet.size() );

                Iterator<Object> behaviorIDIter = workingIDSet.iterator();
                Behavior behavior = null;
                while( behaviorIDIter.hasNext() )
                {
                    // Get the behavior
                    behavior = _behaviors.get( behaviorIDIter.next() );

                    // Add it to this level
                    level.add( behavior );

                    // Remove its id from the set
                    behaviorIDSet.remove( behavior.getID() );
                }

                // Add the level to the hierarchy
                _hierarchy.add( level );

                // Log it
                if( _LOG.isDebugEnabled() )
                {
                    _LOG.debug( "Built level ["
                            + (_hierarchy.size() - 1)
                            + "] of hierarchy with ["
                            + level.size()
                            + "] behaviors." );
                }
            }
        }
        
        _LOG.trace( "Leaving buildHierarchy()" );
    }
}
