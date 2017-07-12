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

// Imports
import org.apache.commons.lang3.Validate;

import edu.snu.csne.forage.SimulationState;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public abstract class AbstractSimulationEventListener
        implements SimulationEventListener
{
    /** The current simulation state */
    protected SimulationState _simState = null;
    
    /**
     * Initializes this event listener
     *
     * @param simState The simulation state
     * @see edu.snu.csne.forage.event.SimulationEventListener#initialize(edu.snu.csne.forage.SimulationState)
     */
    @Override
    public void initialize( SimulationState simState )
    {
        // Validate the parameter
        Validate.notNull( simState, "Simulation State may not be null" );
        _simState = simState;
    }

    /**
     * Prepares the simulation for execution
     *
     * @see edu.snu.csne.forage.event.SimulationEventListener#simSetup()
     */
    @Override
    public void simSetup()
    {
        // Do nothing
    }

    /**
     * Prepares a simulation run for execution
     *
     * @see edu.snu.csne.forage.event.SimulationEventListener#simRunSetup()
     */
    @Override
    public void simRunSetup()
    {
        // Do nothing
    }

    /**
     * Prepares for a simulation step
     *
     * @see edu.snu.csne.forage.event.SimulationEventListener#simStepSetup()
     */
    @Override
    public void simStepSetup()
    {
        // Do nothing
    }

    /**
     * Performs any cleanup after a simulation step
     *
     * @see edu.snu.csne.forage.event.SimulationEventListener#simStepTearDown()
     */
    @Override
    public void simStepTearDown()
    {
        // Do nothing
    }

    /**
     * Performs any cleanup after a simulation run has finished execution
     *
     * @see edu.snu.csne.forage.event.SimulationEventListener#simRunTearDown()
     */
    @Override
    public void simRunTearDown()
    {
        // Do nothing
    }

    /**
     * Performs any cleanup after the simulation has finished execution
     *
     * @see edu.snu.csne.forage.event.SimulationEventListener#simTearDown()
     */
    @Override
    public void simTearDown()
    {
        // Do nothing
    }

    /**
     * Performs any necessary cleanup and teardown
     *
     * @see edu.snu.csne.forage.event.SimulationEventListener#cleanup()
     */
    @Override
    public void cleanup()
    {
        // Do nothing
    }

    /**
     * Performs any processing necessary to handle an agent making a decision
     * @param event The decision
     *
     * @see edu.snu.csne.forage.event.SimulationEventListener#agentDecided(edu.snu.csne.forage.event.DecisionEvent)
     */
    @Override
    public void agentDecided( DecisionEvent event )
    {
        // Do nothing
    }

}
