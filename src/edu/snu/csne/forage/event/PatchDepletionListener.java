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
package edu.snu.csne.forage.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.forage.decision.DecisionType;
import edu.snu.csne.forage.util.PatchDepletion;
import edu.snu.csne.forage.util.PatchDepletionCalculator;
import edu.snu.csne.forage.util.PatchDepletionCalculator.PatchDepletionData;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class PatchDepletionListener extends AbstractSimulationEventListener
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            PatchDepletionListener.class.getName() );
    
    /** PI as a float */
    protected static final float _PI = (float) Math.PI;
    
    
    private class DepletionEvent
    {
        final long simStep;
        final float resourcesDepleted;
        final String patchID;
        
        DepletionEvent( long simStep, float resourcesDepleted, String patchID )
        {
            this.simStep = simStep;
            this.resourcesDepleted = resourcesDepleted;
            this.patchID = patchID;
        }
    }
    
    
    protected float _totalResourcesForaged = 0.0f;
    
    protected PatchDepletionCalculator _patchDepletionCalc = null;
    
    /** History of depletion events */
    protected List<DepletionEvent> _depletonEvents =
            new LinkedList<PatchDepletionListener.DepletionEvent>();
    
    /** Mapping of patches to the agents foraging in them */
    private Map<Patch, List<Agent>> _foragingAgents =
            new HashMap<Patch, List<Agent>>();
    
    
    /**
     * Initializes this event listener
     *
     * @param simState The simulation state
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#initialize(edu.snu.csne.forage.SimulationState)
     */
    @Override
    public void initialize( SimulationState simState )
    {
        // Call the superclass implementation
        super.initialize( simState );
        
        // Add a zero depletion event to help with later calculations
        _depletonEvents.add( new DepletionEvent( -1l, 0.0f, null ) );
        
        // Get the patch depletion calculator
        _patchDepletionCalc = simState.getPatchDepletionCalculator();
    }

    /**
     * Prepares for a simulation step
     *
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#simStepSetup()
     */
    @Override
    public void simStepSetup()
    {
        // Empty out the list of agents foraging in each patch
        Iterator<List<Agent>> foragingAgenstIter = _foragingAgents.values().iterator();
        while( foragingAgenstIter.hasNext() )
        {
            List<Agent> agents = foragingAgenstIter.next();
            agents.clear();
        }
    }

    /**
     * Performs any cleanup after a simulation step
     *
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#simStepTearDown()
     */
    @Override
    public void simStepTearDown()
    {
        // Iterate through each patch with foraging agents
        Iterator<Patch> patchIter = _foragingAgents.keySet().iterator();
        while( patchIter.hasNext() )
        {
            // Get the patch and the agents foraging in it
            Patch patch = patchIter.next();
            List<Agent> patchForagingAgents = _foragingAgents.get( patch );
            
            // If there are no agents, move on to the next patch
            int agentCount = patchForagingAgents.size();
            if( 0 >= agentCount )
            {
                continue;
            }
            
            /* For now, assume all agents have the same max foraging area
             * and consumption rate */
            Agent agent = patchForagingAgents.get( 0 );
            float foragingAreaMax = agent.getMaxForagingArea();
            float consumptionRateMax = agent.getResourceConsumptionRate();
            
            // Compute patch specific values
            float patchArea = patch.getRadius() * patch.getRadius() * _PI;
            float resourceDensity = patch.getRemainingResources()
                    / patchArea;
            
            // Compute how many resources are foraged
            float foragingAreaEffective = (float) Math.min( foragingAreaMax,
                    patchArea / agentCount );
            float resourcesForagedPerAgent = (float) Math.min( consumptionRateMax,
                    foragingAreaEffective * resourceDensity );
            float totalResourcesForaged = resourcesForagedPerAgent * agentCount;
            
            
            // ************
            PatchDepletion depletionData = _patchDepletionCalc.calculatePatchDepletion(
                    patch.getArea(),
                    patch.getRemainingResources(),
                    agentCount,
                    0,
                    agent.getMaxForagingArea(),
                    agent.getResourceConsumptionRate() );
            resourcesForagedPerAgent = depletionData.getPerAgentResources();
            totalResourcesForaged = depletionData.getTotalResources();
            // ************
            
            // Determine how much was actually foraged
//            float totalResourcesForaged = depletionData.resourcesGatheredGroup;
//            float resourcesForagedPerAgent = depletionData.resourcesGatheredAgent;
            float actualResourcesForaged = patch.setResourcesForaged(
                    totalResourcesForaged );
//            if( actualResourcesForaged < depletionData.resourcesGatheredGroup )
//            {
//                resourcesForagedPerAgent = actualResourcesForaged / agentCount;
//            }
            if( actualResourcesForaged < totalResourcesForaged )
            {
                resourcesForagedPerAgent = actualResourcesForaged / agentCount;
            }

            // Notify each agent how much it foraged
            Iterator<Agent> agentIter = patchForagingAgents.iterator();
            while( agentIter.hasNext() )
            {
                agent = agentIter.next();
                agent.addResourcesForaged( resourcesForagedPerAgent );
            }
            
            // Log it
            _depletonEvents.add( new DepletionEvent( _simState.getCurrentSimulationStep(),
                    actualResourcesForaged,
                    patch.getID() ) );
            
            _totalResourcesForaged += totalResourcesForaged;

//            _LOG.warn( "Patch depletion: time=["
//                    + _simState.getCurrentSimulationStep()
//                    + "] resources=["
//                    + totalResourcesForaged
//                    + "] patch=["
//                    + patch.getID()
//                    + "] agentCount=["
//                    + agentCount
//                    + "] remaining=["
//                    + patch.getRemainingResources()
//                    + "] patchArea=["
//                    + patchArea
//                    + "] foragingAreaEffective=["
//                    + foragingAreaEffective
//                    + "] resourcesForagedPerAgent=["
//                    + resourcesForagedPerAgent
//                    + "] density=["
//                    + resourceDensity
//                    + "] foragingAreaMax=["
//                    + foragingAreaMax
//                    + "] consumptionRateMax=["
//                    + consumptionRateMax
//                    + "]" );
        }
    }

    /**
     * TODO Method description
     *
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#simRunTearDown()
     */
    @Override
    public void simRunTearDown()
    {
        // TODO Log the results
    }

    /**
     * Performs any processing necessary to handle an agent making a decision
     * @param event The decision
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#agentDecided(edu.snu.csne.forage.event.DecisionEvent)
     */
    @Override
    public void agentDecided( DecisionEvent event )
    {
        // We are only interested in foraging decisions
        if( DecisionType.FORAGE.equals( event.getDecision().getType() ) )
        {
            Agent agent = event.getAgent();
            Patch patch = event.getDecision().getPatch();
            
            // Ensure nothing went wrong
            Validate.notNull( agent, "Decision has null agent" );
            Validate.notNull( patch, "Forage decision has null patch" );
            
            // Get the list of foraging agents for the patch
            List<Agent> patchForagingAgents = _foragingAgents.get( patch );
            if( null == patchForagingAgents )
            {
                patchForagingAgents = new LinkedList<Agent>();
                _foragingAgents.put( patch, patchForagingAgents );
            }
            
            // Add the agent to the list
            patchForagingAgents.add( agent );
        }
    }
    
    /**
     * Returns the total amount of resources foraged by agents in this simulation
     *
     * @return
     */
    public float getTotalResourcesForaged()
    {
        return _totalResourcesForaged;
    }

}
