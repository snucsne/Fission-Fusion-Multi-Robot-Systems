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
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jme3.math.Vector3f;
import ec.util.MersenneTwisterFast;
import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.forage.SimulationState;
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
    
    /** Property key for the calculator properties file */
    public static final String PROB_DECISION_CALC_PROPS_FILE_KEY = "decision-calc-props-file";
    
    
    /** Property key for the initiation rate base */
    public static final String _INITIATE_RATE_KEY = "initiate-rate";
    
    /** Property key for the initiation k exponent multiplier */
    public static final String _INITIATE_K_EXP_MULT_KEY = "initiate-k-exp-multiplier";
    
    /** Property key for the initiation k exponent offset */
    public static final String _INITIATE_K_EXP_OFFSET_KEY = "initiate-k-exp-offset";
    
    /** Property key for the initiation MRV sigma */
    public static final String _INITIATE_MRV_SIGMA_KEY = "initiate-mrv-sigma";
    
    /** Property key for the initiation MRV flag */
    public static final String _INITIATE_MRV_FLAG_KEY = "initiate-mrv-flag";
    
    /** Property key for the initiation patch value sigma */
    public static final String _INITIATE_PATCH_VALUE_SIGMA_KEY = "initiate-patch-value-sigma";
    
    /** Property key for the initiation patch value flag */
    public static final String _INITIATE_PATCH_VALUE_FLAG_KEY = "initiate-patch-value-flag";
    
    /** Property key for the initiation direction difference sigma */
    public static final String _INITIATE_DIR_DIFF_SIGMA_KEY = "initiate-dir-diff-sigma";
    
    /** Property key for the initiation direction difference flag */
    public static final String _INITIATE_DIR_DIFF_FLAG_KEY = "initiate-dir-diff-flag";
    

    /** Property key for the follow alpha */
    public static final String _FOLLOW_ALPHA_KEY = "follow-alpha";
    
    /** Property key for the follow beta */
    public static final String _FOLLOW_BETA_KEY = "follow-beta";

    /** Property key for the follow k exponent multiplier */
    public static final String _FOLLOW_K_EXP_MULT_KEY = "follow-k-exp-multiplier";
    
    /** Property key for the follow k exponent offset */
    public static final String _FOLLOW_K_EXP_OFFSET_KEY = "follow-k-exp-offset";
    
    /** Property key for the follow MRV difference sigma */
