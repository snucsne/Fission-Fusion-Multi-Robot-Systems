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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.snu.csne.forage.event.SimulationEventListener;
import edu.snu.csne.util.MiscUtils;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class Simulator
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            Simulator.class.getName() );
    
    /** Key for simulation properties file */
    private static final String _PROPS_FILE_KEY = "sim-properties";

    
    
    /** The current state of the simulation */
    private SimulationState _simState = null;
    
    /** The properties used to initialize the system */
    private Properties _props = new Properties();

    /**
     * Main entry into the simulation
     *
     * @param args
     */
    public static void main( String[] args )
    {
        try
        {
            _LOG.debug( "Starting simulation..." );

            // Build, initialize, run
            Simulator sim = new Simulator();
            sim.initialize();
            sim.run();
        }
        catch( Exception e )
        {
            _LOG.error( "Unknown error", e );
            System.exit(1);
        }
    }

    /**
     * Initializes this simulator
     */
    public void initialize()
    {
        _LOG.trace( "Entering initialize()" );
        
        // Load the properties
        _props = MiscUtils.loadProperties( _PROPS_FILE_KEY );

        // Initialize the simulation state
        _simState = new SimulationState();
        _simState.initialize( _props );

        // Signal that the simulator is about ready to start
        _simState.signalSimSetup();

        _LOG.trace( "Leaving initialize()" );
    }
    
    public void run()
    {
        _LOG.trace( "Entering run()" );

        while( !_simState.isSimFinished() )
        {
            // Signal the start of a simulation
            _simState.signalSimRunSetup();
            
            // Run through each step of the simulation
            while( !_simState.isRunFinished() )
            {
                // Signal the start of a simulation step
                _simState.signalSimStepSetup();
                
                // Run a step of the simulation
                runSimStep();
                
                // Signal the end of a simulation step
                _simState.signalSimStepTeardown();
            }
            
            // Signal the end of a simualation
            _simState.signalSimRunTeardown();
        }

        // Signal that the simulation is finishing
        _simState.signalSimTeardown();
        
        // Perform any additional cleanup
        _simState.signalCleanup();

        _LOG.trace( "Leaving run()" );
    }
    
    
    /**
     * Runs a single simulation step
     */
    public void runSimStep()
    {
        _LOG.trace( "Entering runSimStep()" );

        // Get all the agents
        Map<String,Agent> agentsMap = _simState.getAllAgents();
        Collection<Agent> agents = agentsMap.values();
        
        // Execute the sensing
        Iterator<Agent> agentIter = agents.iterator();
        while( agentIter.hasNext() )
        {
            Agent current = agentIter.next();
            current.sense();
            current.plan();
            current.act();
//            agentIter.next().sense();
        }
        
//        // Execute the planning
//        agentIter = agents.iterator();
//        while( agentIter.hasNext() )
//        {
//            agentIter.next().plan();
//        }
//        
//        // Execute the acting
//        agentIter = agents.iterator();
//        while( agentIter.hasNext() )
//        {
//            agentIter.next().act();
//        }
        
        _LOG.trace( "Leaving runSimStep()" );
    }
    

}
