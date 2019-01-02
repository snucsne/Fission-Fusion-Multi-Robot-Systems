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
package edu.snu.csne.forage.decision;

//Imports
import org.apache.commons.lang3.Validate;
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
public class Decision
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            Decision.class.getName() );
    
    /** The type of decision */
    private DecisionType _type = null;

    /** The time of this decision */
    private long _timestep = 0l;
    
    /** The leader (if any) associated with this decision */
    private Agent _leader = null;
    
    /** The patch (if any) associated with this decision */
    private Patch _patch = null;
    
    /** The exploration destination (if any) associated with this decision */
    private Vector3f _destination = null;
    
    /** Weight of the separation behavior */
    protected float _separationWeight = 0.0f;
    
    /** Weight of the cohesion behavior */
    protected float _cohesionWeight = 0.0f;
    
    /** Weight of the alignment behavior */
    protected float _alignmentWeight = 0.0f;
    
    /** Weight of the goal seek behavior */
    protected float _goalSeekWeight = 0.0f;

    /** The probability of making this decision */
    private float _probability = 0.0f;

    
    private Decision( DecisionType type,
            long timestep,
            Agent leader,
            Patch patch,
            Vector3f destination,
            float separationWeight,
            float cohesionWeight,
            float alignmentWeight,
            float goalSeekWeight,
            float probability )
    {
        // Validate and store the required parameters
        Validate.notNull( type, "Decision type may not be null" );
        _type = type;
        Validate.isTrue( 0 <= timestep, "Timestep must be non-negative" );
        _timestep = timestep;
        
        // Optional parameters
        _leader = leader;
        _patch = patch;
        _destination = destination;
        
        // Store the weights
        _separationWeight = separationWeight;
        _cohesionWeight = cohesionWeight;
        _alignmentWeight = alignmentWeight;
        _goalSeekWeight = goalSeekWeight;
        
        // Validate the probability
        Validate.inclusiveBetween( 0.0f,
                1.0f,
                probability,
                "Probability must lie in the interval [0,1] - given ["
                        + probability
                        + "]" );
        _probability = probability;
    }
    
    /**
     * Signal the decision that it has been chosen as the active decision
     *
     * @param simState The current state of the simulation
     */
    public void choose( SimulationState simState )
    {
        // Do nothing now
    }
    
    public long getTimestep()
    {
        return _timestep;
    }
    
    /**
     * Returns the type of this decision
     *
     * @return The type
     */
    public DecisionType getType()
    {
        return _type;
    }
    
    /**
     * Returns the probability of making this decision
     *
     * @return The probability
     */
    public float getProbability()
    {
        return _probability;
    }
    
    /**
     * Returns the leader (if any) associated with this decision
     * 
     * @return The leader (if any)
     */
    public Agent getLeader()
    {
        return _leader;
    }

    public void replaceLeader( Agent leader )
    {
        if( DecisionType.FOLLOW.equals( _type ) )
        {
            Validate.notNull( leader, "Replacement leader may not be null" );
            _leader = leader;
        }
    }
    
    /**
     * Returns the patch (if any) associated with this decision
     *
     * @return The patch (if any)
     */
    public Patch getPatch()
    {
        return _patch;
    }
    
    /**
     * Returns the destination for the goal seek behavior
     *
     * @return The destination
     */
    public Vector3f getDestination()
    {
        // The type of decisions determines the destination
        Vector3f destination = new Vector3f();
        
        if( DecisionType.NAVIGATE.equals( _type ) )
        {
            destination = _patch.getPosition();
        }
        else if( DecisionType.EXPLORE.equals( _type ) )
        {
            destination = _destination;
        }
        else if( DecisionType.FOLLOW.equals( _type ) )
        {
            destination = _leader.getPosition();
        }
        if( DecisionType.FORAGE.equals( _type ) )
        {
            destination = _patch.getPosition();
        }

        return destination;
    }
    
    /**
     * Returns the weight for the separation behavior
     *
     * @return The weight
     */
    public float getSeparationWeight()
    {
        return _separationWeight;
    }
    
    /**
     * Returns the weight for the cohesion behavior
     *
     * @return The weight
     */
    public float getCohesionWeight()
    {
        return _cohesionWeight;
    }
    
    /**
     * Returns the weight for the alignment behavior
     *
     * @return The weight
     */
    public float getAlignmentWeight()
    {
        return _alignmentWeight;
    }
    
    /**
     * Returns the weight for the goal seek behavior
     *
     * @return The weight
     */
    public float getGoalSeekWeight()
    {
        return _goalSeekWeight;
    }
    
    
    public static Decision buildNavigateDecision(
            long timestep,
            Patch patch,
            float separationWeight,
            float cohesionWeight,
            float alignmentWeight,
            float goalSeekWeight,
            float probability )
    {
        Validate.isTrue( 0 <= timestep, "Timestep must be non-negative" );
//        Validate.notNull( team, "Agent team may not be null" );
        Validate.notNull( patch, "Patch may not be null" );
        Validate.inclusiveBetween( 0.0f,
                1.0f,
                probability,
                "Probability must lie in the interval [0,1] - given ["
                        + probability
                        + "]" );

        // Create the decision
        return new Decision(
                DecisionType.NAVIGATE,
                timestep,
                null,
                patch,
                null,
                separationWeight,
                cohesionWeight,
                alignmentWeight,
                goalSeekWeight,
                probability );
    }
    
    public static Decision buildExploreDecision(
            long timestep,
            Vector3f destination,
            float separationWeight,
            float cohesionWeight,
            float alignmentWeight,
            float goalSeekWeight,
            float probability )
    {
        // Validate the parameters
        Validate.isTrue( 0 <= timestep, "Timestep must be non-negative" );
        Validate.notNull( destination, "Destination may not be null" );
        Validate.inclusiveBetween( 0.0f,
                1.0f,
                probability,
                "Probability must lie in the interval [0,1] - given ["
                        + probability
                        + "]" );
        
        // Create the decision
        return new Decision(
                DecisionType.EXPLORE,
                timestep,
                null,
                null,
                destination,
                separationWeight,
                cohesionWeight,
                alignmentWeight,
                goalSeekWeight,
                probability );
    }
    
    public static Decision buildFollowDecision(
            long timestep,
            Agent leader,
            Patch patch,
            float separationWeight,
            float cohesionWeight,
            float alignmentWeight,
            float goalSeekWeight,
            float probability )
    {
        Validate.isTrue( 0 <= timestep, "Timestep must be non-negative" );
        Validate.notNull( leader, "Agent leader may not be null" );
        Validate.inclusiveBetween( 0.0f,
                1.0f,
                probability,
                "Probability must lie in the interval [0,1] - given ["
                        + probability
                        + "]" );
        
        // Create the decision
        return new Decision(
                DecisionType.FOLLOW,
                timestep,
                leader,
                patch,
                null,
                separationWeight,
                cohesionWeight,
                alignmentWeight,
                goalSeekWeight,
                probability );
    }
    
    public static Decision buildForageDecision(
            long timestep,
            Agent leader,
            Patch patch,
            float separationWeight,
            float cohesionWeight,
            float alignmentWeight,
            float goalSeekWeight,
            float probability )
    {
        Validate.isTrue( 0 <= timestep, "Timestep must be non-negative" );
        Validate.notNull( patch, "Patch may not be null" );
        Validate.inclusiveBetween( 0.0f,
                1.0f,
                probability,
                "Probability must lie in the interval [0,1] - given ["
                        + probability
                        + "]" );

        // Create the decision
        return new Decision(
                DecisionType.FORAGE,
                timestep,
                leader,
                patch,
                patch.getPosition(),
                separationWeight,
                cohesionWeight,
                alignmentWeight,
                goalSeekWeight,
                probability );
    }
    
    public static Decision buildRestDecision(
            long timestep,
            Agent agent )
    {
        Validate.isTrue( 0 <= timestep, "Timestep must be non-negative" );
        Validate.notNull( agent, "Agent may not be null" );
        
        // Create the decision
        return new Decision(
                DecisionType.REST,
                timestep,
                null,
                null,
                agent.getPosition(),
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                1.0f );
    }
}
