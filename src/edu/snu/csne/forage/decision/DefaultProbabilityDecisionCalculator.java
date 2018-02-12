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

import java.util.List;
// Imports
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.forage.util.PatchValueCalculator;
import edu.snu.csne.forage.util.PatchValue;
import edu.snu.csne.mates.math.NavigationalVector;
import edu.snu.csne.util.MiscUtils;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class DefaultProbabilityDecisionCalculator
        implements ProbabilityDecisionCalculator
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            DefaultProbabilityDecisionCalculator.class.getName() );
    
    /** Property key for the initiation rate base */
    private static final String _INITIATION_RATE_KEY = "initiation-rate";
    
    /** Property key for the follow alpha constant */
    private static final String _FOLLOW_ALPHA_KEY = "follow-alpha";
    
    /** Property key for the follow beta constant */
    private static final String _FOLLOW_BETA_CONSTANT = "follow-beta";

    /** Pi as a float */
    private static final float _PI = (float) Math.PI;
    
    /** 2*Pi as a float */
    private static final float _TWO_PI = 2.0f * _PI;
    
    
    /** The base initiation rate */
    private float _inititationRate = 0.0f;
    
    /** Follow alpha constant */
    private float _followAlpha = 0.0f;
    
    /** Follow beta constant */
    private float _followBeta = 0.0f;
    
    /** Flag denoting individual patch values should be used */
    private boolean _usePatchValueIndivdiual = false;


    
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

        // Load the initiation rate
        _inititationRate = MiscUtils.loadNonEmptyFloatProperty( props,
                _INITIATION_RATE_KEY,
                "Initiation rate (key="
                        + _INITIATION_RATE_KEY
                        + ") may not be empty" );
        
        // Load the follow alpha constant
        _followAlpha = MiscUtils.loadNonEmptyFloatProperty( props,
                _FOLLOW_ALPHA_KEY,
                "Follow alpha (key="
                        + _FOLLOW_ALPHA_KEY
                        + ") may not be empty" );

        // Load the follow beta constant
        _followBeta = MiscUtils.loadNonEmptyFloatProperty( props,
                _FOLLOW_BETA_CONSTANT,
                "Follow beta (key="
                        + _FOLLOW_BETA_CONSTANT
                        + ") may not be empty" );
        
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
        // Get the mean resultant vector of the agent w.r.t. sesnsed teammates
        NavigationalVector mrv = agent.getMRVForTeam( agent.getTeam().getID() );
        float mrvComponent = (1.0f - mrv.r) * (1.0f - mrv.r)
                / (_navigateMRVSigma * _navigateMRVSigma);
        
        // Get the value of the patch
        PatchValue patchValueData = agent.getPachValue( patch.getID() );
        float patchValueIndMax = agent.getPatchValueIndMax();
        float patchValueGroupMax = agent.getPatchValueGroupMax();
        float patchValue = patchValueData.getGiveUpSlopeGroup() / patchValueGroupMax;
        if( _usePatchValueIndivdiual )
        {
            patchValue = patchValueData.getGiveUpSlopeInd() / patchValueIndMax;
        }
        float patchValueComponent = (1.0f - patchValue) * (1.0f - patchValue)
                / (_navigatePatchValueSigma * _navigatePatchValueSigma);

        // Calculate the difference in direction between the patch and the team
        NavigationalVector toPatch = new NavigationalVector(
                patch.getPosition().subtract( agent.getPosition() ) );
        float dirDiff = toPatch.theta - mrv.theta;
        if( dirDiff < -_PI )
        {
            dirDiff += _TWO_PI;
        }
        else if( dirDiff > _PI )
        {
            dirDiff -= _TWO_PI;
        }
        dirDiff = Math.abs( dirDiff / _PI );
        float dirComponent = (dirDiff * dirDiff)
                / (_navigateDirDiffSigma * _navigateDirDiffSigma);
        
        // Calculate the k-value
        float k = (float) Math.exp( -0.5f * ( mrvComponent
                + patchValueComponent
                + dirComponent ) );
        
        // TODO Auto-generated method stub
        return 0;
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
    public float calculateFollowProbability( Agent leader,
            Agent agent )
    {
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
        
        float mrvDirDiffComponent = (1.0f - mrvDirDiff) * (1.0f - mrvDirDiff)
                / (_followMRVDirDiffSigma * _followMRVDirDiffSigma);

        // Compute the difference in the MRV magnitudes
        float mrvMagDiff = currentMRV.r - leaderMRV.r;
        
        // If it is negative, set it to 0
        if( mrvMagDiff < 0.0f )
        {
            mrvMagDiff = 0.0f;
        }
        
        float mrvMagDiffComponent = (1.0f - mrvMagDiff) * (1.0f - mrvMagDiff)
                / (_followMRVMagDiffSigma * _followMRVMagDiffSigma);
        
        // Iterate through all the patches sensed by the aganet
        NavigationalVector leaderVelocity = new NavigationalVector( leader.getVelocity() );
        List<Patch> sensedPatches = agent.getSensedPatches();
        for( Patch patch : sensedPatches )
        {
            // Compute the difference in the leader's heading and the bearing to the patch
            NavigationalVector toPatch = new NavigationalVector(
                    patch.getPosition().subtract( leader.getPosition() ) );
            float dirDiff = toPatch.theta - leaderVelocity.theta;
            if( dirDiff < -_PI )
            {
                dirDiff += _TWO_PI;
            }
            else if( dirDiff > _PI )
            {
                dirDiff -= _TWO_PI;
            }
            dirDiff = Math.abs( dirDiff / _PI );

            PatchValue patchValueData = agent.getPachValue( patch.getID() );
            float patchValueIndMax = agent.getPatchValueIndMax();
            float patchValueGroupMax = agent.getPatchValueGroupMax();
            float patchValue = patchValueData.getGiveUpSlopeGroup() / patchValueGroupMax;
            if( _usePatchValueIndivdiual )
            {
                patchValue = patchValueData.getGiveUpSlopeInd() / patchValueIndMax;
            }
//            float patchValueComponent = (1.0f - patchValue) * (1.0f - patchValue)
//                    / (_followPatchValueSigma * _followPatchValueSigma);

        }
        
        return 0;
    }

    /**
     * Calculates the probability of foraging in a given patch
     *
     * @param patch The patch in which the agent would forage
     * @param agent The agent making the decision
     * @return The probability
     * @see edu.snu.csne.forage.decision.ProbabilityDecisionCalculator#calculateForageProbability(edu.snu.csne.forage.Patch, edu.snu.csne.forage.Agent)
     */
    @Override
    public float calculateForageProbability( Patch patch, Agent agent )
    {
        // Get the value of the patch
        PatchValue patchValueData = agent.getPachValue( patch.getID() );
        float patchValueIndMax = agent.getPatchValueIndMax();
        float patchValueGroupMax = agent.getPatchValueGroupMax();

        float patchValue = patchValueData.getGiveUpSlopeGroup() / patchValueGroupMax;
        if( _usePatchValueIndivdiual )
        {
            patchValue = patchValueData.getGiveUpSlopeInd() / patchValueIndMax;
        }

        return 0;
    }

}
