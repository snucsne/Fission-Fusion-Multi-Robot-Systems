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
import java.util.Properties;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.util.MiscUtils;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public abstract class AbstractPatchSensor implements PatchSensor
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            AbstractPatchSensor.class.getName() );
    
    /** Property key for the patch sensing distance */
    protected static final String _SENSING_DISTANCE_KEY = "patch-sensing-distance";

    
    /** The state of the simulation */
    protected SimulationState _simState = null;
    
    /** The distance at which patches are detected */
    protected float _sensingDistance = Float.POSITIVE_INFINITY;
    
    /** The square of the sensing distance to speed calculations */
    protected float _sensingDistanceSquared = Float.POSITIVE_INFINITY;

    
    
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

        // Validate the simulation state and the properties
        Validate.notNull( simState, "Simulation state may not be null" );
        Validate.notNull( props, "Configuration properties may not be null" );
        
        // Save the simulation state
        _simState = simState;
        
        // Get the sensing distance
        _sensingDistance = MiscUtils.loadNonEmptyFloatProperty( props,
                _SENSING_DISTANCE_KEY,
                "Patch sensing distance value is required" );
        if( 0.0f > _sensingDistance )
        {
            _sensingDistance = Float.POSITIVE_INFINITY;
        }
        else
        {
            _sensingDistanceSquared = _sensingDistance * _sensingDistance;
        }
        
        _LOG.trace( "Leaving initialize( simState, props )" );
    }
}
