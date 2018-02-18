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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.math.Vector3f;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.forage.SimulationState;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class MetricPatchSensor extends AbstractPatchSensor
        implements PatchSensor
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            MetricPatchSensor.class.getName() );
    
    /**
     * Initialize this patch sensor
     *
     * @param simState The state of the simulation
     * @param props The configuration properties
     * @see edu.snu.csne.forage.sensor.PatchSensor#initialize(edu.snu.csne.forage.SimulationState, java.util.Properties)
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
     * Senses patches in the environment
     *
     * @param agent The agent doing the sensing
     * @return The sensed patches
     * @see edu.snu.csne.forage.sensor.PatchSensor#sense(edu.snu.csne.forage.Agent)
     */
    @Override
    public List<Patch> sense( Agent agent )
    {
        List<Patch> sensedPatches = new LinkedList<Patch>();
        
        Vector3f position = agent.getPosition();
        
        // Get all the agents within sensing range
        Iterator<Patch> patchIter = _simState.getAllPatches().values().iterator();
        while( patchIter.hasNext() )
        {
            Patch current = patchIter.next();
            
            // Is it within range?
            if( _sensingDistanceSquared > position.distanceSquared( current.getPosition() ) )
            {
                // Yes
                sensedPatches.add( current );
            }
        }
        
        return sensedPatches;
    }

}
