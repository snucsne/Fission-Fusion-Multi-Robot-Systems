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

//Imports
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jme3.math.Vector3f;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.AgentTeam;
import edu.snu.csne.forage.SimulationState;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class PositionEventListener extends AbstractSimulationEventListener
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            PositionEventListener.class.getName() );

    /** Key for the position history file */
    private static final String _POSITION_HISTORY_FILE_KEY =
            "position-history-file";
    
    /** Spacer comment for position history file */
    private static final String _SPACER =
            "# =========================================================";

    
    private class PositionUpdate
    {
        public final Vector3f position;
        public final long timestep;
        public final String agentID;
        public final boolean active;
        
        public PositionUpdate( Vector3f position,
                long timestep,
                String agentID,
                boolean active)
        {
            this.position = new Vector3f( position );
            this.timestep = timestep;
            this.agentID = agentID;
            this.active = active;
        }
    }
    
    /** The writer to which the position history is written */
    private PrintWriter _writer = null;

    /** A history of all the position updates */
    private Map<Long, List<PositionUpdate>> _positionHistory =
            new TreeMap<Long, List<PositionUpdate>>();
    
    
    /**
     * Initializes this event listener
     *
     * @param simState The simulation state
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#initialize(edu.snu.csne.forage.SimulationState)
     */
    @Override
    public void initialize( SimulationState simState )
    {
        _LOG.trace( "Entering initialize( simState )" );

        // Call the superclass implementation
        super.initialize( simState );
        
        // Grab the properties
        Properties props = simState.getProps();

        // Load the filename
        String historyFilename = props.getProperty( _POSITION_HISTORY_FILE_KEY );
        Validate.notEmpty( historyFilename,
                "Position history filename may not be empty ["
                + historyFilename
                + "]" );
        
        // Create the writer
        try
        {
            _writer = new PrintWriter( new BufferedWriter(
                    new FileWriter( historyFilename ) ) );
        }
        catch( IOException ioe )
        {
            _LOG.error( "Unable to open position history file ["
                    + historyFilename
                    + "]", ioe );
            throw new RuntimeException( "Unable to open position history file ["
                    + historyFilename
                    + "]", ioe );
        }
        
        // Log the system properties to the stats file for future reference
        _writer.println( "# Started: " + (new Date()) );
        _writer.println( _SPACER );
        _writer.println( "# Simulation properties" );
        _writer.println( _SPACER );
        List<String> keyList = new ArrayList<String>(
                props.stringPropertyNames() );
        Collections.sort( keyList );
        Iterator<String> iter = keyList.iterator();
        while( iter.hasNext() )
        {
            String key = iter.next();
            String value = props.getProperty( key );

            _writer.println( "# " + key + " = " + value );
        }
        _writer.println( _SPACER );
        _writer.println();
        _writer.flush();


        _LOG.trace( "Leaving initialize( simState )" );
    }

    /**
     * Prepares a simulation run for execution
     *
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#simRunSetup()
     */
    @Override
    public void simRunSetup()
    {
        // Get all the agents
        Map<String,Agent> agents = _simState.getAllAgents();
        
        // Create the list for all the positions
        Long timestep = new Long( 0 );
        List<PositionUpdate> positions = new ArrayList<PositionUpdate>( agents.size() );
        _positionHistory.put( timestep, positions );
        
        // Log all the initial positions of the agents
        Iterator<Agent> agentIter = agents.values().iterator();
        while( agentIter.hasNext() )
        {
            Agent current = agentIter.next();

            // Add the agents current position
            positions.add( new PositionUpdate(
                    current.getPosition(),
                    timestep.longValue(),
                    current.getID(),
                    current.isActive() ) );
        }
    }

    /**
     * Performs any cleanup after a simulation step
     *
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#simStepTearDown()
     */
    @Override
    public void simStepTearDown()
    {
        // Get all the agents
        Map<String,Agent> agents = _simState.getAllAgents();
        
        // Create the list for all the positions
        Long timestep = new Long( _simState.getCurrentSimulationStep() );
        List<PositionUpdate> positions = new ArrayList<PositionUpdate>( agents.size() );
        _positionHistory.put( timestep, positions );
        
        if( 0 == timestep )
        {
            _LOG.warn( "TIMESTEP 0 ENCOUNTERED" );
        }

        // Log all the initial positions of the agents
        Iterator<Agent> agentIter = agents.values().iterator();
        while( agentIter.hasNext() )
        {
            Agent current = agentIter.next();
            
            // Add the agents current position
            positions.add( new PositionUpdate(
                    current.getPosition(),
                    timestep.longValue(),
                    current.getID(),
                    current.isActive() ) );
        }
    }

    /**
     * Performs any cleanup after the simulation has finished execution
     *
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#simTearDown()
     */
    @Override
    public void simTearDown()
    {
        _LOG.trace( "Entering simTearDown()" );

        // Write out the position history
        _writer.println( _SPACER );
        
        // Iterate through the position history by timestep
        Iterator<Long> timestepIter = _positionHistory.keySet().iterator();
        while( timestepIter.hasNext() )
        {
            Long timestep = timestepIter.next();
            List<PositionUpdate> updates = _positionHistory.get( timestep );
            Iterator<PositionUpdate> updateIter = updates.iterator();
            while( updateIter.hasNext() )
            {
                PositionUpdate update = updateIter.next();
                _writer.printf( "%06d  %s  %-20s  %+09.3f  %+09.3f  %+09.3f  %1d\n",
                        update.timestep,
                        update.agentID,
                        update.position.x,
                        update.position.y,
                        update.position.z,
                        (update.active ? 1 : 0) );
            }
            
            _writer.println();
            _writer.println();
        }
        
        // Close the results writer
        _writer.close();

        _LOG.trace( "Leaving simTearDown()" );
    }

    
    
}
