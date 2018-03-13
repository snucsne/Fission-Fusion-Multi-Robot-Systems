/*
 * COPYRIGHT
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
        
        DepletionEvent( long simStep, float resourcesDepleted )
        {
            this.simStep = simStep;
            this.resourcesDepleted = resourcesDepleted;
        }
    }
    
    
    protected float _totalResourcesForaged = 0.0f;
    
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
        _depletonEvents.add( new DepletionEvent( -1l, 0.0f ) );
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
            float foragingAreaEffective = (float) Math.min( consumptionRateMax,
                    patchArea / agentCount );
            float resourcesForagedPerAgent = (float) Math.min( consumptionRateMax,
                    foragingAreaEffective * resourceDensity );
            float totalResourcesForaged = resourcesForagedPerAgent * agentCount;
            
            // Tell the patch how much was foraged
            float actualResourcesForaged = patch.setResourcesForaged( totalResourcesForaged );
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
                    totalResourcesForaged ) );
            
            _totalResourcesForaged += totalResourcesForaged;
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
