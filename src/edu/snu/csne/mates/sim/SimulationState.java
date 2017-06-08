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
package edu.snu.csne.mates.sim;

// Imports
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jme3.app.SimpleApplication;
import edu.snu.csne.mates.agent.AgentLibrary;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class SimulationState
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            SimulationState.class.getName() );
    
    
    /** The main game engine */
    private SimpleApplication _engine = null;
    
    /** Simulation configuration */
    private SimulationConfiguration _simConfig = null;
    
    /** Library that manages all agents */
    private AgentLibrary _agentLib = new AgentLibrary();
    
    /** The current time step in the simulation */
    private long _currentTimeStep = 0;
    
    /** The maximum number of steps in the simulation */
    private long _maxTimeSteps = Long.MAX_VALUE;
    
    
    /**
     * Initializes the simulation state
     * 
     * @param engine The simulation engine
     * @param simConfig The configuration of the simulator
     */
    public void initiatize( SimpleApplication engine,
            SimulationConfiguration simConfig )
    {
        _LOG.trace( "Entering initiatize( SimpleApplication, SimulationConfiguration )" );
        
        // Save the engine
        Validate.notNull( engine, "Simulator engine may not be null" );
        _engine = engine;

        // Save the configuration
        Validate.notNull( simConfig, "Simulator configuration may not be null" );
        _simConfig = simConfig;
        
        // Get the maximum number of time steps
        _maxTimeSteps = 0;
        
        
        _LOG.trace( "Leaving initiatize( SimpleApplication, SimulationConfiguration )" );
    }
    
    /**
     * Increments the time step and returns the new value
     *
     * @return The current time step
     */
    public long incrementTimeStep()
    {
        ++_currentTimeStep;
        
        // Have we reached the maximum number?
        if( _currentTimeStep > _maxTimeSteps )
        {
            // Yup, log it and signal the engine
            _LOG.info( "Max timesteps reached: ["
                    + _currentTimeStep
                    + "] > ["
                    + _maxTimeSteps
                    + "]" );
            _engine.stop();
        }
        
        return _currentTimeStep;
    }
    
    /**
     * Returns the configuration for this simulator
     *
     * @return The simulator's configuration
     */
    public SimulationConfiguration getConfig()
    {
        return _simConfig;
    }
    
    /**
     * Returns the agent library for this simulation
     *
     * @return The agent library
     */
    public AgentLibrary getAgentLibrary()
    {
        return _agentLib;
    }
}