//    public static final String _FOLLOW_MRV_DIFF_SIGMA_KEY = "follow-mrv-diff-sigma";

    /** Property key for the follow mean position relative distance sigma */
    public static final String _FOLLOW_MEAN_POS_REL_DIST_SIGMA_KEY = "follow-mean-pos-rel-dist-sigma";

    /** Property key for the follow mean position relative distance flag */
    public static final String _FOLLOW_MEAN_POS_REL_DIST_FLAG_KEY = "follow-mean-pos-rel-dist-flag";

    /** Property key for the follow MRV direction difference sigma */
    public static final String _FOLLOW_MRV_DIR_DIFF_SIGMA_KEY = "follow-mrv-dir-diff-sigma";
    
    /** Property key for the follow MRV direction difference flag */
    public static final String _FOLLOW_MRV_DIR_DIFF_FLAG_KEY = "follow-mrv-dir-diff-flag";
    
    /** Property key for the follow MRV magnitude difference sigma */
    public static final String _FOLLOW_MRV_MAG_DIFF_SIGMA_KEY = "follow-mrv-mag-diff-sigma";

    /** Property key for the follow MRV magnitude difference flag */
    public static final String _FOLLOW_MRV_MAG_DIFF_FLAG_KEY = "follow-mrv-mag-diff-flag";

    
    /** Property key for the forage rate base */
    public static final String _FORAGE_RATE_KEY = "forage-rate";

    /** Property key for the forage k exponent multiplier */
    public static final String _FORAGE_K_EXP_MULT_KEY = "forage-k-exp-multiplier";
    
    /** Property key for the forage k exponent offset */
    public static final String _FORAGE_K_EXP_OFFSET_KEY = "forage-k-exp-offset";
    
    /** Property key for the forage patch value sigma */
    public static final String _FORAGE_PATCH_VALUE_SIGMA_KEY = "forage-patch-value-sigma";

    /** Property key for the forage patch value flag */
    public static final String _FORAGE_PATCH_VALUE_FLAG_KEY = "forage-patch-value-flag";

    
    
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
    
    
    /** The random number generator */
    public MersenneTwisterFast _rng = null;
    
    /** The base initiation rate */
    private float _inititationRate = 0.0f;
    
    /** Initiation k exponent multiplier */
    private float _initiationKExpMultiplier = 0.0f;
    
    /** Initiation k exponent offset */
    private float _initiationKExpOffset = 0.0f;

    /** Initiation MRV sigma */
    private float _navigateMRVSigma = 0.0f;
    
    /** Initiation MRV flag */
    private boolean _navigateMRVFlag = false;
    
    /** Initiation patch value sigma */
    private float _navigatePatchValueSigma = 0.0f;
    
    /** Initiation patch value flag */
    private boolean _navigatePatchValueFlag = false;
    
    /** Initiation direction difference sigma */
    private float _navigateDirDiffSigma = 0.0f;
    
    /** Initiation direction difference flag */
    private boolean _navigateDirDiffFlag = false;
    
    /** Maximum navigation k value */
    private float _maxNavigateK = 0.0f;
    

    /** Follow alpha constant */
    private float _followAlpha = 0.0f;
    
    /** Follow beta constant */
    private float _followBeta = 0.0f;
    
    /** Follow k exponent multiplier */
    private float _followKExpMultiplier = 0.0f;
    
    /** Follow k exponent offset */
    private float _followKExpOffset = 0.0f;
    
    /** Follow MRV direction difference sigma */
    private float _followMRVDirDiffSigma = 0.0f;
    
    /** Follow MRV direction difference flag */
    private boolean _followMRVDirDiffFlag = false;
    
    /** Follow MRV magnitude difference sigma */
    private float _followMRVMagDiffSigma = 0.0f;
    
    /** Follow MRV magnitude difference flag */
    private boolean _followMRVMagDiffFlag = false;
    
    /** Follow relative distance sigma */
    private float _followRelDistanceSigma = 0.0f;
    
    /** Follow relative distance flag */
    private boolean _followRelDistanceFlag = false;
    
    /** Maximum follow k value */
    private float _maxFollowK = 0.0f;
    
    
    /** Forage base rate */
    private float _forageBaseRate = 0.0f;
    
    /** Forage k exponent multiplier */
    private float _forageKExpMultilier = 0.0f;
    
    /** Forage k exponent offset */
    private float _forageKExpOffset = 0.0f;
    
    /** Forage patch value sigma */
    private float _foragePatchValueSigma = 0.0f;
    
    /** Forage patch value flag */
    private boolean _foragePatchValueFlag = false;
    
    /** Maximum forage k value */
    private float _maxForageK = 0.0f;
    
    
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
        
        // Load the initiation rate
        _inititationRate = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _INITIATE_RATE_KEY,
                "Initiation rate" );
        
        _initiationKExpMultiplier = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _INITIATE_K_EXP_MULT_KEY,
                "Initiation rate" );
        
        _initiationKExpOffset = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _INITIATE_K_EXP_OFFSET_KEY,
                "Initiation rate" );
        
        // Load the navigate MRV values
        _navigateMRVSigma = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _INITIATE_MRV_SIGMA_KEY,
                "Navigate MRV sigma" );
        _navigateMRVFlag = MiscUtils.loadNonEmptyBooleanProperty( calcProps,
                _INITIATE_MRV_FLAG_KEY,
                "Navigate MRV flag" );
        
        // Load the navigate patch value values
        _navigatePatchValueSigma = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _INITIATE_PATCH_VALUE_SIGMA_KEY,
                "Navigate patch value sigma" );
        _navigatePatchValueFlag = MiscUtils.loadNonEmptyBooleanProperty( calcProps,
                _INITIATE_PATCH_VALUE_FLAG_KEY,
                "Navigate patch value flag" );

        // Load the navigate direction difference values
        _navigateDirDiffSigma = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _INITIATE_DIR_DIFF_SIGMA_KEY,
                "Navigate direction difference sigma" );
        _navigateDirDiffFlag = MiscUtils.loadNonEmptyBooleanProperty( calcProps,
                _INITIATE_DIR_DIFF_FLAG_KEY,
                "Navigate direction difference flag" );

        /** Compute the max navigation k value */
        _maxNavigateK = (1.0f/(1.0f + (float) Math.exp( _initiationKExpMultiplier
                * (-_initiationKExpOffset))));

