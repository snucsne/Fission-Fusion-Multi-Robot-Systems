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
package edu.snu.csne.forage.sensor;

// Imports
import java.util.List;
import java.util.Properties;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.SimulationState;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public interface AgentSensor
{
    /**
     * Initialize this agent sensor
     *
     * @param simState The state of the simulation
     * @param props The configuration properties
     */
    public void initialize( SimulationState simState, Properties props );
    
    /**
     * Senses agents in the environment
     *
     * @param agent The agent doing the sensing
     * @return The sensed agents
     */
    public List<Agent> sense( Agent agent );
    
    /**
     * Returns the maximum sensirng range of this sensor
     * 
     * @return The max sensing range
     */
    public float getMaxSensingRange();
}
