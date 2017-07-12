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

import org.apache.commons.lang3.Validate;

// Imports
import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.AgentTeam;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class Decision
{
    /** The type of decision */
    private DecisionType _type = null;

    /** The team associated with this decision */
    private AgentTeam _team = null;
    
    /** The leader (if any) associated with this decision */
    private Agent _leader = null;
    
    /** Weight of the separation behavior */
    protected float _separationWeight = 0.0f;
    
    /** Weight of the cohesion behavior */
    protected float _cohesionWeight = 0.0f;
    
    /** Weight of the alignment behavior */
    protected float _alignmentWeight = 0.0f;
    
    /** Weight of the goal seek behavior */
    protected float _goalSeekWeight = 0.0f;
    

    /**
     * Builds this Decision object
     *
     * @param type The type of decsion
     * @param team The associated team
     * @param leader The leader (if any)
     * @param separationWeight
     * @param cohesionWeight
     * @param alignmentWeight
     * @param goalSeekWeight
     */
    public Decision( DecisionType type,
            AgentTeam team,
            Agent leader,
            float separationWeight,
            float cohesionWeight,
            float alignmentWeight,
            float goalSeekWeight )
    {
        // Validate and store
        Validate.notNull( type, "Decision type may not be null" );
        Validate.notNull( type, "Agent team may not be null" );
        // Leader is optional
        // Weights can be zero (or negative) to inhibit
        _type = type;
        _team = team;
        _leader = leader;
        _separationWeight = separationWeight;
        _cohesionWeight = cohesionWeight;
        _alignmentWeight = alignmentWeight;
        _goalSeekWeight = goalSeekWeight;
        
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
     * Returns the team associated with this decision
     *
     * @return The team
     */
    public AgentTeam getTeam()   
    {
        return _team;
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
    
}