//        _LOG.info( "Initiation values: _inititationRate=["
//                +  _inititationRate
//                + "] _initiationKExpMultiplier=["
//                + _initiationKExpMultiplier
//                + "] _initiationKExpOffset=["
//                + _initiationKExpOffset
//                + "] _navigateMRVSigma=["
//                +  _navigateMRVSigma
//                + "] _navigatePatchValueSigma=["
//                + _navigatePatchValueSigma
//                + "] _navigateDirDiffSigma=["
//                + _navigateDirDiffSigma
//                + "] _maxNavigateK=["
//                + _maxNavigateK
//                + "]" );
        
        
        // Load the follow alpha constant
        _followAlpha = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FOLLOW_ALPHA_KEY,
                "Follow alpha" );

        // Load the follow beta constant
        _followBeta = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FOLLOW_BETA_KEY,
                "Follow beta" );
        
        // Load the follow k exponent multiplier
        _followKExpMultiplier = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FOLLOW_K_EXP_MULT_KEY,
                "follow base rate" );

        // Load the follow k exponent offset value
        _followKExpOffset = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FOLLOW_K_EXP_OFFSET_KEY,
                "follow base rate" );

        // Load the follow relative distance values
        _followRelDistanceSigma = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FOLLOW_MEAN_POS_REL_DIST_SIGMA_KEY,
                "follow mean position relative distance sigma" );
        _followRelDistanceFlag = MiscUtils.loadNonEmptyBooleanProperty( calcProps,
                _FOLLOW_MEAN_POS_REL_DIST_FLAG_KEY,
                "follow mean position relative distance flag" );

        // Load the follow MRV direction difference values
        _followMRVDirDiffSigma = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FOLLOW_MRV_DIR_DIFF_SIGMA_KEY,
                "follow MRV direction difference sigma" );
        _followMRVDirDiffFlag = MiscUtils.loadNonEmptyBooleanProperty( calcProps,
                _FOLLOW_MRV_DIR_DIFF_FLAG_KEY,
                "follow MRV direction difference flag" );
        
        // Load the follow MRV magnitude difference values
        _followMRVMagDiffSigma = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FOLLOW_MRV_MAG_DIFF_SIGMA_KEY,
                "follow MRV magnitude difference sigma" );
        _followMRVMagDiffFlag = MiscUtils.loadNonEmptyBooleanProperty( calcProps,
                _FOLLOW_MRV_MAG_DIFF_FLAG_KEY,
                "follow MRV magnitude difference flag" );

        // Compute the max follow k value
        _maxFollowK = (1.0f/(1.0f + (float) Math.exp( _followKExpMultiplier
                * (-_followKExpOffset))));

