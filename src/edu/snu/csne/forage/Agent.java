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

//Imports
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jme3.math.Vector3f;
import edu.snu.csne.forage.decision.AgentDecisionMaker;
import edu.snu.csne.forage.decision.Decision;
import edu.snu.csne.forage.event.DecisionEvent;
import edu.snu.csne.forage.sensor.AgentSensor;
import edu.snu.csne.forage.sensor.PatchSensor;
import edu.snu.csne.forage.util.PatchValue;
import edu.snu.csne.forage.util.PatchValueCalculator;
import edu.snu.csne.mates.math.NavigationalVector;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class Agent
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            Agent.class.getName() );
    
    /** Maximum strength of the separation force */
    private static final float _MAX_SEPARATION_STRENGTH = 0.000001f;
    
    /** This agent's unique ID */
    private String _id = null;
    
    /** The agent sensor for this agent */
    private AgentSensor _agentSensor = null;
    
    /** The patch sensor for this agent */
    private PatchSensor _patchSensor = null;
    
    /** This agent's current decision */
    private Decision _decision = null;
    
    /** The decision-maker for this agent */
    private AgentDecisionMaker _decisionMaker = null;
    
    /** Patch value calculator */
    private PatchValueCalculator _patchValueCalc = null;

    /** This agent's position */
    private Vector3f _position = new Vector3f();
    
    /** This agent's velocity */
    private Vector3f _velocity = new Vector3f();
    
    /** This agent's acceleration */
    private Vector3f _acceleration = new Vector3f();
    
    /** The team of which this agent is a member */
    private AgentTeam _team = null;
    
    /** The rate of resources this agent can consume */
    private float _resourceConsumptionRate = 0.0f;
    
    /** The amount of resources this agent has foraged */
    private float _resourcesForaged = 0.0f;
    
    /** The maximum speed of this agent */
    private float _maxSpeed = 0.0f;
    
    /** The maximum force this agent can apply */
    private float _maxForce = 0.0f;
    
    /** The distance at which the velocity is scaled to arrive at a destination */
    private float _arrivalScaleDistance = 0.0f;
    
    /** The min separation distance */
    private float _minSeparation = 0.0f;
    
    /** The desired separation distance */
    private float _desiredSeparation = 0.0f;
    
    /** The max foraging area */
    private float _maxForagingArea = 0.0f;
    
    /** Flag indicating whether or not this agent is active */
    private boolean _active = true;
    
    /** The other agents currently sensed */
    private List<Agent> _sensedAgents = new LinkedList<Agent>();
    
    /** The other teammates currently sensed */
    private List<Agent> _sensedTeammates = new LinkedList<Agent>();
    
    /** The non-teammates currently sensed */
    private List<Agent> _sensedNonTeammates = new LinkedList<Agent>();
    
    /** The patches currently sensed */
    private List<Patch> _sensedPatches = new LinkedList<Patch>();
    
    /** The known team memberships */
    private Map<String,List<Agent>> _knownTeamMemberships =
            new HashMap<String,List<Agent>>();
    
    /** The known team mean positions */
    private Map<String,Vector3f> _meanTeamPositions = new HashMap<String,Vector3f>();
    
    /** Mean resultant vectors by team */
    private Map<String,NavigationalVector> _meanResultantVectorsByTeam =
            new HashMap<String,NavigationalVector>();
    
    /** Patch value by patch ID */
    private Map<String,PatchValue> _patchValues =
            new HashMap<String,PatchValue>();

    /** The sum of all the individual give up time slopes */
    private float _patchValueSlopeIndSum = 0.0f;
    
    /** The sum of all the group give up time slopes */
    private float _patchValueSlopeGroupSum = 0.0f;

    /** The max of all the individual give up time slopes */
    private float _patchValueSlopeIndMax = 0.0f;
    
    /** The max of all the group give up time slopes */
    private float _patchValueSlopeGroupMax = 0.0f;
    
    /** The current state of the simulation */
    private SimulationState _simState = null;
    
    /**
     * Builds this Agent object
     *
     * @param id
     * @param initialPosition
     * @param initialVelocity
     * @param team
     * @param resourceConsumptionRate
     * @param resourceConsumptionMax
     * @param maxSpeed
     * @param agentSensor
     * @param patchSensor
     * @param decisionMaker
     * @param simState
     */
    public Agent( String id,
            Vector3f initialPosition,
            Vector3f initialVelocity,
            AgentTeam team,
            float resourceConsumptionRate,
            float resourceConsumptionMax,
            float maxSpeed,
            float maxForce,
            float arrivalScaleDistance,
            float minSeparation,
            float desiredSeparation,
            float maxForagingArea,
            AgentSensor agentSensor,
            PatchSensor patchSensor,
            AgentDecisionMaker decisionMaker,
            PatchValueCalculator patchValueCalc,
            SimulationState simState )
    {
        // Validate the parameters
        Validate.notBlank( id, "ID may not be null" );
        Validate.notNull( initialPosition, "Initial position may not be null" );
        Validate.notNull( initialVelocity, "Initial velocity may not be null" );
        Validate.notNull( team, "Team may not be null" );
        Validate.isTrue( 0.0f < resourceConsumptionRate,
                "Resource consumption rate must be positive" );
        Validate.isTrue( 0.0f < maxSpeed, "Max speed must be positive" );
        Validate.isTrue( 0.0f < maxForce, "Max force must be positive" );
        Validate.isTrue( 0.0f < arrivalScaleDistance, "Arrival scale distance must be positive" );
        Validate.isTrue( 0.0f < desiredSeparation, "Desired separation must be positive" );
        Validate.isTrue( 0.0f < maxForagingArea, "Max foraging area must be positive" );
        Validate.notNull( agentSensor, "Agent sensor may not be null" );
        Validate.notNull( patchSensor, "Patch sensor may not be null" );
        Validate.notNull( decisionMaker, "Decision maker may not be null" );
        Validate.notNull( patchValueCalc, "Patch value calculator may not be null" );
        Validate.notNull( simState, "Simulation state may not be null" );
        
        // Store the parameters
        _id = id;
        _position = initialPosition.clone();
        _velocity = initialVelocity.clone();
        _team = team;
        _resourceConsumptionRate = resourceConsumptionRate;
        _maxSpeed = maxSpeed;
        _maxForce = maxForce;
        _arrivalScaleDistance = arrivalScaleDistance;
        _desiredSeparation = desiredSeparation;
        _maxForagingArea = maxForagingArea;
        _agentSensor = agentSensor;
        _patchSensor = patchSensor;
        _decisionMaker = decisionMaker;
        _patchValueCalc = patchValueCalc;
        _simState = simState;
        
        _decision = Decision.buildRestDecision( 0, this );
    }
    
    /**
     * Causes this agent to sense its environment
     */
    public void sense()
    {
        _LOG.trace( "Entering sense()" );

        // Clear out all the previously sensed objects
        _sensedAgents.clear();
        _sensedTeammates.clear();
        _sensedNonTeammates.clear();
        _sensedPatches.clear();
        
        // Sense the agents
        _sensedAgents = _agentSensor.sense( this );
        
        // Which ones are teammates?
        findSensedTeammates();
        
        // Determine team memberships
        findTeamMemberships();
        
        // Calculate the mean resultant vectors for all teams
        calculateMeanResultantVectors();
        
//        _LOG.debug( "Sensed [" + _sensedTeammates.size() + "] teammates" );
        
        // Sense the patches
        _sensedPatches = _patchSensor.sense( this );
        
        // Calculate the value of all the sensed patches
        calculatePatchValues();
        
        _LOG.trace( "Leaving sense()" );
    }

    /**
     * Causes this agent to decide on a course of action
     */
    public void plan()
    {
        _LOG.trace( "Entering plan()" );

        // Get the new decision
        _decision = _decisionMaker.decide( this );

        // Are we switching teams?
        if( !_decision.getTeam().equals( _team ) )
        {
            // Yup, change the team and notify them
            _team.leave( this );
            _team = _decision.getTeam();
            _team.join( this );
            
            // Search through the sensed agents for the new teammates
            findSensedTeammates();
        }
        
        // Signal this decision
        _simState.signalAgentDecision( new DecisionEvent(
                _decision,
                this,
                _simState.getCurrentSimulationStep() ) );
        
        _LOG.trace( "Leaving plan()" );
    }

    /**
     * Causes this agent to act on its current decision
     */
    public void act()
    {
        _LOG.trace( "Entering act()" );

        // Reset the acceleration
        _acceleration = new Vector3f();

        // Are we at our desired location?
        
        // Some behaviors only work if we have sensed teammates
        boolean sensedTeammates = (0 < _sensedTeammates.size() );
        
        // Execute the behaviors
        float separationWeight = _decision.getSeparationWeight();
        if( sensedTeammates && separationWeight > 0.0f )
        {
            Vector3f separation = calculateSeparation();
            separation.multLocal( separationWeight );
            _LOG.debug( getID() + " separation=[" + separation + "]" );
            _acceleration.addLocal( separation );
        }
        float cohesionWeight = _decision.getCohesionWeight();
        if( sensedTeammates && cohesionWeight > 0.0f )
        {
            Vector3f cohesion = calculateCohesion();
            cohesion.multLocal( cohesionWeight );
            _LOG.debug( getID() + " cohesion=[" + cohesion + "]" );
            _acceleration.addLocal( cohesion );
        }
        float alignmentWeight = _decision.getAlignmentWeight();
        if( sensedTeammates && alignmentWeight > 0.0f )
        {
            Vector3f alignment = calculateAlignment();
            alignment.multLocal( alignmentWeight );
            _LOG.debug( getID() + " alignment=[" + alignment + "]" );
            _acceleration.addLocal( alignment );
        }
        float goalSeekWeight = _decision.getGoalSeekWeight();
        if( goalSeekWeight > 0.0f )
        {
            Vector3f goalSeek = calculateGoalSeek();
            goalSeek.multLocal( goalSeekWeight );
            _LOG.debug( getID() + " goalSeek=[" + goalSeek + "]" );
            _acceleration.addLocal( goalSeek );
        }
        
        ForageUtils.limitMagnitude( _acceleration, _maxForce );
        
        // Apply the acceleration to the velocity and move the agent
        if( _acceleration.lengthSquared() > 0.0001f )
        {
            _velocity.addLocal( _acceleration );
            ForageUtils.limitMagnitude( _velocity, _maxSpeed );
        }
        _position.addLocal( _velocity );
        
        _LOG.debug( "Action "
                + getID()
                + ": accel="
                + _acceleration
                + " vel="
                + _velocity
                + " position="
                + _position );

        _LOG.trace( "Leaving act()" );
    }
    
    /**
     * Kills this agent as a result of predation
     */
    public void terminate()
    {
        // Notify the team the agent isn't a member anymore
        _team.leave( this );
        
        // Set the agent to inactive
        _active = false;
    }
    
    /**
     * Returns the unique ID of this agent
     *
     * @return The unique ID
     */
    public String getID()
    {
        return _id;
    }
    
    /**
     * Returns the team of which this agent is a member
     *
     * @return The agent's team
     */
    public AgentTeam getTeam()
    {
        return _team;
    }
    
    /**
     * Returns this agent's position
     *
     * @return This agent's position
     */
    public Vector3f getPosition()
    {
        return _position;
    }
    
    /**
     * Returns this agent's velocity
     *
     * @return This agent's velocity
     */
    public Vector3f getVelocity()
    {
        return _velocity.clone();
    }
    
    /**
     * Returns this agent's max speed
     *
     * @return This agent's max speed
     */
    public float getMaxSpeed()
    {
        return _maxSpeed;
    }
    
    /**
     * Returns the amount of resources this agent has foraged
     *
     * @return The amount of resources this agent has foraged
     */
    public float getResourcesForaged()
    {
        return _resourcesForaged;
    }
    
    /**
     * TODO Method description
     *
     * @param resourcesForaged
     */
    public void addResourcesForaged( float resourcesForaged )
    {
        Validate.isTrue( resourcesForaged >= 0.0f, "Resources foraged must be non-negative: ["
                + resourcesForaged
                + "]" );
        _resourcesForaged += resourcesForaged;
    }
    
    /**
     * Returns the flag indicating whether or not this agent is active
     *
     * @return <code>true</code> if the agent is active, otherwise,
     *          <code>false</code>
     */
    public boolean isActive()
    {
        return _active;
    }

    /**
     * Returns this agent's current decision
     *
     * @return The decision
     */
    public Decision getDecision()
    {
        return _decision;
    }
    
    /**
     * Returns all the agents sensed by this agent
     * 
     * @return The sensed agents
     */
    public List<Agent> getSensedAgents()
    {
        return _sensedAgents;
    }
    
    /**
     * Returns all the teammate agents that are sensed by this agent
     *
     * @return The sensed teammates
     */
    public List<Agent> getSensedTeammates()
    {
        return _sensedTeammates;
    }
    
    /**
     * Returns all the non-teammate agents that are sensed by this agent
     *
     * @return The sensed non-teammates
     */
    public List<Agent> getSensedNonTeammates()
    {
        return _sensedNonTeammates;
    }

    /**
     * Returns all the patches sensed by this agent
     *
     * @return The sensed patches
     */
    public List<Patch> getSensedPatches()
    {
        return _sensedPatches;
    }
    
    /**
     * Returns a map of all the sensed teams and the first known member
     * (referred to as the leader)
     *
     * @return The sensed team leaders
     */
    public Map<String,Agent> getSensedTeamLeaders()
    {
        // Build a map that stores teamID -> agent
        // Where the agent is the first sensed team member
        Map<String,Agent> sensedLeaders = new HashMap<String,Agent>();
        Iterator<String> teamIDIter = _knownTeamMemberships.keySet().iterator();
        while( teamIDIter.hasNext() )
        {
            String teamID = teamIDIter.next();
            Agent agent = _knownTeamMemberships.get( teamID ).get( 0 );
            sensedLeaders.put( teamID, agent );
        }
        
        return sensedLeaders;
    }
    
    /**
     * Searches for teammates among the sensed agents
     */
    private void findSensedTeammates()
    {
        // Which of the sensed agents are teammates?
        Iterator<Agent> agentIter = _sensedAgents.iterator();
        while( agentIter.hasNext() )
        {
            Agent current = agentIter.next();
            
            // Are they on the same team?
            if( _team.equals( current.getTeam() ) )
            {
                // Yup
                _sensedTeammates.add( current );
            }
            else
            {
                _sensedNonTeammates.add( current );
            }
        }
    }
    
    /**
     * Returns the maximum amount of resources the agent can consume per timestep
     *
     * @return The resource consumption rate
     */
    public float getResourceConsumptionRate()
    {
        return _resourceConsumptionRate;
    }
    
    /**
     * Returns the maximum area in which the agent can forage per timestep
     *
     * @return The max foraging area
     */
    public float getMaxForagingArea()
    {
        return _maxForagingArea;
    }
    
    /**
     * Returns the mean resultant vector for this agent for the given team
     *
     * @param teamID The team associated with the MRV
     * @return The mean resultant vector
     */
    public NavigationalVector getMRVForTeam( String teamID )
    {
        // Validate the team ID
        Validate.notEmpty( teamID, "Team ID is required" );
        Validate.isTrue( _meanResultantVectorsByTeam.containsKey( teamID ),
                "Unknown team ID [" + teamID + "]" );
        
        return _meanResultantVectorsByTeam.get( teamID );
    }
    
    /**
     * Returns the mean position of the given team
     *
     * @param teamID The team
     * @return The mean position
     */
    public Vector3f getMeanPositionOfTeam( String teamID )
    {
        // Validate the team ID
        Validate.notEmpty( teamID, "Team ID is required" );
        Validate.isTrue( _meanTeamPositions.containsKey( teamID ),
                "Unknown team ID [" + teamID + "]" );
        
        return _meanTeamPositions.get( teamID );
    }
    
    /**
     * Returns the value of the specified patch
     *
     * @param patchID The patch of interest
     * @return The patches value
     */
    public PatchValue getPachValue( String patchID )
    {
        // Validate the patch ID
        Validate.notEmpty( patchID, "Patch ID is required" );
        Validate.isTrue( _patchValues.containsKey( patchID ),
                "Uknown patch ID [" + patchID + "]" );
        
        return _patchValues.get( patchID );
    }
    
    /**
     * Returns the maximum individual patch value 
     *
     * @return The maximum individual patch value
     */
    public float getPatchValueIndMax()
    {
        return _patchValueSlopeIndMax;
    }
    
    /**
     * Returns the maximum group patch value
     *
     * @return The maximum group patch value
     */
    public float getPatchValueGroupMax()
    {
        return _patchValueSlopeGroupMax;
    }
    
    /**
     * Searches for team memberships (ordered by observed join time)
     * among the sensed agents
     */
    private void findTeamMemberships()
    {
        _LOG.trace( "Entering findTeamMemberships()" );

        // Create a new map for team memberships
        Map<String,List<Agent>> currentKnownTeamMemberships =
                new HashMap<String,List<Agent>>();
        
        // Build a map of all the observed agents
        Map<String,Agent> sensedAgentMap = new HashMap<String,Agent>();
        Iterator<Agent> agentIter = _sensedAgents.iterator();
        while( agentIter.hasNext() )
        {
            Agent current = agentIter.next();
            sensedAgentMap.put( current.getID(), current );
        }
        
        // Iterate through all the teamIDs in the existing team membership map
        Iterator<String> teamIDIter = _knownTeamMemberships.keySet().iterator();
        while( teamIDIter.hasNext() )
        {
            // Iterate through all the sensed agents in this team
            String teamID = teamIDIter.next();
            List<Agent> newTeamMembers = new LinkedList<Agent>();
            List<Agent> oldTeamMembers = _knownTeamMemberships.get( teamID );
            Iterator<Agent> oldTeamMemberIter = oldTeamMembers.iterator();
            while( oldTeamMemberIter.hasNext() )
            {
                // Did we sense this agent?
                Agent agent = oldTeamMemberIter.next();
                if( sensedAgentMap.containsKey( agent.getID() ) )
                {
                    // Was it in the same team?
                    if( teamID.equals( agent.getTeam().getID() ) )
                    {
                        // Yup.  Add it to the new list of team members
                        newTeamMembers.add( agent );
                        
                        // Remove it from the map of sensed agents
                        sensedAgentMap.remove( agent.getID() );
                        
//                        _LOG.debug( "Found previous member of ["
//                                + teamID
//                                + "]" );
                    }
                }
            }
            
//            _LOG.debug( "Team=[" + teamID + "]  Previous members=[" + newTeamMembers.size() + "]" );
            
            // Did we sense any team members for this team?
            if( newTeamMembers.size() > 0 )
            {
                // Yup add it to the map
                currentKnownTeamMemberships.put( teamID, newTeamMembers );
            }
        }
        
        // Process all the agents we haven't previously observed to be part of a team
        Iterator<String> unprocessedAgentIDIter = sensedAgentMap.keySet().iterator();
        while( unprocessedAgentIDIter.hasNext() )
        {
            // Get the agent and their teamID
            String agentID = unprocessedAgentIDIter.next();
            Agent agent = sensedAgentMap.get( agentID );
            String teamID = agent.getTeam().getID();
            
//            _LOG.debug( "Found new member of ["
//                    + teamID
//                    + "]" );
            
            // Get the observed members of that team
            List<Agent> observedTeamMembers = currentKnownTeamMemberships.get( teamID );
            if( null == observedTeamMembers )
            {
                // We haven't observed any for that team, create the list and
                // add it to the map
                observedTeamMembers = new LinkedList<Agent>();
                currentKnownTeamMemberships.put( teamID, observedTeamMembers );
                
//                _LOG.debug( "New team [" + teamID + "]" );
            }
            
            // Add the agent to the end of the list
            observedTeamMembers.add( agent );
//            _LOG.debug( "Added agent [" + agentID + "] to team [" + teamID + "]" );
        }
        
        // Save the team memberships
        _knownTeamMemberships = currentKnownTeamMemberships;
        
        // Compute the mean positions of all the teams
        teamIDIter = _knownTeamMemberships.keySet().iterator();
        while( teamIDIter.hasNext() )
        {
            // Iterate through all the sensed agents in this team
            String teamID = teamIDIter.next();
            Vector3f meanPosition = new Vector3f();
            List<Agent> teamMembers = _knownTeamMemberships.get( teamID );
            Iterator<Agent> teamMemberIter = teamMembers.iterator();
            while( teamMemberIter.hasNext() )
            {
                meanPosition.addLocal( teamMemberIter.next().getPosition() );
            }
            
            meanPosition.divideLocal( teamMembers.size() );
            
            // Add it to the map
            _meanTeamPositions.put( teamID, meanPosition );
//            _LOG.debug( "Mean position: teamID=["
//                    + teamID
//                    + "] position=["
//                    + meanPosition
//                    + "]" );
        }
        
        // If we didn't find any team members, create an empty MRV for our team
        if( !_meanTeamPositions.containsKey( getTeam().getID() ) )
        {
            _meanTeamPositions.put( getTeam().getID(),
                    new Vector3f() );
        }


//        _LOG.debug( "Sensed leaders for [" + _knownTeamMemberships.size() + "]" );
        
        _LOG.trace( "Leaving findTeamMemberships()" );
    }
    
    /**
     * Calculates the mean resultant vector of this agent w.r.t. to all sensed
     * agents by team.
     */
    private void calculateMeanResultantVectors()
    {
        _LOG.trace( "Entering calculateMeanResultantVectors()" );

        // Iterate through each team
        Iterator<String> teamIDIter = _knownTeamMemberships.keySet().iterator();
        while( teamIDIter.hasNext() )
        {
            String currentTeamID = teamIDIter.next();
            
            // Iterate through all the agents on the team
            Vector3f teamSum = new Vector3f();
            List<Agent> teamMembers = _knownTeamMemberships.get( currentTeamID );
            Iterator<Agent> currentTeamMemberIter = teamMembers.iterator();
            while( currentTeamMemberIter.hasNext() )
            {
                Agent currentAgent = currentTeamMemberIter.next();
                
                // Get the vector from the agent to the teammate
                Vector3f toAgent = currentAgent.getPosition().subtract( _position );

                // Normalize and add it to the sum of vectors
                teamSum.addLocal( toAgent.normalize() );
                
//                _LOG.debug( "ToAgent ["
//                        + toAgent
//                        + "] teamSum=["
//                        + teamSum
//                        + "]" );
            }
            
            // Scale it by the number of agents
            Vector3f mrv = new Vector3f( teamSum );
            if( teamMembers.size() > 0 )
            {
                mrv.multLocal( 1.0f / teamMembers.size() );
            }
            
            // Store it
            _meanResultantVectorsByTeam.put( currentTeamID,
                    new NavigationalVector( mrv ) );
            
            _LOG.debug( "MRV Team=["
                    + currentTeamID
                    + "]: mrv=["
                    + mrv
                    + "] teamSum=["
                    + teamSum
                    + "] size=["
                    + teamMembers.size()
                    + "]" );
        }
        
        // If we didn't find any team members, create an empty MRV for our team
        if( !_meanResultantVectorsByTeam.containsKey( getTeam().getID() ) )
        {
            _meanResultantVectorsByTeam.put( getTeam().getID(),
                    new NavigationalVector() );
        }
        
        _LOG.trace( "Leaving calculateMeanResultantVectors()" );
    }


    /**
     * Calculates the value of all the sensed patches
     */
    private void calculatePatchValues()
    {
        _LOG.trace( "Entering calculatePatchValues()" );
        
        // Reset the patch value sums
        _patchValueSlopeIndSum = 0.0f;
        _patchValueSlopeGroupSum = 0.0f;
        _patchValueSlopeIndMax = 0.0f;
        _patchValueSlopeGroupMax = 0.0f;
        
        // Iterate through each of the sensed patches
        Iterator<Patch> patchIter = _sensedPatches.iterator();
        while( patchIter.hasNext() )
        {
            Patch patch = patchIter.next();
            
            // Calculate the value
            PatchValue value = _patchValueCalc.calculatePatchValue( patch, this );
            float giveUpSlopeInd = value.getGiveUpSlopeInd();
            float giveUpSlopeGroup = value.getGiveUpSlopeGroup();
            
            // Save it
            _patchValues.put( patch.getID(), value );
            
            // Add it to the sums
            _patchValueSlopeIndSum += giveUpSlopeInd;
            _patchValueSlopeGroupSum += giveUpSlopeGroup;
            
            // See if they are the new maximums
            if( _patchValueSlopeIndMax < giveUpSlopeInd )
            {
                _patchValueSlopeIndMax = giveUpSlopeInd;
            }
            if( _patchValueSlopeGroupMax < giveUpSlopeGroup )
            {
                _patchValueSlopeGroupMax = giveUpSlopeGroup;
            }
        }

        _LOG.trace( "Leaving calculatePatchValues()" );
    }
    
    /**
     * Calculates the alignment vector
     *
     * @return The alignment vector
     */
    private Vector3f calculateAlignment()
    {
        Vector3f alignment = new Vector3f();
        
        // Average all the velocities of our sensed teammates
        Iterator<Agent> agentIter = _sensedTeammates.iterator();
        while( agentIter.hasNext() )
        {
            Agent current = agentIter.next();
            alignment.addLocal( current.getVelocity() );
        }
        alignment.divideLocal( _sensedTeammates.size() );
        
        // Limit it by the max force
        alignment.subtractLocal( getVelocity() );
        ForageUtils.limitMagnitude( alignment, _maxForce );

        return alignment;
    }
    
    /**
     * Calculates the separation vector
     *
     * @return The separation vector
     */
    private Vector3f calculateSeparation()
    {
        Vector3f separation = new Vector3f();

        float minMaxSeparationDiff = _desiredSeparation - _minSeparation;
        
        // Average all the relative positions from everyone
        int separationAgentCount = 0;
//        Iterator<Agent> agentIter = _sensedTeammates.iterator();
        Iterator<Agent> agentIter = _sensedAgents.iterator();
        while( agentIter.hasNext() )
        {
            Agent current = agentIter.next();
            
            // Calculate the vector to us 
            Vector3f fromCurrent = _position.subtract( current.getPosition() );
            float distance = fromCurrent.length();
//            _LOG.debug( "Separation distance=[" + distance + "]" );
            
            // Is the distance within the desired separation?
            if( _desiredSeparation > distance )
            {
                /* There is a separation force that uses the distance to the agent.
                 * Since we don't want to be closer than the minimum, anything
                 * closer is at maximum strength.
                 */
                float separationStrength = (distance - _minSeparation) / minMaxSeparationDiff;
                if( separationStrength <= 0.0f )
                {
                    separationStrength = _MAX_SEPARATION_STRENGTH;
                }
                fromCurrent.normalizeLocal();
                fromCurrent.divideLocal( separationStrength );
//                _LOG.debug( "Separation: distance=[" + distance
//                        + "] strength=[" + separationStrength
//                        + "] fromCurrent=[" + fromCurrent + "]" );

                // Add it to the total separation
                separation.addLocal( fromCurrent );
                separationAgentCount++;
            }
        }
        
        // Did we separate from anyone?
        if( 0 < separationAgentCount )
        {
            //Yup, scale it
            separation.divideLocal( _sensedTeammates.size() );
            
            // Limit it by the max force
//            ForageUtils.limitMagnitude( separation, _maxForce );
        }
        else
        {
            separation = new Vector3f();
        }

        return separation;
    }
    
    /**
     * Calculates the cohesion vector
     *
     * @return The cohesion vector
     */
    private Vector3f calculateCohesion()
    {
        Vector3f cohesion = new Vector3f();
        
        // Average all the relative positions of our sensed teammates
        Iterator<Agent> agentIter = _sensedTeammates.iterator();
        while( agentIter.hasNext() )
        {
            Agent current = agentIter.next();
            cohesion.addLocal( current.getPosition().subtract( _position ) );
        }
        cohesion.divideLocal( _sensedTeammates.size() );
        
        return seek( cohesion, false );
    }

    /**
     * Calculates the goal seek vector
     *
     * @return The goal seek vector
     */
    private Vector3f calculateGoalSeek()
    {
        // Get the destination from the decision
        Vector3f goal = _decision.getDestination();
        
        _LOG.debug( "Position=["
                + _position
                + "] Goal=["
                + goal
                + "]" );
        
        return seek( goal.subtract( _position ), true );
    }
    
    /**
     * Calculates vector to seek to a location
     *
     * @param location The location to which the agent will navigate
     * @param slowDown Flag indicating whether or not the agent should slow down
     * when the destination is close
     * @return The vector to navigate to a location
     */
    private Vector3f seek( Vector3f location, boolean slowDown )
    {
        _LOG.debug( "Seeking [" + location + "]" );
        
        // Is it within the minimum distance to not go full speed?
        float distance = location.length();
        Vector3f desired = location.normalize();
        Vector3f steer = null;
        if( 0.0f < distance )
        {
            if( slowDown && (_arrivalScaleDistance > distance) )
            {
                // Yup, scale based on distance
                desired.multLocal( _maxSpeed * distance / _arrivalScaleDistance );
            }
            else
            {
                // Nope, use the max speed
                desired.multLocal( _maxSpeed );
            }
            
            // Calculate steering change
            steer = desired.subtract( getVelocity() );
        }
        else
        {
            steer = new Vector3f();
        }

        // Limit it to our max force
        ForageUtils.limitMagnitude( steer, _maxForce );

        return steer;
    }
}
