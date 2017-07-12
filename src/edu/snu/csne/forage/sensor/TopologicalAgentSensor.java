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

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.Validate;
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
public class TopologicalAgentSensor extends AbstractAgentSensor
        implements AgentSensor
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            TopologicalAgentSensor.class.getName() );
    
    
    /**
     * TODO Class description
     */
    private class AgentDistanceComparator implements Comparator
    {
        /** The central sensing agent */
        private Agent _sensingAgent = null;
        
        /**
         * Builds this TopologicalAgentSensor.AgentDistanceComparator object
         *
         * @param sensingAgent The central agent that sensed the other agents
         */
        public AgentDistanceComparator( Agent sensingAgent )
        {
            Validate.notNull( sensingAgent, "Sensing agent may not be null" );
            _sensingAgent = sensingAgent;
        }
        
        /**
         * Compares two agents by virtue of their distance from the sensing
         * agent.
         *
         * @param o1
         * @param o2
         * @return
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare( Object o1, Object o2 )
        {
            Agent agent1 = (Agent) o1;
            float distance1 = agent1.getPosition().distance( _sensingAgent.getPosition() );
            Agent agent2 = (Agent) o2;
            float distance2 = agent2.getPosition().distance( _sensingAgent.getPosition() );
            
            int result = 0;
            if( distance1 < distance2 )
            {
                result = -1;
            }
            else if( distance1 > distance2 )
            {
                result = 1;
            }
            
            return result;
        }
        
    }
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
        SortedSet<Agent> sortedAgents = new TreeSet<Agent>(
                new AgentDistanceComparator( agent ) );
        
        Vector3f position = agent.getPosition();
        
        // Get all the agents within sensing range
        Iterator<Agent> agentIter = _simState.getAllAgents().values().iterator();
        while( agentIter.hasNext() )
        {
            Agent current = agentIter.next();
            
            // Is it within range?
            if( _sensingDistanceSquared > position.distanceSquared( current.getPosition() ) )
            {
                // Yes
                sortedAgents.add( current );
            }
        }
        
        List<Agent> sensedAgents = new LinkedList<Agent>( sortedAgents );

        return sensedAgents;
    }

}
