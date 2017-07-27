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
package edu.snu.csne.forage;

// Imports
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.math.Vector3f;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class Patch
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            Patch.class.getName() );
    
    /** This patch's unique ID */
    private String _id = null;
    
    /** This patch's position */
    private Vector3f _position = Vector3f.ZERO;
    
    /** This patch's radius */
    private float _radius = 0.0f;
    
    /** The initial amount of resources */
    private float _initialResources = 0.0f;
    
    /** The amount of resources at this patch */
    private float _remainingResources = 0.0f;
    
    /** The probability of predation at this patch */
    private float _predationProbability = 0.0f;
    
    /** The minimum number of agents required for foraging to succeed */
    private int _minAgentForageCount = 0;
    
    
    /**
     * Builds this Patch object
     *
     * @param id
     * @param position
     * @param radius
     * @param initialResources
     * @param predationProbability
     * @param minAgentForageCount
     */
    public Patch( String id,
            Vector3f position,
            float radius,
            float initialResources,
            float predationProbability,
            int minAgentForageCount )
    {
        // Validate and store
        Validate.notEmpty( id, "Patch ID may not be null or empty" );
        Validate.notNull( position, "Patch position may not be null" );
        Validate.isTrue( 0.0f < radius,
                "Patch radius must be positive" );
        Validate.isTrue( 0.0f < initialResources,
                "Patch initial resources must be positive" );
        Validate.inclusiveBetween( 0.0f, 1.0f, predationProbability,
                "Predation probability must lie in [0,1]: value=["
                + predationProbability
                + "]" );
        Validate.isTrue( 0 <= minAgentForageCount,
                "Minimum agent foraging count must be non-negative" );
        _id = id;
        _position = position;
        _radius = radius;
        _initialResources = initialResources;
        _remainingResources = initialResources;
        _predationProbability = predationProbability;
        _minAgentForageCount = minAgentForageCount;
    }
    
    /**
     * Determins if the specified agent is in this patch
     * 
     * @param agent The agent
     * @return <code>true</code> if the agent is in the patch, otherwise
     * <code>false</code>
     */
    public boolean isInPatch( Agent agent )
    {
        return (_radius > _position.distance( agent.getPosition() ));
    }
    
    /**
     * Returns the unique ID of this patch
     *
     * @return The ID
     */
    public String getID()
    {
        return _id;
    }
    
    /**
     * Returns this food patch's position 
     *
     * @return The location
     */
    public Vector3f getPosition()
    {
        return _position;
    }
    
    /**
     * Returns the radius of this patch
     *
     * @return The radius
     */
    public float getRadius()
    {
        return _radius;
    }
    
    /**
     * Returns the amount of resources remaining at this patch
     *
     * @return The amount of resources remaining at this patch
     */
    public float getRemainingResources()
    {
        return _remainingResources;
    }
}
