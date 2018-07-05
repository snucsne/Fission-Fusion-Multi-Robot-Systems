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

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.util.MersenneTwisterFast;
import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.AgentTeam;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.forage.util.PatchValue;
import edu.snu.csne.mates.math.NavigationalVector;
import edu.snu.csne.util.MiscUtils;
import edu.snu.jyperneat.core.Network;
import edu.snu.jyperneat.core.NodeType;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class NeatProbabilityDecisionCalculator
        implements ProbabilityDecisionCalculator
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            NeatProbabilityDecisionCalculator.class.getName() );
    
    /** Property key for the calculator properties file */
    public static final String PROB_DECISION_CALC_PROPS_FILE_KEY = "decision-calc-props-file";
    
    /** Property key for the initiation rate base */
    public static final String _INITIATE_RATE_KEY = "initiate-rate";
    
    /** Property key for the follow alpha */
    public static final String _FOLLOW_ALPHA_KEY = "follow-alpha";
    
    /** Property key for the follow beta */
    public static final String _FOLLOW_BETA_KEY = "follow-beta";

    /** Property key for the forage rate base */
    public static final String _FORAGE_RATE_KEY = "forage-rate";

    /** Property key for the flag indicating individual patch value should be used */
    protected static final String _USE_PATCH_VALUE_INDIVIDUAL_KEY = "use-patch-value-individual";
    
    /** Pi as a float */
    private static final float _PI = (float) Math.PI;
    
    /** 2*Pi as a float */
    private static final float _TWO_PI = 2.0f * _PI;
    
    /** Minimum k-value for a non-zero probability */
    private static final float _MIN_K_VALUE = 0.0000000001f;

    /** Maximum probability */
    private static final float _MAX_PROBABILITY = 0.9999f;

    
    /** The bias node's name */
    private static final String _BIAS_NODE_NAME = "Bias";
    
    /** The initiation active node's name */
    private static final String _INITIATION_ACTIVE_NODE_NAME = "InitiationActive";
    
    /** The initiation's relative position in the group node's name */
    private static final String _INITIATION_REL_POSITION_NODE_NAME = "InitiationRelativePosition";
    
    /** The initiation's  node's name */
    private static final String _INITIATION_PATCH_REL_DIR_DIFF_NODE_NAME = "InitiationPatchRelativeDirDiff";
    
    /** The initiation's  node's name */
    private static final String _INITIATION_PATCH_VALUE_NODE_NAME = "InitiationPatchValue";
    
    /** The following's  node's name */
    private static final String _FOLLOWING_ACIVE_NODE_NAME = "FollowActive";
    
    /** The following's  node's name */
    private static final String _FOLLOWING_LEADER_REL_DIR_DIFF_NODE_NAME = "FollowLeaderRelativeDirDiff";
    
    /** The following's  node's name */
    private static final String _FOLLOWING_PATCH_CONTRIBUTION_NODE_NAME = "FollowPatchContribution";
    
    /** The following's  node's name */
    private static final String _FOLLOWING_SIZE_REQUIREMENTS_MET_NODE_NAME = "FollowSizeRequirementMet";
    
    /** The foraging's  node's name */
    private static final String _FORAGING_ACTIVE_NODE_NAME = "ForageActive";
    
    /** The foraging's  node's name */
    private static final String _FORAGING_PATCH_VALUE_NODE_NAME = "ForagePatchValue";
    
    /** The output node's name */
    private static final String _OUTPUT_NODE_NAME = "Output";

    
    /** The current state of the simulation */
    private SimulationState _simState = null;
    
    /** The network evolved by NEAT */
    private Network _network = null;
    
    /** The base initiation rate */
    private float _inititationRate = 0.0f;
    
    /** Follow alpha constant */
    private float _followAlpha = 0.0f;
    
    /** Follow beta constant */
    private float _followBeta = 0.0f;

    /** Forage base rate */
    private float _forageBaseRate = 0.0f;
    
    /** The random number generator */
    public MersenneTwisterFast _rng = null;
    
    /** Flag denoting individual patch values should be used */
    private boolean _usePatchValueIndivdiual = false;


    
    public NeatProbabilityDecisionCalculator( Network network )
    {
        Validate.notNull( network, "NEAT network may not be null" );
        _network = network;
    }
    
    /**
     * Initialize this agent decision calculator
     *
     * @param simState The state of the simulation
     * @see edu.snu.csne.forage.decision.ProbabilityDecisionCalculator#initialize(edu.snu.csne.forage.SimulationState)
     */
    @Override
    public void initialize( SimulationState simState )
    {
        _LOG.trace( "Entering initialize( simState )" );

        // Get the system properties
        Properties props = simState.getProps();
        _simState = simState;

        _usePatchValueIndivdiual = MiscUtils.loadNonEmptyBooleanProperty( props,
                _USE_PATCH_VALUE_INDIVIDUAL_KEY,
                "Use patch value individual" );
        _LOG.debug( "Using patch value for individuals=["
                + _usePatchValueIndivdiual
                + "]" );

        // Get the random number generator
        _rng = simState.getRNG();

        // If there is a separate props file for calculations, use it
        Properties calcProps = props;
        String calcPropsFilename = System.getProperty( PROB_DECISION_CALC_PROPS_FILE_KEY );
        if( null != calcPropsFilename )
        {
            calcProps = MiscUtils.loadPropertiesFromFile( calcPropsFilename );
            _LOG.info( "Using decision calculator properties file" );
        }
        
        // Load the follow alpha constant
        _followAlpha = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FOLLOW_ALPHA_KEY,
                "Follow alpha" );

        // Load the follow beta constant
        _followBeta = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FOLLOW_BETA_KEY,
                "Follow beta" );

        // Load the base forage rate
        _forageBaseRate = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FORAGE_RATE_KEY,
                "Forage base rate" );

        // Load the initiation rate
        _inititationRate = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _INITIATE_RATE_KEY,
                "Initiation rate" );

        _LOG.trace( "Leaving initialize( simState )" );
    }

    /**
     * Calculate the probability of a given navigation decision
     *
     * @param patch The patch to which the agent would navigate
     * @param agent The agent making the decision
     * @return The probability
     * @see edu.snu.csne.forage.decision.ProbabilityDecisionCalculator#calculateNavigateProbability(edu.snu.csne.forage.Patch, edu.snu.csne.forage.Agent)
     */
    @Override
    public float calculateNavigateProbability( Patch patch, Agent agent )
    {
        // Reset the network inputs
        _network.reinitialize( _rng );
        resetNetworkInputs();
        
        // Get the mean resultant vector of the agent w.r.t. sensed teammates
        NavigationalVector mrv = agent.getMRVForTeam( agent.getTeam().getID() );
        float mrvR = mrv.r;
        if( 1 == agent.getTeam().getSize() )
        {
            mrvR = 1.0f;
        }

        // Get the value of the patch
        float patchValue = getPatchValue( patch, agent );

        // Calculate the difference in direction between the patch and the team
        NavigationalVector toPatch = new NavigationalVector(
                patch.getPosition().subtract( agent.getPosition() ) );
        float dirDiff = toPatch.theta - mrv.theta;
        if( 1 == agent.getTeam().getSize() )
        {
            dirDiff = 0.0f;
        }
        if( dirDiff < -_PI )
        {
            dirDiff += _TWO_PI;
        }
        else if( dirDiff > _PI )
        {
            dirDiff -= _TWO_PI;
        }
        dirDiff = Math.abs( dirDiff / _PI );

        // Set the network inputs
        _network.setValue( _INITIATION_ACTIVE_NODE_NAME, 1.0 );
        _network.setValue( _INITIATION_PATCH_REL_DIR_DIFF_NODE_NAME, dirDiff );
        _network.setValue( _INITIATION_REL_POSITION_NODE_NAME, mrvR );
        _network.setValue( _INITIATION_PATCH_VALUE_NODE_NAME, patchValue );
        // TODO
        
        // Calculate k
        _network.update();
        float k = (float) _network.getValue( _OUTPUT_NODE_NAME );

        // Calculate the probability
        float probability = 0.0f;
        if( k > _MIN_K_VALUE )
        {
            probability = k / ( _inititationRate * agent.getSensedAgents().size() );
        }

        // Ensure it isn't too big
        if( probability > _MAX_PROBABILITY )
        {
            probability = _MAX_PROBABILITY;
        }

        return probability;
    }

    /**
     * Calculate the probability of a given follow decision
     *
     * @param leader The leader which the agent would follow
     * @param agent The agent making the decision
     * @return The probability
     * @see edu.snu.csne.forage.decision.ProbabilityDecisionCalculator#calculateFollowProbability(edu.snu.csne.forage.Agent, edu.snu.csne.forage.Agent)
     */
    @Override
    public float calculateFollowProbability( Agent leader, Agent agent )
    {
        if( leader.getTeam().equals( agent.getTeam() ) )
        {
            _LOG.error( "Team IDs of potential leader ["
                    + leader.getID()
                    + "] and follower ["
                    + agent.getID()
                    + "] match ["
                    + leader.getTeam().getID()
                    + "]" );
        }

        // Reset the network inputs
        _network.reinitialize( _rng );
        resetNetworkInputs();
        
        // Get the leader team's MRV
        NavigationalVector leaderMRV = agent.getMRVForTeam( leader.getTeam().getID() );
        
        // Get the MRV for the agent's current team
        NavigationalVector currentMRV = agent.getMRVForTeam( agent.getTeam().getID() );
        
        // Compute the difference in the MRV angles
        float mrvDirDiff = leaderMRV.theta - currentMRV.theta;
        if( mrvDirDiff < -_PI )
        {
            mrvDirDiff += _TWO_PI;
        }
        else if( mrvDirDiff > _PI )
        {
            mrvDirDiff -= _TWO_PI;
        }
        mrvDirDiff = Math.abs( mrvDirDiff / _PI );
        
        // Compute the difference in the MRV magnitudes
        float mrvMagDiff = currentMRV.r - leaderMRV.r;

        // If it is negative, set it to 0
        if( mrvMagDiff < 0.0f )
        {
            mrvMagDiff = 0.0f;
        }
        
        // Get the patch to which the leader is navigating
        Agent rootLeader = leader.getLeader();
        Patch patch = null;
        if( null != rootLeader )
        {
            patch = rootLeader.getDecision().getPatch();
        }
        float patchValue = getPatchValue( patch, agent );
        int minAgentsToForage = patch.getMinAgentForageCount();
        int leaderTeamSize = agent.getSensedAgentsOnTeam( leader.getTeam() ).size();
        float sizeRequirementMet = leaderTeamSize / (float) minAgentsToForage;
        if( sizeRequirementMet > 1.0f )
        {
            sizeRequirementMet = 1.0f;
        }

        // Set the network inputs
        _network.setValue( _FOLLOWING_ACIVE_NODE_NAME, 1.0 );
        _network.setValue( _FOLLOWING_LEADER_REL_DIR_DIFF_NODE_NAME, mrvDirDiff );
        _network.setValue( _FOLLOWING_PATCH_CONTRIBUTION_NODE_NAME, patchValue );
        _network.setValue( _FOLLOWING_SIZE_REQUIREMENTS_MET_NODE_NAME, sizeRequirementMet );

        // Calculate k
        _network.update();
        float k = (float) _network.getValue( _OUTPUT_NODE_NAME );

        // Compute the probability
        float sensedAgentCount = agent.getSensedAgents().size();
        float probability = 0.0f;
        if( k > _MIN_K_VALUE )
        {
            probability = k / (_followAlpha + ( ( _followBeta
                    * (sensedAgentCount - leaderTeamSize) / leaderTeamSize ) ) );
        }

        // Ensure it isn't too big
        if( probability > _MAX_PROBABILITY )
        {
            probability = _MAX_PROBABILITY;
        }

        return probability;
    }

    /**
     * Calculate the probability that a given agent forages in the
     * current patch
     * 
     * @param agent The agent making the decision
     * @return The probability
     * @see edu.snu.csne.forage.decision.ProbabilityDecisionCalculator#calculateForageProbability(edu.snu.csne.forage.Patch, edu.snu.csne.forage.Agent)
     */
    @Override
    public float calculateForageProbability( Patch patch, Agent agent )
    {
        // Reinitialize the network inputs
        _network.reinitialize( _rng );
        resetNetworkInputs();
        
        // Get the value of the patch
        float patchValue = getPatchValue( patch, agent );

        // Set the network inputs
        _network.setValue( _FORAGING_ACTIVE_NODE_NAME, 1.0 );
        _network.setValue( _FORAGING_PATCH_VALUE_NODE_NAME, patchValue );

        // Calculate k
        _network.update();
        float k = (float) _network.getValue( _OUTPUT_NODE_NAME );
        
        // Calculate the probability
        float probability = 0.0f;
        if( k > _MIN_K_VALUE )
        {
            probability = k / ( _forageBaseRate);
        }
        
        // Ensure it isn't too big
        if( probability > _MAX_PROBABILITY )
        {
            probability = _MAX_PROBABILITY;
        }

        if( _LOG.isDebugEnabled() )
        {
            _LOG.debug( "Forage: k=["
                    + k
                    + "] probability=["
                    + String.format( "%10.8f", probability )
                    + "] resources=["
                    + patch.getRemainingResources()
                    + "] distance=["
                    + patch.getPosition().distance( agent.getPosition() )
                    + "]" );
        }

        return probability;
    }

    /**
     * Reset all the input nodes to 0 and the bias to 1 (just in case)
     */
    private void resetNetworkInputs()
    {
        // Get all the input node names
        List<Object> inputNodeNames = _network.getNodeNamesOfType( NodeType.INPUT );
        Iterator<Object> iter = inputNodeNames.iterator();
        while( iter.hasNext() )
        {
            _network.setValue( iter.next(), 0.0 );
        }
        
        // Set the bias to 1
        _network.setValue( _BIAS_NODE_NAME, 1.0 );
    }
    
    /**
     * Calculates the value of a givent patch for a given agent
     *
     * @param patch The patch
     * @param agent The agent
     * @return The value of the patch
     */
    private float getPatchValue( Patch patch, Agent agent )
    {
        float patchValue = 0.0f;
        if( null != patch )
        {
            PatchValue patchValueData = agent.getPachValue( patch.getID() );
            float patchValueIndMax = agent.getPatchValueIndMax();
            float patchValueGroupMax = agent.getPatchValueGroupMax();
            patchValue = patchValueData.getGiveUpSlopeGroup() / patchValueGroupMax;
            if( _usePatchValueIndivdiual )
            {
                patchValue = patchValueData.getGiveUpSlopeInd() / patchValueIndMax;
            }
        }
        
        return patchValue;
    }
}
