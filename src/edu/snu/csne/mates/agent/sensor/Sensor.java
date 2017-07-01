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
package edu.snu.csne.mates.agent.sensor;

import java.util.Map;

// Imports
import edu.snu.csne.mates.agent.Agent;
import edu.snu.csne.mates.agent.parameter.Parameter;
import edu.snu.csne.mates.agent.parameter.ParameterKey;
import edu.snu.csne.mates.sim.SimulationState;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public interface Sensor
{
    /**
     * Returns the unique identifier of this sensor
     *
     * @return The unique identifier
     */
    public Object getID();
    
    /**
     * Stores the parameters for this behavior 
     *
     * @param params The parameters
     */
    public void storeParameters( Map<ParameterKey, Parameter> params );
    
    /**
     * Initializes this behavior.
     *
     * @param agent The agent to which this behavior belongs
     * @param simState The current state of the simulation
     */
    public void initialize( Agent agent, SimulationState simState );
    
    /**
     * Executes the sensing functionality for this sensor
     */
    public void sense();

}
