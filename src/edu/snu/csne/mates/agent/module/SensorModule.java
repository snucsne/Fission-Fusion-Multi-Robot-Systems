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

// Imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.snu.csne.mates.agent.Agent;
import edu.snu.csne.mates.agent.sensor.Sensor;
import edu.snu.csne.mates.sim.SimulationState;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class SensorModule extends AbstractAgentModule
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            SensorModule.class.getName() );
    
    /** A mapping between sensor id -> pre-requisite sensor ids */
    private Map<Object, List<Object>> _prerequisiteSensors =
            new HashMap<Object, List<Object>>();
    
    /** A mapping between sensor id -> sensor */
    private Map<Object, Sensor> _sensors = new HashMap<Object, Sensor>();
    
    /** The sensors organized by levels into the hierarchy */
    private List<List<Sensor>> _heierarchy =
            new ArrayList<List<Sensor>>();


    /**
     * Adds a sensor to this module
     *
     * @param sensor The sensor to add
     * @param prereqSensorIDs The list of pre-requisite sensor ids
     */
    public void addSensor( Sensor sensor, List<Object> prereqSensorIDs )
    {
        _LOG.trace( "Entering addSensor( sensor, prereqSensorIDs )" );

        // Validate the parameters
        Validate.notNull( sensor, "Sensor may not be null" );
        Validate.notNull( prereqSensorIDs, "Pre-requisite sensor IDs may not be null" );
        
        // Store them
        _sensors.put( sensor.getID(), sensor );
        _prerequisiteSensors.put( sensor.getID(), prereqSensorIDs );
        
        // Log it
        if( _LOG.isDebugEnabled() )
        {
            _LOG.debug( "Added sensor=["
                    + sensor
                    + "] with id=["
                    + sensor.getID()
                    + "] and pre-req sensor ids=["
                    + prereqSensorIDs
                    + "]" );
        }
        
        _LOG.trace( "Leaving addSensor( sensor, prereqSensorIDs )" );
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

        // Build the hierarchy
        buildHierarchy();
        
        // Initialize the sensors
        Iterator<List<Sensor>> hierarchyIter = _heierarchy.iterator();
        while( hierarchyIter.hasNext() )
        {
            Iterator<Sensor> sensorIter = hierarchyIter.next().iterator();
            while( sensorIter.hasNext() )
            {
                sensorIter.next().initialize( agent, simState );
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
        
        // Have all the sensors sense the environment in order
        Iterator<List<Sensor>> hierarchyIter = _heierarchy.iterator();
        while( hierarchyIter.hasNext() )
        {
            Iterator<Sensor> sensorIter = hierarchyIter.next().iterator();
            while( sensorIter.hasNext() )
            {
                sensorIter.next().sense();
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

        // Do nothing
        _LOG.debug( "Nothing to destroy" );
        
        _LOG.trace( "Leaving destroy()" );
    }
    
    /**
     * Builds the sensor hierarchy
     */
    private void buildHierarchy()
    {
        _LOG.trace( "Entering buildHierarchy()" );

        // Get all the sensor ids
        Set<Object> sensorIDSet = new TreeSet<Object>( _sensors.keySet() );
        
        // Build the hierarchy in reverse (it is easier that way)
        Stack<List<Sensor>> reverseHierarchy = new Stack<List<Sensor>>();
        while( !sensorIDSet.isEmpty() )
        {
            // Get a working copy of the set
            Set<Object> workingIDSet = new TreeSet<Object>( sensorIDSet );
            
            // Iterate over each behavior in the id set
            Iterator<Object> idIter = sensorIDSet.iterator();
            while( idIter.hasNext() )
            {
                /* For each of these sensors, get its pre-req sensors and
                 * remove them from the working set.  Since they are before
                 * sensors in this level, they won't be in this one.
                 */
                Object currentID = idIter.next();
                Iterator<Object> prereqSensorIDIter =
                        _prerequisiteSensors.get( currentID ).iterator();
                while( prereqSensorIDIter.hasNext() )
                {
                    workingIDSet.remove( prereqSensorIDIter.next() );
                }
            }
            
            // Whatever is left, goes in this level
            if( workingIDSet.isEmpty() )
            {
                // Oops.  We done blowed up and removed all the sensors
                _LOG.error( "Unable to build hierarchy: Empty level found" );
                throw new RuntimeException( "Unable to build sensor hierarchy" );
            }
            else
            {
                // Add all the remaining behaviors to the current level
                List<Sensor> level = new ArrayList<Sensor>( workingIDSet.size() );
                Iterator<Object> sensorIDIter = workingIDSet.iterator();
                Sensor sensor = null;
                while( sensorIDIter.hasNext() )
                {
                    // Get the sensor
                    sensor = _sensors.get( sensorIDIter.next() );
                    
                    // Add it to this level
                    level.add( sensor );
                    
                    // Remove its id from teh set
                    sensorIDSet.remove( sensor.getID() );
                }
                
                // Add the level to the reverse hierarchy
                reverseHierarchy.push( level );
            }
        }
        
        // Now that we have the reverse hierarchy, put it in the real one
        List<Sensor> current = null;
        while( !reverseHierarchy.isEmpty() )
        {
            current = reverseHierarchy.pop();
            _heierarchy.add( current );
            
            // Log it
            if( _LOG.isDebugEnabled() )
            {
                _LOG.debug( "Built level ["
                        + (_heierarchy.size() - 1)
                        + "] of hierarchy with ["
                        + current.size()
                        + "] sensors." );
            }
        }
        
        _LOG.trace( "Leaving buildHierarchy()" );
    }
}