//        _LOG.info( "Follow values: _followAlpha=["
//                + _followAlpha
//                + "] _followBeta=["
//                + _followBeta
//                + "] _followMRVDirDiffSigma=["
//                + _followMRVDirDiffSigma
//                + "] _followMRVMagDiffSigma=["
//                + _followMRVMagDiffSigma
//                + "] _usePatchValueIndivdiual=["
//                + _usePatchValueIndivdiual
//                + "] _maxFollowK=["
//                + _maxFollowK
//                + "]" );
        
        
        // Load the base forage rate
        _forageBaseRate = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FORAGE_RATE_KEY,
                "Forage base rate" );

        // Load the forage k exponent multiplier
        _forageKExpMultilier = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FORAGE_K_EXP_MULT_KEY,
                "Forage base rate" );

        // Load the forage k exponent offset value
        _forageKExpOffset = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FORAGE_K_EXP_OFFSET_KEY,
                "Forage base rate" );

        // Load the forage patch value values
        _foragePatchValueSigma = MiscUtils.loadNonEmptyFloatProperty( calcProps,
                _FORAGE_PATCH_VALUE_SIGMA_KEY,
                "forage patch value sigma" );
        _foragePatchValueFlag = MiscUtils.loadNonEmptyBooleanProperty( calcProps,
                _FORAGE_PATCH_VALUE_FLAG_KEY,
                "forage patch value flag" );

        // Compute the max forage k value
        _maxForageK = (1.0f/(1.0f + (float) Math.exp( _forageKExpMultilier
                * (-_forageKExpOffset))));

//        _LOG.info( "Forage values: _forageBaseRate=["
//                + _forageBaseRate
//                + "] _forageKExpMultilier=["
//                + _forageKExpMultilier
//                + "] _forageKExpOffset=["
//                + _forageKExpOffset
//                + "] _foragePatchValueSigma=["
//                + _foragePatchValueSigma
//                + "] _maxForageK=["
//                + _maxForageK
//                + "]" );

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
        // Get the mean resultant vector of the agent w.r.t. sensed teammates
        NavigationalVector mrv = agent.getMRVForTeam( agent.getTeam().getID() );
        float mrvComponent = 0.0f;
        float mrvR = mrv.r;
        if( 1 == agent.getTeam().getSize() )
        {
            mrvR = 1.0f;
//            _LOG.warn( "Using single size team" );
        }
        if( _navigateMRVFlag && _navigateMRVSigma > 0.0f )
        {
            mrvComponent = (1.0f - mrv.r) * (1.0f - mrv.r)
                / (_navigateMRVSigma * _navigateMRVSigma);
        }
