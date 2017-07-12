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

import edu.snu.csne.forage.SimulationState;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public interface SimulationEventListener
{
    /**
     * Initializes this event listener
     *
     * @param simState The simulation state
     */
    public void initialize( SimulationState simState );

    /**
     * Prepares the simulation for execution
     */
    public void simSetup();

    /**
     * Prepares a simulation run for execution
     */
    public void simRunSetup();

    /**
     * Prepares for a simulation step
     */
    public void simStepSetup();

    /**
     * Performs any cleanup after a simulation step
     * */
    public void simStepTearDown();
    
    /**
     * Performs any cleanup after a simulation run has finished execution
     */
    public void simRunTearDown();

    /**
     * Performs any cleanup after the simulation has finished execution
     */
    public void simTearDown();

    /**
     * Performs any necessary cleanup and teardown
     */
    public void cleanup();
    
    /**
     * Performs any processing necessary to handle an agent making a decision
     * 
     * @param event The decision
     */
    public void agentDecided( DecisionEvent event );

}
