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

// Imports
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.snu.csne.mates.agent.Agent;
import edu.snu.csne.mates.agent.parameter.Parameter;
import edu.snu.csne.mates.agent.parameter.ParameterKey;
import edu.snu.csne.mates.sim.SimulationState;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public abstract class AbstractSensor implements Sensor
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            AbstractSensor.class.getName() );
    
    /** The unique identifier for this sensor */
    private Object _id = null;

    
    /**
     * Returns the unique identifier of this sensor
     *
     * @return The unique identifier
     * @see edu.snu.csne.mates.agent.sensor.Sensor#getID()
     */
    @Override
    public Object getID()
    {
        return _id;
    }

    /**
     * Stores the parameters for this behavior 
     *
     * @param params The parameters
     * @see edu.snu.csne.mates.agent.sensor.Sensor#storeParameters(java.util.Map)
     */
    @Override
    public void storeParameters( Map<ParameterKey, Parameter> params )
    {
        // Do nothing
    }

    /**
     * Initializes this behavior.
     *
     * @param agent The agent to which this behavior belongs
     * @param simState The current state of the simulation
     * @see edu.snu.csne.mates.agent.sensor.Sensor#initialize(edu.snu.csne.mates.agent.Agent, edu.snu.csne.mates.sim.SimulationState)
     */
    @Override
    public void initialize( Agent agent, SimulationState simState )
    {
        _LOG.trace( "Entering initialize( agent, simState )" );
        
        
        _LOG.trace( "Leaving initialize( agent, simState )" );
    }

}