//        _LOG.debug( "mrv.r=[" + mrv.r + "]" );
        
        // Get the value of the patch
        float patchValue = getPatchValue( patch, agent );
        float patchValueComponent = 0.0f;
        if( _navigatePatchValueFlag && _navigatePatchValueSigma > 0.0f )
        {
            patchValueComponent = (1.0f - patchValue) * (1.0f - patchValue)
                / (_navigatePatchValueSigma * _navigatePatchValueSigma);
        }

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
        float dirComponent = 0.0f;
        if( _navigateDirDiffFlag && _navigateDirDiffSigma > 0.0f )
        {
            dirComponent = (dirDiff * dirDiff)
                    / (_navigateDirDiffSigma * _navigateDirDiffSigma);
        }
        
        // Calculate the k-value
        float k = (1.0f / (1.0f + (float) Math.exp( _initiationKExpMultiplier * ( mrvComponent
                + patchValueComponent
                + dirComponent
                - _initiationKExpOffset ) ) ) )
                / _maxNavigateK;
        
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

        if( _LOG.isDebugEnabled() )
        {
            _LOG.debug( "Navigate: patch=["
                    + patch.getID()
                    + "] patchValue=["
                    + patchValue
                    + "] mrvR=["
                    + mrvR
                    + "] mrvComponent=["
                    + mrvComponent
                    + "] patchValueComponent=["
                    + patchValueComponent
                    + "] dirComponent=["
                    + dirComponent
                    + "] k=["
                    + k
                    + "] probability=["
                    + String.format( "%10.8f", probability )
                    + "]" );
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
        _LOG.debug( "Final: mrvDirDiff=[" + mrvDirDiff + "]" );
        
        float mrvDirDiffComponent = 0.0f;
        if( _followMRVDirDiffFlag && _followMRVDirDiffSigma > 0.0f )
        {
            mrvDirDiffComponent = (1.0f - mrvDirDiff) * (1.0f - mrvDirDiff)
                / (_followMRVDirDiffSigma * _followMRVDirDiffSigma);
        }
        
        // Compute the difference in the MRV magnitudes
        float mrvMagDiff = currentMRV.r - leaderMRV.r;

        // If it is negative, set it to 0
        if( mrvMagDiff < 0.0f )
        {
            mrvMagDiff = 0.0f;
        }
        
        float mrvMagDiffComponent = 0.0f;
        if( _followMRVMagDiffFlag && _followMRVMagDiffSigma > 0.0f )
        {
            mrvMagDiffComponent = (1.0f - currentMRV.r) * (1.0f - currentMRV.r)
                / (_followMRVMagDiffSigma * _followMRVMagDiffSigma);
        }
        _LOG.debug( "currentMRV.r=[" + currentMRV.r + "]" );
        
        // Compute the difference in the distance to the mean position
        Vector3f leaderTeamMeanPosition = agent.getMeanPositionOfTeam(
                leader.getTeam().getID() );
        Vector3f leaderTeamRelPosition = leaderTeamMeanPosition.subtract(
                agent.getPosition() );
        Vector3f currentTeamMeanPosition = agent.getMeanPositionOfTeam(
                agent.getTeam().getID() );
        Vector3f currentTeamRelPosition = currentTeamMeanPosition.subtract(
                agent.getPosition() );
        
        float relDistance = leaderTeamRelPosition.lengthSquared() / currentTeamRelPosition.lengthSquared();
        float relDistanceComponent = 0.0f;
        if( _followRelDistanceFlag && _followRelDistanceSigma > 0.0f )
        {
            relDistanceComponent = (relDistance) * (relDistance)
                    / (_followRelDistanceSigma * _followRelDistanceSigma);
        }
        _LOG.debug( "relDistance=[" + relDistance + "]" );
        
//        // Iterate through all the patches sensed by the aganet
//        NavigationalVector leaderVelocity = new NavigationalVector( leader.getVelocity() );
//        List<Patch> sensedPatches = agent.getSensedPatches();
//        for( Patch patch : sensedPatches )
//        {
//            // Compute the difference in the leader's heading and the bearing to the patch
//            NavigationalVector toPatch = new NavigationalVector(
//                    patch.getPosition().subtract( leader.getPosition() ) );
//            float dirDiff = toPatch.theta - leaderVelocity.theta;
//            if( dirDiff < -_PI )
//            {
//                dirDiff += _TWO_PI;
//            }
//            else if( dirDiff > _PI )
//            {
//                dirDiff -= _TWO_PI;
//            }
//            dirDiff = Math.abs( dirDiff / _PI );
//
//            float patchValue = getPatchValue( patch, agent );
////            float patchValueComponent = (1.0f - patchValue) * (1.0f - patchValue)
////                    / (_followPatchValueSigma * _followPatchValueSigma);
//        }

        // Compute the k value
//        float k = (float) Math.exp( -2.0f * ( mrvDirDiffComponent
//                + mrvMagDiffComponent ) );
        float positionComponent = mrvDirDiffComponent + mrvMagDiffComponent + relDistanceComponent;
        float k = (1.0f/(1.0f + (float) Math.exp( _followKExpMultiplier * (positionComponent - _followKExpOffset))))
                / _maxFollowK;

        // Compute the probability
        float departed = leader.getTeam().getSize();
        float currentTeamSize = agent.getTeam().getSize();
        float groupSize = agent.getSensedAgents().size();
        float probability = 0.0f;
        if( k > _MIN_K_VALUE )
        {
            probability = k / (_followAlpha + ( ( _followBeta * currentTeamSize / departed ) ) );
        }

        // Ensure it isn't too big
        if( probability > _MAX_PROBABILITY )
        {
            probability = _MAX_PROBABILITY;
        }

        if( _LOG.isDebugEnabled() )
        {
            _LOG.debug( "Follow: mrvDirDiffComponent=["
                    + mrvDirDiffComponent
                    + "] mrvMagDiffComponent=["
                    + mrvMagDiffComponent
                    + "] k=["
                    + k
                    + "] currentTeamSize=["
                    + currentTeamSize
                    + "] departed=["
                    + departed
                    + "] groupSize=["
                    + groupSize
                    + "] probability=["
                    + String.format( "%10.8f", probability )
                    + "]" );
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
        // Get the value of the patch
        float patchValue = getPatchValue( patch, agent );
        float patchValueComponent = 0.0f;
        if( _foragePatchValueSigma > 0.0f )
        {
            patchValueComponent = (1.0f - patchValue) * (1.0f - patchValue)
                / (_foragePatchValueSigma * _foragePatchValueSigma);
        }

        // Calculate the k-value
        float k = (1.0f / (1.0f + (float) Math.exp( _initiationKExpMultiplier * ( patchValueComponent
                - _initiationKExpOffset ) ) ) )
                / _maxForageK;
        
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
            _LOG.debug( "Forage: patchValueComponent=["
                    + patchValueComponent
                    + "] k=["
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

//    /**
//     * Calculate all the probabilities that a given agent forages in the
//     * patches it currently is in
//     * 
//     * @param agent The agent making the decision
//     * @return The probabilities for foraging at each patch indexed by patch ID
//     * @see edu.snu.csne.forage.decision.ProbabilityDecisionCalculator#calculatePatchForageProbabilities(edu.snu.csne.forage.Agent)
//     */
//    @Override
//    public Map<String, Float> calculatePatchForageProbabilities( Agent agent )
//    {
//        Map<String,Float> patchForageProbabilities = new HashMap<String, Float>();
//        
//        /* The agent can forage in any patch in which they are currently
//         * located.  While that should be a maximum of one, it is possible,
//         * patches could overlap (but that would cause problems). */
//        float patchValueSum = 0.0f;
//        List<Patch> patches = agent.getSensedPatches();
//        Iterator<Patch> patchIter = patches.iterator();
//        while( patchIter.hasNext() )
//        {
//            Patch patch = patchIter.next();
//            
//            // Is the agent in this patch?
//            if( patch.isInPatch( agent ) )
//            {
//                // Yup.  They can forage here
//
//                // Get the value of the patch
//                float patchValue = getPatchValue( patch, agent );
//                
//                // Save it
//                patchForageProbabilities.put( patch.getID(), patchValue );
//                patchValueSum += patchValue;
//            }
//        }
//        
//        // Calculate the probabilities
//        Iterator<String> patchIDIter = patchForageProbabilities.keySet().iterator();
//        while( patchIDIter.hasNext() )
//        {
//            String patchID = patchIDIter.next();
//            float patchValue = patchForageProbabilities.get( patchID );
//            patchForageProbabilities.put( patchID, patchValue / patchValueSum );
//        }
//        
//        return patchForageProbabilities;
//    }

    /**
     * Calculates the value of a givent patch for a given agent
     *
     * @param patch The patch
     * @param agent The agent
     * @return The value of the patch
     */
    private float getPatchValue( Patch patch, Agent agent )
    {
        PatchValue patchValueData = agent.getPachValue( patch.getID() );
        float patchValueIndMax = agent.getPatchValueIndMax();
        float patchValueGroupMax = agent.getPatchValueGroupMax();
        float patchValue = patchValueData.getGiveUpSlopeGroup() / patchValueGroupMax;
        if( _usePatchValueIndivdiual )
        {
            patchValue = patchValueData.getGiveUpSlopeInd() / patchValueIndMax;
        }
        
//        _LOG.warn( "Max patch value=["
//                + patchValueIndMax
//                + "] patchValue=["
//                + patchValue
//                + "] - "
//                + patchValueData );
//        _LOG.debug( "patchValueIndMax=["
//                + patchValueIndMax
//                + "] patchValueGroupMax=["
//                + patchValueGroupMax
//                + "] giveUpSlopeGroup=["
//                + patchValueData.getGiveUpSlopeGroup()
//                + "] giveUpSlopeInd=["
//                + patchValueData.getGiveUpSlopeInd()
//                + "]" );

        return patchValue;
    }
}
