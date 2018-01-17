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
import java.util.List;
import java.util.Properties;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.AgentTeam;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.util.MiscUtils;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class PatchValueCalculator
{
    public static class PatchDepletionData
    {
        private float _resourcesGathered = 0.0f;
        private float _totalResourcesGathered = 0.0f;
        private int _timesteps = 0;
        private int _agentsPresent = 0;
        
        public PatchDepletionData( float resourcesGathered,
                float totalResourcesGathered,
                int timesteps,
                int agentsPresent)
        {
            _resourcesGathered = resourcesGathered;
            _totalResourcesGathered = totalResourcesGathered;
            _timesteps = timesteps;
            _agentsPresent = agentsPresent;
        }
        
        public float getResourcesGathered()
        {
            return _resourcesGathered;
        }
        
        public float getTotalResourcesGathered()
        {
            return _totalResourcesGathered;
        }
        
        public int getTimesteps()
        {
            return _timesteps;
        }
        
        public int getAgentsPresent()
        {
            return _agentsPresent;
        }
    }
    
    public static class PatchArrivalTime
    {
        private String _teamID = null;
        private int _arrivalTime = Integer.MAX_VALUE;
        
        public PatchArrivalTime( String teamID, int arrivalTime )
        {
            _teamID = teamID;
            _arrivalTime = arrivalTime;
        }
        
        public String getTeamID()
        {
            return _teamID;
        }
        
        public int getArrivalTime()
        {
            return _arrivalTime;
        }
    }
    
    public static class PatchValue
    {
        private int _giveUpTime = 0;
        private float _giveUpSlope = 0.0f;
        private float _indResources = 0.0f;
        private float _teamResources = 0.0f;
        
        public PatchValue( int giveUpTime,
                float giveUpSlope,
                float indResources,
                float teamResources )
        {
            _giveUpTime = giveUpTime;
            _giveUpSlope = giveUpSlope;
            _indResources = indResources;
            _teamResources = teamResources;
        }

        /**
         * Returns the giveUpTime for this object
         *
         * @return The giveUpTime.
         */
        public int getGiveUpTime()
        {
            return _giveUpTime;
        }

        /**
         * Returns the giveUpSlope for this object
         *
         * @return The giveUpSlope.
         */
        public float getGiveUpSlope()
        {
            return _giveUpSlope;
        }

        /**
         * Returns the indResources for this object
         *
         * @return The indResources.
         */
        public float getIndResources()
        {
            return _indResources;
        }

        /**
         * Returns the teamResources for this object
         *
         * @return The teamResources.
         */
        public float getTeamResources()
        {
            return _teamResources;
        }
    }
    
    /** Property key for the maximum timesteps that patch depletion is calculated */
    private static final String _MAX_DEPLETION_TIMESTEPS_KEY =
            "max-depletion-timesteps";

    
    /** The simulation state */
    protected SimulationState _simState = null;

    /** The maximum timesteps that patch depletion is calculated */
    protected int _maxDepletionTimesteps = 0;
    
    
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
        int giveUpTime = 0;
        float giveUpSlope = 0.0f;
        float indResources = 0.0f;
        float teamResources = 0.0f;
        
        // Calculate the agent's arrival time at the patch
        int agentArrivalTime = 0;
        
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
                if( current.getArrivalTime() <= i )
                {
                    // Yup
                    AgentTeam team = _simState.getTeam( current.getTeamID(), false );
                    agentCounts[i] += team.getSize();
                }
            }
        }
        
        // Project the patch depletion
        for( int i = 0; i < _maxDepletionTimesteps; i++ )
        {
            // 
        }
        
        return new PatchValue( giveUpTime, giveUpSlope, indResources, teamResources );
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
        
        
        
        return arrivalTimes;
    }
}
