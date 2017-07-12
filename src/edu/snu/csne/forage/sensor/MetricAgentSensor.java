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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.math.Vector3f;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.SimulationState;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class MetricAgentSensor extends AbstractAgentSensor
    implements AgentSensor
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            MetricAgentSensor.class.getName() );
    
    
    /**
     * Initialize this agent sensor
     *
     * @param simState The state of the simulation
     * @param props The configuration properties
     * @see edu.snu.csne.forage.sensor.AbstractAgentSensor#initialize(edu.snu.csne.forage.SimulationState, java.util.Properties)
     */
    @Override
    public void initialize( SimulationState simState, Properties props )
    {
        _LOG.trace( "Entering initialize( simState, props )" );

        // Call the superclass implementation
        super.initialize( simState, props );
        
        // No real need to override this since we don't do anything special
        // But we might need to do it soon, so I went ahead and created it

        _LOG.trace( "Leaving initialize( simState, props )" );
    }

    /**
     * Initialize this agent sensor
     *
     * @param simState The state of the simulation
     * @param props The configuration properties
     * @see edu.snu.csne.forage.sensor.AgentSensor#initialize(edu.snu.csne.forage.SimulationState, java.util.Properties)
     */
    @Override
    public List<Agent> sense( Agent agent )
    {
        List<Agent> sensedAgents = new LinkedList<Agent>();
        
        Vector3f position = agent.getPosition();
        
        // Get all the agents within sensing range
        Iterator<Agent> agentIter = _simState.getAllAgents().values().iterator();
        while( agentIter.hasNext() )
        {
            Agent current = agentIter.next();
            
            // Exclude ourselves and check range
            if( !current.getID().equals(agent.getID())
                    && (_sensingDistanceSquared > position.distanceSquared( current.getPosition() )) )
            {
                // It is in range
                sensedAgents.add( current );
//                _LOG.debug( "Agent ["
//                        + agent.getID()
//                        + "] sensed agent ["
//                        + current.getID()
//                        + "]" );
            }
        }
        
//        _LOG.debug( "Sensed ["
//                + sensedAgents.size()
//                + "] agents" );
        
        return sensedAgents;
    }

}
