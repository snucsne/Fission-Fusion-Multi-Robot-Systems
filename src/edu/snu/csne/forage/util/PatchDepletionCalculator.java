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
package edu.snu.csne.forage.util;

// Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.math.Vector3f;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.AgentTeam;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.forage.decision.Decision;
import edu.snu.csne.util.MiscUtils;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class PatchDepletionCalculator
{
    public static class PatchDepletionData
    {
        public final float resourcesGatheredAgent;
        public final float totalResourcesGatheredAgent;
        public final float resourcesGatheredGroup;
        public final float totalResourcesGatheredGroup;
        public final int timesteps;
        public final int agentsPresent;
        
        public PatchDepletionData( float resourcesGatheredAgent,
                float totalResourcesGatheredAgent,
                float resourcesGatheredGroup,
                float totalResourcesGatheredGroup,
                int timesteps,
                int agentsPresent)
        {
            this.resourcesGatheredAgent = resourcesGatheredAgent;
            this.totalResourcesGatheredAgent = totalResourcesGatheredAgent;
            this.resourcesGatheredGroup = resourcesGatheredGroup;
            this.totalResourcesGatheredGroup = totalResourcesGatheredGroup;
            this.timesteps = timesteps;
            this.agentsPresent = agentsPresent;
        }
    }
    
    public static class PatchArrivalTime
    {
        public final String teamID;
        public final int arrivalTime;
        
        public PatchArrivalTime( String teamID, int arrivalTime )
        {
            this.teamID = teamID;
            this.arrivalTime = arrivalTime;
        }
    }
    
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            PatchDepletionCalculator.class.getName() );
    
    /** Property key for the maximum timesteps that patch depletion is calculated */
    private static final String _MAX_DEPLETION_TIMESTEPS_KEY =
            "max-depletion-timesteps";
    
    /** Property key for the flag to enforce patch minimums */
    private static final String _ENFORCE_PATCH_MIN_AGENTS_KEY =
            "enforce-patch-minimum-agents";

    
    /** The simulation state */
    protected SimulationState _simState = null;

    /** The maximum timesteps that patch depletion is calculated */
    protected int _maxDepletionTimesteps = 0;
    
    /** Average speed as a percentage of the max speed */
    private float _avgSpeedPercentage = 0.9f;
    
    /** Flag to enforce patch minimum agent counts */
    private boolean _enforcePatchMinimumAgents = false;
    
    
    /**
     * Initializes this patch value calculator
     *
     * @param simState The simulation's state
     */
    public void initialize( SimulationState simState)
    {
        // Save the simulation state
        _simState = simState;
        
        // Get the properties
        Properties props = simState.getProps();

        // Load the max timesteps that patch depletion is calculated
        _maxDepletionTimesteps = MiscUtils.loadNonEmptyIntegerProperty( props,
                _MAX_DEPLETION_TIMESTEPS_KEY,
                "Max depletion timesteps" );

        // Load the flag that signals if we enforce patch minimum agent counts
        _enforcePatchMinimumAgents = MiscUtils.loadOptionalBooleanProperty( props,
                _ENFORCE_PATCH_MIN_AGENTS_KEY,
                false );
        _LOG.warn( "Enforcing patch minimum agent count ["
                + _enforcePatchMinimumAgents
                + "]" );
    }

    /**
     * Calculate the value of a patch for a given agent
     *
     * @param patch The patch
     * @param agent The agent
     * @return The value of the patch
     */
    public PatchValue calculatePatchValue( Patch patch, Agent agent )
    {
        // Create some handy variables
        int giveUpTimeInd = 0;
        float giveUpSlopeInd = 0.0f;
        int giveUpTimeGroup = 0;
        float giveUpSlopeGroup = 0.0f;
        float indResources = 0.0f;
        float groupResources = 0.0f;
        
        // Calculate the agent's arrival time at the patch
        Vector3f toPatch = patch.getPosition().subtract( agent.getPosition() );
        float distance = toPatch.length() - patch.getRadius();
        int agentArrivalTime = (int) Math.ceil( distance /
                (agent.getMaxSpeed() * _avgSpeedPercentage ) );
        
        // Get the arrival times of teams
        List<PatchArrivalTime> arrivalTimes = findTeamArrivalsAtPatch( patch );
        
        // Build an array of the number of agents at the patch at each timestep
        int[] agentCounts = new int[_maxDepletionTimesteps];
        for( int i = 0; i < agentCounts.length; i++ )
        {
            // Is the agent projected to arrive now?
            if( agentArrivalTime <= i )
            {
                agentCounts[i]++;
            }
            
            // Are any of the teams projected to arrive now?
            for( int j = 0; j < arrivalTimes.size(); j++ )
            {
                PatchArrivalTime current = arrivalTimes.get( j );
                if( current.arrivalTime <= i )
                {
                    // Yup
                    AgentTeam team = _simState.getTeam( current.teamID, false );
                    agentCounts[i] += team.getSize();
                }
            }
        }
        
        // Project the patch depletion
        PatchDepletionData[] depletionData = new PatchDepletionData[ _maxDepletionTimesteps ];
        float startResources = patch.getRemainingResources();
        float remainingResources = startResources;
        float totalResourcesForaged = 0.0f;
        float patchArea = patch.getArea();
        float consumptionRateMax = agent.getResourceConsumptionRate();
        float foragingAreaMax = agent.getMaxForagingArea();
        for( int i = 0; i < _maxDepletionTimesteps; i++ )
        {
            PatchDepletion tempData = calculatePatchDepletion( patchArea,
                    remainingResources,
                    agentCounts[i],
                    patch.getMinAgentForageCount(),
                    foragingAreaMax,
                    consumptionRateMax );
            
            // Update the totals
            remainingResources -= tempData.getTotalResources();
            totalResourcesForaged += tempData.getTotalResources();
            indResources += tempData.getPerAgentResources();
            groupResources += tempData.getTotalResources();
//
//            // Store the data
//            depletionData[i] = new PatchDepletionData( tempData.resourcesGatheredAgent,
//                    indResources,
//                    tempData.resourcesGatheredGroup,
//                    groupResources,
//                    i,
//                    agentCounts[i] );

//            // Compute the density of the remaining resources in the patch
//            float resourceDensity = remainingResources / patchArea;
//            
//            // Compute the effective foraging area for each individual agent
//            float foragingAreaEffective = (float) Math.min( foragingAreaMax,
//                    patchArea / agentCounts[i] );
//            
//            // Compute the amount of resources foraged per agent
//            float resourcesForagedPerAgent = 0.0f;
//            if( 0 < agentCounts[i] )
//            {
//                resourcesForagedPerAgent = (float) Math.min( consumptionRateMax,
//                    foragingAreaEffective * resourceDensity );
//            }
//            
//            // Compute the total amount of resources foraged this timestep
//            float resourcesForaged = resourcesForagedPerAgent * agentCounts[i];
//            
//            // Update the totals
//            remainingResources -= resourcesForaged;
//            totalResourcesForaged += resourcesForaged;
//            indResources += resourcesForagedPerAgent;
//            groupResources += resourcesForaged;
            
            // Compute the slope, but only if we are past the first timestep
            if( i > 0 )
            {
                /* Compute the slope of the line from the origin to the
                 * current data point */
                float currentSlopeInd = indResources / i;
                float currentSlopeGroup = totalResourcesForaged / i;
                
                // Is it the biggest slope for individual resources?
                if( currentSlopeInd > giveUpSlopeInd )
                {
                    // Yup, this is the best give up time so far
                    giveUpSlopeInd = currentSlopeInd;
                    giveUpTimeInd = i;
                }
                
                // Is it the biggest slope for group resources?
                if( currentSlopeGroup > giveUpSlopeGroup )
                {
                    // Yup, this is the best give up time so far
                    giveUpSlopeGroup = currentSlopeGroup;
                    giveUpTimeGroup = i;
                }
            }
        }
        
        PatchValue patchValue = new PatchValue( giveUpTimeInd,
                giveUpSlopeInd,
                giveUpTimeGroup,
                giveUpSlopeGroup,
                indResources,
                groupResources,
                depletionData );
        
//        _LOG.warn( "Patch [" + patch.getID() + "] resources=[" + patch.getRemainingResources() + "] - " + patchValue );
        
        return patchValue;
    }

    public PatchDepletion calculatePatchDepletion( float patchArea,
            float resources,
            int currentAgentCount,
            int minAgentCount,
            float foragingAreaMax,
            float consumptionRateMax )
    {
        // Assume no resources are foraged
        float resourcesForagedPerAgent = 0.0f;
        float totalResourcesForaged = 0.0f;
        
        // Check to ensure that we have sufficient agents
        if( (0 < currentAgentCount)
                && (!_enforcePatchMinimumAgents
                        || (currentAgentCount >= minAgentCount)) )
        {
            // Compute the resource density and effective area per agent
            float resourceDensity = resources / patchArea;
            float foragingAreaEffective = (float) Math.min( foragingAreaMax,
                    patchArea / currentAgentCount );

            // Compute how many resources are foraged
            resourcesForagedPerAgent = (float) Math.min( consumptionRateMax,
                    foragingAreaEffective * resourceDensity );
            totalResourcesForaged = resourcesForagedPerAgent * currentAgentCount;
        }
//        else
//        {
//            _LOG.warn( "No resources foraged: patchArea=["
//                    + patchArea
//                    + "] resources=["
//                    + resources
//                    + "] currentAgentCount=["
//                    + currentAgentCount
//                    + "] minAgentCount=["
//                    + minAgentCount
//                    + "] foragingAreaMax=["
//                    + foragingAreaMax
//                    + "] consumptionRateMax=["
//                    + consumptionRateMax
//                    + "] _enforcePatchMinimumAgents=["
//                    + _enforcePatchMinimumAgents
//                    + "]" );
//        }
        
        return new PatchDepletion( resourcesForagedPerAgent,
                totalResourcesForaged,
                currentAgentCount );
    }
    
    /**
     * Calculate the arrival times of all the teams headed towards the patch
     *
     * @param patch The patch
     * @return The team arrival times
     */
    public List<PatchArrivalTime> findTeamArrivalsAtPatch( Patch patch )
    {
        List<PatchArrivalTime> arrivalTimes = new ArrayList<PatchArrivalTime>();
        
        // Iterate through all the teams
        Iterator<AgentTeam> teamIter = _simState.getAllActiveTeams().values().iterator();
        while( teamIter.hasNext() )
        {
            AgentTeam team = teamIter.next();
            
            // Is the team leader headed to this patch?
            List<Agent> members = team.getMembers();
            Validate.isTrue( members.size() > 0, "An active team must have members" );
            Decision leaderDecision = members.get(0).getDecision();
            Patch teamPatch = leaderDecision.getPatch();
            if( (null != teamPatch) && patch.getID().equals( teamPatch.getID() ) )
            {
                // Yup.  What is its arrival time?
                Agent leader = members.get( 0 );
                Vector3f toPatch = patch.getPosition().subtract( leader.getPosition() );
                float distance = toPatch.length() - patch.getRadius();
                int leaderArrivalTime = (int) Math.ceil( distance /
                        (leader.getMaxSpeed() * _avgSpeedPercentage ) );
                
                // Add the team's arrival time
                arrivalTimes.add( new PatchArrivalTime( team.getID(), leaderArrivalTime ) );
            }
        }
        
        return arrivalTimes;
    }
}
