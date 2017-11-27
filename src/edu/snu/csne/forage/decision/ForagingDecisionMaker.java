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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jme3.math.Vector3f;
import ec.util.MersenneTwisterFast;
import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.forage.SimulationState;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class ForagingDecisionMaker extends AbstractAgentDecisionMaker
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            ForagingDecisionMaker.class.getName() );
    
    /** Threshold distance squared to determine if patch locations are the same */
    private static final float _MAX_SAME_PATCH_DIST_SQUARED = 0.02f;
    
    
    /** Random number generator */
    private MersenneTwisterFast _rng = null;

    /** The decision builder */
    private DecisionBuilder _decisionBuilder = new DecisionBuilder();

    
    /**
     * Initialize this agent decision-maker
     *
     * @param simState The state of the simulation
     * @param props The configuration properties
     * @see edu.snu.csne.forage.decision.AbstractAgentDecisionMaker#initialize(edu.snu.csne.forage.SimulationState, java.util.Properties)
     */
    @Override
    public void initialize( SimulationState simState, Properties props )
    {
        _LOG.trace( "Entering initialize( simState, props )" );

        // Call the superclass implementation
        super.initialize( simState, props );
        
        // Get the random number generator
        _rng = simState.getRNG();

        // Initialize the decision builder
        _decisionBuilder.initialize( simState, props );
        
        _LOG.trace( "Leaving initialize( simState, props )" );
    }

    /**
     * Makes a decision upon which the agent will act
     *
     * @param agent The agent making the decision
     * @return The decision
     * @see edu.snu.csne.forage.decision.AgentDecisionMaker#decide(edu.snu.csne.forage.Agent)
     */
    @Override
    public Decision decide( Agent agent )
    {
        _LOG.trace( "Entering decide( agent )" );

        // Create a list of all possible decisions
        List<Decision> allDecisions = new LinkedList<Decision>();
        allDecisions.addAll( buildAllNavigateDecisions( agent ) );
        allDecisions.addAll( buildAllFollowDecisions( agent ) );
        allDecisions.addAll( buildAllForageDecisions( agent ) );
        
        // Sum all the decision probabilities
        float decisionProbabilitiesSum = 0.0f;
        Iterator<Decision> decisionIter = allDecisions.iterator();
        while( decisionIter.hasNext() )
        {
            decisionProbabilitiesSum += decisionIter.next().getProbability();
        }
        
        // Get the minimum probability of continuing the same decision
        float sameDecisionProbabilityMin = 0.0f;
        
        // If the sum is larger than 1 minus the same decision minimum, scale everything
        float scale = 1.0f;
        if( decisionProbabilitiesSum > (1.0f - sameDecisionProbabilityMin) )
        {
            scale = 1.0f / (decisionProbabilitiesSum + sameDecisionProbabilityMin);
        }
        
        // Make a decision (defaulting to the current decision)
        Decision decision = agent.getDecision();
        boolean done = false;
        float randomDecision = _rng.nextFloat();
        decisionIter = allDecisions.iterator();
        while( decisionIter.hasNext() && !done )
        {
            Decision possibleDecision = decisionIter.next();
            
            // What is the probability?
            float probability = scale * possibleDecision.getProbability();
            
            // Is this the one?
            if( probability > randomDecision )
            {
                // Yup
                decision = possibleDecision;
                done = true;
            }
            else
            {
                // Nope
                randomDecision -= probability;
            }
        }
        
        _LOG.trace( "Leaving decide( agent )" );
        
        return decision;
    }

    /**
     * Builds a list of all possible navigation decisions for a given agent
     *
     * @param agent The agent associated with the decisions
     * @return The navigate decisions
     */
    private List<Decision> buildAllNavigateDecisions( Agent agent )
    {
        List<Decision> navDecisions = new LinkedList<Decision>();

        // See if the agent is already currently moving toward a patch
        Decision currentDecision = agent.getDecision();
        Vector3f currentDestination = null;
        if( DecisionType.NAVIGATE.equals( currentDecision.getType() ) )
        {
            currentDestination = currentDecision.getDestination();
        }
        
        // The agent can potentially navigate to any known patch
        List<Patch> patches = agent.getSensedPatches();
        Iterator<Patch> patchIter = patches.iterator();
        while( patchIter.hasNext() )
        {
            Patch patch = patchIter.next();
            
            // If the agent is already in the patch, skip it as the agent
            // can forage here now
            if( patch.isInPatch( agent ) )
            {
                continue;
            }
            
            // If the agent is already navigating to this patch, skip it
            if( (null != currentDestination)
                    && (_MAX_SAME_PATCH_DIST_SQUARED >= currentDestination.distanceSquared( patch.getPosition() ) ) )
            {
                continue;
            }
            
            // Calculate the probability of navigating to this patch
            float probability = 0.0f;
            
            // Build the decision
            Decision decision = _decisionBuilder.buildPatchNavigateDecision(
                    agent,
                    patch,
                    probability );
            
            // Add it to the list
            navDecisions.add( decision );
        }
        
        return navDecisions;
    }
    
    /**
     * Builds a list of all possible follow decisions for a given agent
     *
     * @param agent The agent associated with the decisions
     * @return The follow decisions
     */
    private List<Decision> buildAllFollowDecisions( Agent agent )
    {
        List<Decision> followDecisions = new LinkedList<Decision>();
        
        // The agent follow any known team leader
        Map<String,Agent> teamLeaders = agent.getSensedTeamLeaders();
        Iterator<Agent> leaderIter = teamLeaders.values().iterator();
        while( leaderIter.hasNext() )
        {
            Agent leader = leaderIter.next();
            
            // Skip it if the agent already belongs to this team
            if( agent.getTeam().equals( leader.getTeam() ) )
            {
                continue;
            }
            
            // Calculate the probability of following this leader
            float probability = 0.0f;
            
            // Build the decision
            Decision decision = _decisionBuilder.buildFollowDecision(
                    agent,
                    leader,
                    probability );
            
            // Add it to the list
            followDecisions.add( decision );
        }
        
        return followDecisions;
    }
    
    /**
     * Builds all the possible foraging decisions for a given agent
     *
     * @param agent The agent associated with the decisions
     * @return The foraging decisions
     */
    private List<Decision> buildAllForageDecisions( Agent agent )
    {
        List<Decision> forageDecisions = new LinkedList<Decision>();
        
        /* The agent can forage in any patch in which they are currently
         * located.  While that should be a maximum of one, it is possible,
         * patches could overlap (but that would cause problems). */
        List<Patch> patches = agent.getSensedPatches();
        Iterator<Patch> patchIter = patches.iterator();
        while( patchIter.hasNext() )
        {
            Patch patch = patchIter.next();
            
            // Is the agent in this patch?
            if( patch.isInPatch( agent ) )
            {
                // Yup.  They can forage here
                // Calculate the probability
                float probability = 0.0f;
                Decision decision = _decisionBuilder.buildForageDecision(
                        agent,
                        patch,
                        probability );
                
                // Add it to the list of decisions
                forageDecisions.add( decision );
            }
        }

        return forageDecisions;
    }
}
