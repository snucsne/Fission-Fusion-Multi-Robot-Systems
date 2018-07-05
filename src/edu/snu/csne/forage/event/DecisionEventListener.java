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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.forage.decision.Decision;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class DecisionEventListener extends AbstractSimulationEventListener
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            DecisionEventListener.class.getName() );
    
    /** Key for the decision history file */
    private static final String _DECISION_HISTORY_FILE_KEY =
            "decision-history-file";
    
    /** Spacer comment for position history file */
    private static final String _SPACER =
            "# =========================================================";

    /** The writer to which the position history is written */
    private PrintWriter _writer = null;

    /** A list of all the decisions made by agents */
    private List<DecisionEvent> _decisionHistory =
            new LinkedList<DecisionEvent>();
    
    /** The current state of the simulation */
    private SimulationState _simState = null;

    
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
        _simState = simState;
        
        // Grab the properties
        Properties props = simState.getProps();

        // Load the filename
        String historyFilename = props.getProperty( _DECISION_HISTORY_FILE_KEY );
        Validate.notEmpty( historyFilename,
                "Decision history filename may not be empty ["
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
     * Performs any processing necessary to handle an agent making a decision
     * @param event The decision
     * 
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#agentDecided(edu.snu.csne.forage.event.DecisionEvent)
     */
    @Override
    public void agentDecided( DecisionEvent event )
    {
        _LOG.trace( "Entering agentDecided( event )" );

        // Simply store it for later
        _decisionHistory.add( event );
        
        _LOG.trace( "Leaving agentDecided( event )" );
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

        // Write out the decision history
        _writer.println( _SPACER );
        
        // Iterate through each of the decisions
        Iterator<DecisionEvent> decisionIter = _decisionHistory.iterator();
        while( decisionIter.hasNext() )
        {
            DecisionEvent currentEvent = decisionIter.next();
            Decision currentDecision = currentEvent.getDecision();
            
            _writer.printf( "%010d   %10s  %10s\n",
                    currentEvent.getTimestep(),
                    currentEvent.getAgent().getID(),
                    currentDecision.getType() );
        }
        
        _writer.println();
        _writer.println();
        
        // Close the results writer
        _writer.close();

        _LOG.trace( "Leaving simTearDown()" );
    }
    
    
}
