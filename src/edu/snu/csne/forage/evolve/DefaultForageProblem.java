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
package edu.snu.csne.forage.evolve;

// Imports
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.vector.BitVectorIndividual;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.forage.Simulator;
import edu.snu.csne.forage.decision.DefaultProbabilityDecisionCalculator;
import edu.snu.csne.forage.event.PatchDepletionListener;
import edu.snu.csne.forage.evolve.FoldProperties.FoldType;
import edu.snu.csne.forage.evolve.FoldProperties.PropertyType;
import edu.snu.csne.util.MiscUtils;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class DefaultForageProblem extends Problem
        implements SimpleProblemForm, IndividualDescriber
{
    /** Default serial version UID */
    private static final long serialVersionUID = 1L;
    
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            DefaultForageProblem.class.getName() );

    /** Parameter key for the initiation base rate */
    private static final String _P_INITIATE_BASE_RATE = "initiate-base-rate.";
    
    /** Parameter key for the initiation K exponent multiplier */
    private static final String _P_INITIATE_K_EXP_MULT = "initiate-k-exp-mult.";
    
    /** Parameter key for the inititation K exponent offset */
    private static final String _P_INITIATE_K_EXP_OFFSET = "initiate-k-exp-offset.";
    
    /** Parameter key for the initiation MRV length sigma */
    private static final String _P_INITIATE_MRV_LEN_SIGMA = "initiate-mrv-len-sigma.";
    
    /** Parameter key for the initiation MRV length flag */
    private static final String _P_INITIATE_MRV_LEN_FLAG = "initiate-mrv-len-flag.";
    
    /** Parameter key for the initiation patch value sigma */
    private static final String _P_INITIATE_PATCH_VALUE_SIGMA = "initiate-patch-value-sigma.";
    
    /** Parameter key for the initiation patch value flag */
    private static final String _P_INITIATE_PATCH_VALUE_FLAG = "initiate-patch-value-flag.";
    
    /** Parameter key for the initiation direction difference sigma */
    private static final String _P_INITIATE_DIR_DIFF_SIGMA = "initiate-dir-diff-sigma.";

    /** Parameter key for the initiation direction difference flag */
    private static final String _P_INITIATE_DIR_DIFF_FLAG = "initiate-dir-diff-flag.";

    
    /** Parameter key for the follow alpha */
    private static final String _P_FOLLOW_ALPHA = "follow-alpha.";

    /** Parameter key for the follow beta */
    private static final String _P_FOLLOW_BETA = "follow-beta.";

    /** Parameter key for the follow K exponent multiplier */
    private static final String _P_FOLLOW_K_EXP_MULT = "follow-k-exp-mult.";

    /** Parameter key for the follow K exponent offset */
    private static final String _P_FOLLOW_K_EXP_OFFSET = "follow-k-exp-offset.";

//    /** Parameter key for the follow MRV difference sigma */
//    private static final String _P_FOLLOW_MRV_DIFF_SIGMA = "follow-mrv-diff-sigma.";

    /** Parameter key for the follow mean position relative distance sigma */
    private static final String _P_FOLLOW_MEAN_POS_REL_DIST_SIGMA = "follow-mean-pos-rel-dist-sigma.";

    /** Parameter key for the follow mean position relative distance flag */
    private static final String _P_FOLLOW_MEAN_POS_REL_DIST_FLAG = "follow-mean-pos-rel-dist-flag.";

    /** Parameter key for the follow MRV direction difference sigma */
    private static final String _P_FOLLOW_MRV_DIR_DIFF_SIGMA = "follow-mrv-dir-diff-sigma.";

    /** Parameter key for the follow MRV direction difference flag */
    private static final String _P_FOLLOW_MRV_DIR_DIFF_FLAG = "follow-mrv-dir-diff-flag.";

    /** Parameter key for the follow MRV magnitude difference sigma */
    private static final String _P_FOLLOW_MRV_MAG_DIFF_SIGMA = "follow-mrv-mag-diff-sigma.";

    /** Parameter key for the follow MRV magnitude difference flag */
    private static final String _P_FOLLOW_MRV_MAG_DIFF_FLAG = "follow-mrv-mag-diff-flag.";

    
    /** Parameter key for the forage base rate */
    private static final String _P_FORAGE_BASE_RATE = "forage-base-rate.";

    /** Parameter key for the forage K exponent multiplier */
    private static final String _P_FORAGE_K_EXP_MULT = "forage-k-exp-mult.";

    /** Parameter key for the forage K exponent offset */
    private static final String _P_FORAGE_K_EXP_OFFSET = "forage-k-exp-offset.";

    /** Parameter key for the forage patch value sigma */
    private static final String _P_FORAGE_PATCH_VALUE_SIGMA = "forage-patch-value-sigma.";

    /** Parameter key for the forage patch value flag */
    private static final String _P_FORAGE_PATCH_VALUE_FLAG = "forage-patch-value-flag.";


    /** Parameter key postfix for scaling factors */
    private static final String _SCALING_POSTFIX = "scaling";

    /** Parameter key postfix for codon size */
    private static final String _CODON_SIZE_POSTFIX = "codon-size";

    
    /** Parameter key for the minimum k exponent multiplier value */
    private static final String _P_MIN_K_EXP_MULT_VALUE = "min-k-exp-mult-value";
    
    /** Parameter key for the minimum k exponent offset value */
    private static final String _P_MIN_K_EXP_OFFSET_VALUE = "min-k-exp-offset-value";
    
    /** Parameter key for the minimum sigma value */
    private static final String _P_MIN_SIGMA_VALUE = "min-sigma-value";

    /** Parameter key for the fold properties file */
    private static final String _P_FOLD_PROPERTIES = "fold-properties";
    
    /** Parameter key for flag to force re-evaluation of individuals */
    private static final String _P_FORCE_REEVALUATION = "force-reevaluation";

    
    /** Initiation base rate: codon size */
    private int _initiateBaseRateCodonSize = 0;
    
    /** Initiation base rate: scaling */
    private float _initiateBaseRateScaling = 0.0f;
    
    /** Initiation k exponent multiplier: codon size */
    private int _initiateKExpMultCodonSize = 0;
    
    /** Initiation k exponent multiplier scaling */
    private float _initiateKExpMultScaling = 0.0f;
    
    /** Initiation k exponent offset: codon size */
    private int _initiateKExpOffsetCodonSize = 0;
    
    /** Initiation k exponent offset: scaling */
    private float _initiateKExpOffsetScaling = 0.0f;
    
    /** Initiation MRV length sigma: codon size */
    private int _initiateMRVLenSigmaCodonSize = 0;
    
    /** Initiation MRV length sigma: scaling */
    private float _initiateMRVLenSigmaScaling = 0.0f;
    
    /** Initiation MRV length flag: codon size */
    private int _initiateMRVLenFlagCodonSize = 0;
    
    /** Initiation patch value sigma: codon size */
    private int _initiatePatchValueSigmaCodonSize = 0;
    
    /** Initiation patch value sigma: scaling */
    private float _initiatePatchValueSigmaScaling = 0.0f;
    
    /** Initiation patch value flag: codon size */
    private int _initiatePatchValueFlagCodonSize = 0;
    
    /** Initiation direction difference sigma: codon size */
    private int _initiateDirDiffSigmaCodonSize = 0;
    
    /** Initiation direction difference sigma: scaling */
    private float _initiateDirDiffSigmaScaling = 0.0f;

    /** Initiation direction difference flag: codon size */
    private int _initiateDirDiffFlagCodonSize = 0;
    
    
    
    /** Follow alpha: codon size */
    private int _followAlphaCodonSize = 0;
    
    /** Follow alpha: scaling */
    private float _followAlphaScaling = 0.0f;
    
    /** Follow beta: codon size */
    private int _followBetaCodonSize = 0;
    
    /** Follow beta: scaling */
    private float _followBetaScaling = 0.0f;
    
    /** Follow k exponent multiplier: codon size */
    private int _followKExpMultCodonSize = 0;
    
    /** Follow k exponent multiplier: scaling */
    private float _followKExpMultScaling = 0.0f;
    
    /** Follow k exponent offset: codon size */
    private int _followKExpOffsetCodonSize = 0;
    
    /** Follow k exponent offset: scaling */
    private float _followKExpOffsetScaling = 0.0f;
    
//    /** Follow MRV difference sigma: codon size */
//    private int _followMRVDiffSigmaCodonSize = 0;
//    
//    /** Follow MRV difference sigma: scaling */
//    private float _followMRVDiffSigmaScaling = 0.0f;
    
    /** Follow relative distance in mean position sigma: codon size */
    private int _followMeanPosRelDistSigmaCodonSize = 0;
    
    /** Follow relative distance in mean position sigma: scaling */
    private float _followMeanPosRelDistSigmaScaling = 0.0f;
    
    /** Follow relative distance in mean position flag: codon size */
    private int _followMeanPosRelDistFlagCodonSize = 0;
    
    /** Follow MRV direction difference sigma: codon size */
    private int _followMRVDirDiffSigmaCodonSize = 0;
    
    /** Follow MRV direction difference sigma : scaling */
    private float _followMRVDirDiffSigmaScaling = 0.0f;
    
    /** Follow MRV direction difference flag: codon size */
    private int _followMRVDirDiffFlagCodonSize = 0;
    
    /** Follow MRV magnitude difference sigma: codon size */
    private int _followMRVMagDiffSigmaCodonSize = 0;
    
    /** Follow MRV magnitude difference sigma: scaling */
    private float _followMRVMagDiffSigmaScaling = 0.0f;
    
    /** Follow MRV magnitude difference flag: codon size */
    private int _followMRVMagDiffFlagCodonSize = 0;
    

    /** Forage base rate: codon size */
    private int _forageBaseRateCodonSize = 0;
    
    /** Forage base rate: scaling */
    private float _forageBaseRateScaling = 0.0f;
    
    /** Forage k exponent multiplier: codon size */
    private int _forageKExpMultCodonSize = 0;
    
    /** Forage k exponent multiplier: scaling */
    private float _forageKExpMultScaling = 0.0f;
    
    /** Forage k exponent offset: codon size */
    private int _forageKExpOffsetCodonSize = 0;
    
    /** Forage k exponent offset: scaling */
    private float _forageKExpOffsetScaling = 0.0f;
    
    /** Forage patch value sigma: codon size */
    private int _foragePatchValueSigmaCodonSize = 0;
    
    /** Forage patch value sigma: scaling */
    private float _foragePatchValueSigmaScaling = 0.0f;
    
    /** Forage patch value flag: codon size */
    private int _foragePatchValueFlagCodonSize = 0;
    

    /** Default simulator properties */
    private Properties _defaultSimProperties = null;
    
    /** Fold property files */
    private FoldProperties _foldProps = new FoldProperties();
    
    /** Flag indicating that individuals should be re-evaluated every generation */
    private boolean _forceReevaluation = false;
    
    /** Minimum value for k exponent multipliers */
    private float _minKExpMultValue = 0.0f;
    
    /** Minimum value for k exponent offsets */
    private float _minKExpOffsetValue = 0.0f;
    
    /** Minimum value for sigmas */
    private float _minSigmaValue = 0.0f;

    
    /**
     * Sets up the object by reading it from the parameters stored in
     * state, built off of the parameter base base.
     *
     * @param state
     * @param base
     * @see ec.Problem#setup(ec.EvolutionState, ec.util.Parameter)
     */
    @Override
    public void setup( EvolutionState state, Parameter base )
    {
        _LOG.trace( "Entering setup( state, base )" );

        // Call the superclass implementation
        super.setup( state, base );

        // Load the initiation base rate values
        _initiateBaseRateCodonSize = loadIntParameter(
                _P_INITIATE_BASE_RATE + _CODON_SIZE_POSTFIX,
                "initiation base rate codon size",
                state,
                base );
        _initiateBaseRateScaling = loadFloatParameter(
                _P_INITIATE_BASE_RATE + _SCALING_POSTFIX,
                "initiation base rate scaling",
                state,
                base );
        
        // Load the initiation k exponent multiplier values
        _initiateKExpMultCodonSize = loadIntParameter(
                _P_INITIATE_K_EXP_MULT + _CODON_SIZE_POSTFIX,
                "initiation k exponent multiplier codon size",
                state,
                base );
        _initiateKExpMultScaling = loadFloatParameter(
                _P_INITIATE_K_EXP_MULT + _SCALING_POSTFIX,
                "initiation k exponent multiplier scaling",
                state,
                base );
        
        // Load the initiation k exponent offset values
        _initiateKExpOffsetCodonSize = loadIntParameter(
                _P_INITIATE_K_EXP_OFFSET + _CODON_SIZE_POSTFIX,
                "initiation k exponent offset codon size",
                state,
                base );
        _initiateKExpOffsetScaling = loadFloatParameter(
                _P_INITIATE_K_EXP_OFFSET + _SCALING_POSTFIX,
                "initiation k exponent offset scaling",
                state,
                base );
        
        // Load the initiation MRV length sigma values
        _initiateMRVLenSigmaCodonSize = loadIntParameter(
                _P_INITIATE_MRV_LEN_SIGMA + _CODON_SIZE_POSTFIX,
                "initiation MRV length sigma codon size",
                state,
                base );
        _initiateMRVLenSigmaScaling = loadFloatParameter(
                _P_INITIATE_MRV_LEN_SIGMA + _SCALING_POSTFIX,
                "initiation MRV length sigma scaling",
                state,
                base );
        _initiateMRVLenFlagCodonSize = loadIntParameter(
                _P_INITIATE_MRV_LEN_FLAG + _CODON_SIZE_POSTFIX,
                "initiation MRV length flag codon size",
                state,
                base );

        // Load the initiation patch value values
        _initiatePatchValueSigmaCodonSize = loadIntParameter(
                _P_INITIATE_PATCH_VALUE_SIGMA + _CODON_SIZE_POSTFIX,
                "initiation patch value sigma codon size",
                state,
                base );
        _initiatePatchValueSigmaScaling = loadFloatParameter(
                _P_INITIATE_PATCH_VALUE_SIGMA + _SCALING_POSTFIX,
                "initiation patch value scaling",
                state,
                base );
        _initiatePatchValueFlagCodonSize = loadIntParameter(
                _P_INITIATE_PATCH_VALUE_FLAG + _CODON_SIZE_POSTFIX,
                "initiation patch value flag codon size",
                state,
                base );

        // Load the initiation direction difference values
        _initiateDirDiffSigmaCodonSize = loadIntParameter(
                _P_INITIATE_DIR_DIFF_SIGMA + _CODON_SIZE_POSTFIX,
                "initiation direction difference sigma codon size",
                state,
                base );
        _initiateDirDiffSigmaScaling = loadFloatParameter(
                _P_INITIATE_DIR_DIFF_SIGMA + _SCALING_POSTFIX,
                "initiation direction difference scaling",
                state,
                base );
        _initiateDirDiffFlagCodonSize = loadIntParameter(
                _P_INITIATE_DIR_DIFF_FLAG + _CODON_SIZE_POSTFIX,
                "initiation direction difference flag codon size",
                state,
                base );

        
        // Load the follow alpha values
        _followAlphaCodonSize = loadIntParameter(
                _P_FOLLOW_ALPHA + _CODON_SIZE_POSTFIX,
                "follow alpha codon size",
                state,
                base );
        _followAlphaScaling = loadFloatParameter(
                _P_FOLLOW_ALPHA + _SCALING_POSTFIX,
                "follow alpha scaling",
                state,
                base );

        // Load the follow beta values
        _followBetaCodonSize= loadIntParameter(
                _P_FOLLOW_BETA + _CODON_SIZE_POSTFIX,
                "follow beta codon size",
                state,
                base );
        _followBetaScaling = loadFloatParameter(
                _P_FOLLOW_BETA + _SCALING_POSTFIX,
                "follow beta scaling",
                state,
                base );
        
        // Load the follow k exponent multiplier values
        _followKExpMultCodonSize = loadIntParameter(
                _P_FOLLOW_K_EXP_MULT + _CODON_SIZE_POSTFIX,
                "follow k exponent multiplier codon size",
                state,
                base );
        _followKExpMultScaling = loadFloatParameter(
                _P_FOLLOW_K_EXP_MULT + _SCALING_POSTFIX,
                "follow k exponent multiplier scaling",
                state,
                base );
        
         // Load the follow k exponent offset values
        _followKExpOffsetCodonSize = loadIntParameter(
                _P_FOLLOW_K_EXP_OFFSET + _CODON_SIZE_POSTFIX,
                "follow k exponent offset codon size",
                state,
                base );
        _followKExpOffsetScaling= loadFloatParameter(
                _P_FOLLOW_K_EXP_OFFSET + _SCALING_POSTFIX,
                "follow k exponent offset scaling",
                state,
                base );
        
//         // Load the follow MRV difference sigma values
//        _followMRVDiffSigmaCodonSize= loadIntParameter(
//                _P_FOLLOW_MRV_DIFF_SIGMA + _CODON_SIZE_POSTFIX,
//                "follow MRV difference sigma codon size",
//                state,
//                base );
//        _followMRVDiffSigmaScaling = loadFloatParameter(
//                _P_FOLLOW_MRV_DIFF_SIGMA + _SCALING_POSTFIX,
//                "follow MRV difference sigma scaling",
//                state,
//                base );
//        
        // Load the follow mean position relative distance sigma values
        _followMeanPosRelDistSigmaCodonSize = loadIntParameter(
                _P_FOLLOW_MEAN_POS_REL_DIST_SIGMA + _CODON_SIZE_POSTFIX,
                "follow mean position relative distance sigma codon size",
                state,
                base );
        _followMeanPosRelDistSigmaScaling = loadFloatParameter(
                _P_FOLLOW_MEAN_POS_REL_DIST_SIGMA + _SCALING_POSTFIX,
                "follow mean position relative distance sigma scaling",
                state,
                base );
        _followMeanPosRelDistFlagCodonSize = loadIntParameter(
                _P_FOLLOW_MEAN_POS_REL_DIST_FLAG + _CODON_SIZE_POSTFIX,
                "follow mean position relative distance flag codon size",
                state,
                base );

        // Load the follow MRV direction difference sigma values
       _followMRVDirDiffSigmaCodonSize= loadIntParameter(
               _P_FOLLOW_MRV_DIR_DIFF_SIGMA + _CODON_SIZE_POSTFIX,
               "follow MRV direction difference sigma codon size",
               state,
               base );
       _followMRVDirDiffSigmaScaling = loadFloatParameter(
               _P_FOLLOW_MRV_DIR_DIFF_SIGMA + _SCALING_POSTFIX,
               "follow MRV direction difference sigma scaling",
               state,
               base );
       _followMRVDirDiffFlagCodonSize= loadIntParameter(
               _P_FOLLOW_MRV_DIR_DIFF_FLAG + _CODON_SIZE_POSTFIX,
               "follow MRV direction difference flag codon size",
               state,
               base );

       // Load the follow MRV magnitude difference sigma values
       _followMRVMagDiffSigmaCodonSize = loadIntParameter(
               _P_FOLLOW_MRV_MAG_DIFF_SIGMA + _CODON_SIZE_POSTFIX,
               "follow MRV magnitude difference sigma codon size",
               state,
               base );
       _followMRVMagDiffSigmaScaling = loadFloatParameter(
               _P_FOLLOW_MRV_MAG_DIFF_SIGMA + _SCALING_POSTFIX,
               "follow MRV magnitude difference sigma scaling",
               state,
               base );
       _followMRVMagDiffFlagCodonSize = loadIntParameter(
               _P_FOLLOW_MRV_MAG_DIFF_FLAG + _CODON_SIZE_POSTFIX,
               "follow MRV magnitude difference flag codon size",
               state,
               base );

        // Load the forage base rate values
        _forageBaseRateCodonSize = loadIntParameter(
                _P_FORAGE_BASE_RATE + _CODON_SIZE_POSTFIX,
                "forage base rate codon size",
                state,
                base );
        _forageBaseRateScaling = loadFloatParameter(
                _P_FORAGE_BASE_RATE + _SCALING_POSTFIX,
                "forage base rate scaling",
                state,
                base );
        
        // Load the forage k exponent multiplier values
        _forageKExpMultCodonSize = loadIntParameter(
                _P_FORAGE_K_EXP_MULT + _CODON_SIZE_POSTFIX,
                "forage k exponent multiplier codon size",
                state,
                base );
        _forageKExpMultScaling = loadFloatParameter(
                _P_FORAGE_K_EXP_MULT + _SCALING_POSTFIX,
                "forage k exponent multiplier scaling",
                state,
                base );
        
        // Load the forage k exponent offset values
        _forageKExpOffsetCodonSize = loadIntParameter(
                _P_FORAGE_K_EXP_OFFSET + _CODON_SIZE_POSTFIX,
                "forage k exponent offset codon size",
                state,
                base );
        _forageKExpOffsetScaling = loadFloatParameter(
                _P_FORAGE_K_EXP_OFFSET + _SCALING_POSTFIX,
                "forage k exponent offset scaling",
                state,
                base );
        
        // Load the forage patch value sigma values
        _foragePatchValueSigmaCodonSize = loadIntParameter(
                _P_FORAGE_PATCH_VALUE_SIGMA + _CODON_SIZE_POSTFIX,
                "forage patch value sigma codon size",
                state,
                base );
        _foragePatchValueSigmaScaling = loadFloatParameter(
                _P_FORAGE_PATCH_VALUE_SIGMA + _SCALING_POSTFIX,
                "forage patch value sigma scaling",
                state,
                base );
        _foragePatchValueFlagCodonSize = loadIntParameter(
                _P_FORAGE_PATCH_VALUE_FLAG + _CODON_SIZE_POSTFIX,
                "forage patch value flag codon size",
                state,
                base );

        // Load the default simulator properties
        _defaultSimProperties = MiscUtils.loadProperties( Simulator.PROPS_FILE_KEY );
        
        // Load the fold properties
        Validate.isTrue( state.parameters.exists(
                base.push( _P_FOLD_PROPERTIES ), null ),
                "Fold properties file is required " );
        String foldPropsDefFile = state.parameters.getString(
                 base.push( _P_FOLD_PROPERTIES ),
                 null );
        _LOG.info( "Using foldPropsDefFile=[" + foldPropsDefFile + "]" );
        _foldProps.initialize( foldPropsDefFile );

        // Load the minimum values
        _minKExpMultValue = loadFloatParameter(
                _P_MIN_K_EXP_MULT_VALUE,
                "minimum k exponent multiplier value",
                state,
                base );
        _minKExpOffsetValue = loadFloatParameter(
                _P_MIN_K_EXP_OFFSET_VALUE,
                 "minimum k exponent offset value",
                 state,
                 base );
        _minSigmaValue = loadFloatParameter(
                _P_MIN_SIGMA_VALUE,
                 "minimum sigma value",
                 state,
                 base );

        // Do we force reevaluation?
        Validate.isTrue( state.parameters.exists(
                base.push( _P_FORCE_REEVALUATION ), null ),
                "Force reevaluation flag is required " );
        _forceReevaluation = state.parameters.getBoolean(
                 base.push( _P_FORCE_REEVALUATION ),
                 null,
                 false );
        _LOG.info( "Using _forceReevaluation=[" + _forceReevaluation + "]" );

        _LOG.trace( "Leaving setup( state, base )" );
    }

    /**
     * Evaluates the individual in ind, if necessary (perhaps not
     * evaluating them if their evaluated flags are true), and sets
     * their fitness appropriately.
     *
     * @param state
     * @param ind
     * @param subpopulation
     * @param threadnum
     * @see ec.simple.SimpleProblemForm#evaluate(ec.EvolutionState, ec.Individual, int, int)
     */
    @Override
    public void evaluate( EvolutionState state,
            Individual ind,
            int subpopulation,
            int threadnum )
    {
        // Has the individual already been evaluated?
        if( ind.evaluated && !_forceReevaluation )
        {
            // Yup, bail out early
            return;
        }

        // Is it the correct type of individual
        if( !(ind instanceof BitVectorIndividual) )
        {
            // Nope, complain
            _LOG.error( "Individual is not of correct type ["
                    + ind.getClass().getCanonicalName()
                    + "]" );
            state.output.fatal( "Individual is not the correct type" );
        }

        evaluate( state, ind, FoldType.TRAINING );
    }

    public void evaluate( EvolutionState state,
            Individual ind,
            FoldType foldType )
    {
        // Cast it to the proper type
        BitVectorIndividual bitInd = (BitVectorIndividual) ind;

        // Decode the genome
        Properties genomeProps = decodeGenome( bitInd.genome );
        
        // Get fold training properties
        String[] foldAgentProperties = _foldProps.getProperties(
                foldType,
                PropertyType.AGENT );
        _LOG.debug( "Agent fold properties: type=["
                + foldType.name()
                + "] count=["
                + foldAgentProperties.length
                + "]" );
        String[] foldPatchProperties = _foldProps.getProperties(
                foldType,
                PropertyType.PATCH );
        _LOG.debug( "Patch fold properties: type=["
                + foldType.name()
                + "] count=["
                + foldPatchProperties.length
                + "]" );

        // Iterate through all the training properties
        float totalResourcesForaged = 0.0f;
        float[] fitnessValues = new float[foldAgentProperties.length];
        for( int i = 0; i < foldAgentProperties.length; i++ )
        {
//            _LOG.info( "Running sim [" + i + "]" );
            
            // Get the default properties
            Properties simProps = new Properties();
            simProps.putAll( _defaultSimProperties );
            
            // Override the fold specific properties
            simProps.setProperty( SimulationState._AGENT_PROPS_FILE_KEY,
                    foldAgentProperties[i] );
            simProps.setProperty( SimulationState._PATCH_PROPS_FILE_KEY,
                    foldPatchProperties[i] );
            
            // Override the genome specific properties
            simProps.putAll( genomeProps );
            
            // Create the simulator
            Simulator sim = new Simulator();
            sim.initialize( simProps );
            
            // Add our own patch depletion listener
            PatchDepletionListener patchListener = new PatchDepletionListener();
            SimulationState simState = sim.getSimState();
            simState.addEventListener( patchListener );
            
            // Run it
            sim.run();
            
            // Get the resources foraged
            float resourcesForaged = patchListener.getTotalResourcesForaged();
            totalResourcesForaged += resourcesForaged;
            fitnessValues[i] = resourcesForaged;

//            _LOG.debug( "Run ["
//                    + i
//                    + "] resourcesForaged=["
//                    + resourcesForaged
//                    + "]" );
        }
        
        // Compute the mean resources foraged
        float meanResourcesForaged = totalResourcesForaged / foldAgentProperties.length;
        _LOG.debug( "Fitness ["
                + foldType.name()
                + "]: totalResourcesForaged=["
                + totalResourcesForaged
                + "] foldAgentProperties.length=["
                + foldAgentProperties.length
                + "] meanResourcesForaged=["
                + meanResourcesForaged
                + "]" );
        
        // Save the fitness
        CrossValidationFitness cvFitness = (CrossValidationFitness) ind.fitness;
        if( FoldType.TRAINING.equals( foldType ) )
        {
            cvFitness.setTrainingResults( fitnessValues );
            cvFitness.setFitness( state, cvFitness.getTrainingFitnessMean(), false );
        }
        else if( FoldType.TESTING.equals( foldType ) )
        {
            cvFitness.setTestingResults( fitnessValues );
        }
        else if( FoldType.VALIDATION.equals( foldType ) )
        {
            cvFitness.setValidationResults( fitnessValues );
        }
        else
        {
            _LOG.error( "Unknown fold type ["
                    + foldType
                    + "]" );
            state.output.fatal( "Unknown fold type ["
                    + foldType
                    + "]" );
        }
        
        // Mark the individual as evaluated
        bitInd.evaluated = true;
    }
    
    /**
     * Returns a description of the specified individual using the specified
     * line prefix.
     *
     * @param ind The individual to describe
     * @param prefix The prefix for every line in the description
     * @return A description of the individual
     * @see edu.snu.csne.forage.evolve.IndividualDescriber#describe(ec.Individual, java.lang.String)
     */
    @Override
    public String describe( Individual ind, String prefix )
    {
        // Cast it to the proper type
        BitVectorIndividual bitInd = (BitVectorIndividual) ind;

        // Decode the genome
        Properties genomeProps = decodeGenome( bitInd.genome );

        StringBuilder builder = new StringBuilder();
        builder.append( prefix );
        builder.append( "decoded-genome =" );
        
        String[] genomeKeys =  { DefaultProbabilityDecisionCalculator._INITIATE_RATE_KEY,
                DefaultProbabilityDecisionCalculator._INITIATE_K_EXP_MULT_KEY,
                DefaultProbabilityDecisionCalculator._INITIATE_K_EXP_OFFSET_KEY,
                DefaultProbabilityDecisionCalculator._INITIATE_MRV_SIGMA_KEY,
                DefaultProbabilityDecisionCalculator._INITIATE_MRV_FLAG_KEY,
                DefaultProbabilityDecisionCalculator._INITIATE_PATCH_VALUE_SIGMA_KEY,
                DefaultProbabilityDecisionCalculator._INITIATE_PATCH_VALUE_FLAG_KEY,
                DefaultProbabilityDecisionCalculator._INITIATE_DIR_DIFF_SIGMA_KEY,
                DefaultProbabilityDecisionCalculator._INITIATE_DIR_DIFF_FLAG_KEY,
                DefaultProbabilityDecisionCalculator._FOLLOW_ALPHA_KEY,
                DefaultProbabilityDecisionCalculator._FOLLOW_BETA_KEY,
                DefaultProbabilityDecisionCalculator._FOLLOW_K_EXP_MULT_KEY,
                DefaultProbabilityDecisionCalculator._FOLLOW_K_EXP_OFFSET_KEY,
                DefaultProbabilityDecisionCalculator._FOLLOW_MEAN_POS_REL_DIST_SIGMA_KEY,
                DefaultProbabilityDecisionCalculator._FOLLOW_MEAN_POS_REL_DIST_FLAG_KEY,
                DefaultProbabilityDecisionCalculator._FOLLOW_MRV_DIR_DIFF_SIGMA_KEY,
                DefaultProbabilityDecisionCalculator._FOLLOW_MRV_DIR_DIFF_FLAG_KEY,
                DefaultProbabilityDecisionCalculator._FOLLOW_MRV_MAG_DIFF_SIGMA_KEY,
                DefaultProbabilityDecisionCalculator._FOLLOW_MRV_MAG_DIFF_FLAG_KEY,
                DefaultProbabilityDecisionCalculator._FORAGE_RATE_KEY,
                DefaultProbabilityDecisionCalculator._FORAGE_K_EXP_MULT_KEY,
                DefaultProbabilityDecisionCalculator._FORAGE_K_EXP_OFFSET_KEY,
                DefaultProbabilityDecisionCalculator._FORAGE_PATCH_VALUE_SIGMA_KEY,
                DefaultProbabilityDecisionCalculator._FORAGE_PATCH_VALUE_FLAG_KEY };
        
        for( int i = 0; i < genomeKeys.length; i++ )
        {
            builder.append( " " );
            builder.append( genomeKeys[i] );
            builder.append( "=[" );
            builder.append( genomeProps.getProperty( genomeKeys[i] ) );
            builder.append( "]" );
        }
//        StringWriter writer = new StringWriter();
//        try
//        {
//            genomeProps.store( writer, "" );
//            builder.append( writer.getBuffer() );
//        }
//        catch( IOException ioe )
//        {
//            _LOG.error( "Unable to convert decoded genome to string", ioe );
//        }

        builder.append( System.getProperty( "line.separator" ) );

        return builder.toString();
    }

    public Properties getGenomeProperties( Individual ind )
    {
        // Cast it to the proper type
        BitVectorIndividual bitInd = (BitVectorIndividual) ind;

        // Decode the genome
        Properties genomeProps = decodeGenome( bitInd.genome );

        return genomeProps;
    }
    
//    /**
//     * TODO Method description
//     *
//     * @param ind
//     * @param state
//     * @param subpopulation
//     * @param threadnum
//     * @param log
//     * @param verbosity
//     * @see ec.simple.SimpleProblemForm#describe(ec.Individual, ec.EvolutionState, int, int, int, int)
//     */
//    @Override
//    public void describe( Individual ind,
//            EvolutionState state,
//            int subpopulation,
//            int threadnum,
//            int log,
//            int verbosity )
//    {
//        // TODO Auto-generated method stub
//        _LOG.warn( "SimpleProblemForm.describe was called" );
//    }

    /**
     * Decode the genome
     *
     * @param genome
     */
    protected Properties decodeGenome( boolean[] genome )
    {
        // Build the properties object
        Properties props = new Properties();
        
        int codonIdx = 0;

        /* Decode each codon in the genome, starting with the base
         * initiation rate */
        float initiateBaseRate = decodeFloatCodon( genome,
                _initiateBaseRateCodonSize,
                _initiateBaseRateScaling,
                codonIdx );
        codonIdx += _initiateBaseRateCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._INITIATE_RATE_KEY,
                Float.toString( initiateBaseRate ) );

        // Initiation K exponent multiplier
        float initiateKExpMult = decodeFloatCodon( genome,
                _initiateKExpMultCodonSize,
                _initiateKExpMultScaling,
                codonIdx )
                + _minKExpMultValue;
        codonIdx += _initiateKExpMultCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._INITIATE_K_EXP_MULT_KEY,
                Float.toString( initiateKExpMult ) );

        // Initiation K exponent offset
        float initiateKExpOffset = decodeFloatCodon( genome,
                _initiateKExpOffsetCodonSize,
                _initiateKExpOffsetScaling,
                codonIdx )
                + _minKExpOffsetValue;
        codonIdx += _initiateKExpOffsetCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._INITIATE_K_EXP_OFFSET_KEY,
                Float.toString( initiateKExpOffset ) );

        // Initiation MRV length sigma
        float initiateMRVLenSigma = decodeFloatCodon( genome,
                _initiateMRVLenSigmaCodonSize,
                _initiateMRVLenSigmaScaling,
                codonIdx )
                + _minSigmaValue;
        codonIdx += _initiateMRVLenSigmaCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._INITIATE_MRV_SIGMA_KEY,
                Float.toString( initiateMRVLenSigma ) );
        boolean initiateMRVFlag = genome[ codonIdx++ ];
        props.setProperty( DefaultProbabilityDecisionCalculator._INITIATE_MRV_FLAG_KEY,
                Boolean.toString( initiateMRVFlag ) );

        // Initiation patch value sigma
        float initiatePatchValueSigma = decodeFloatCodon( genome,
                _initiatePatchValueSigmaCodonSize,
                _initiatePatchValueSigmaScaling,
                codonIdx )
                + _minSigmaValue;
        codonIdx += _initiatePatchValueSigmaCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._INITIATE_PATCH_VALUE_SIGMA_KEY,
                Float.toString( initiatePatchValueSigma ) );
        boolean initiatePatchValueFlag = genome[ codonIdx++ ];
        props.setProperty( DefaultProbabilityDecisionCalculator._INITIATE_PATCH_VALUE_FLAG_KEY,
                Boolean.toString( initiatePatchValueFlag ) );

        // Initiation direction difference sigma
        float initiateDirDiffSigma = decodeFloatCodon( genome,
                _initiateDirDiffSigmaCodonSize,
                _initiateDirDiffSigmaScaling,
                codonIdx )
                + _minSigmaValue;
        codonIdx += _initiateDirDiffSigmaCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._INITIATE_DIR_DIFF_SIGMA_KEY,
                Float.toString( initiateDirDiffSigma ) );
        boolean initiateDirDiffFlag = genome[ codonIdx++ ];
        props.setProperty( DefaultProbabilityDecisionCalculator._INITIATE_DIR_DIFF_FLAG_KEY,
                Boolean.toString( initiateDirDiffFlag ) );

        // Follow alpha
        float followAlpha = decodeFloatCodon( genome,
                _followAlphaCodonSize,
                _followAlphaScaling,
                codonIdx );
        codonIdx += _followAlphaCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._FOLLOW_ALPHA_KEY,
                Float.toString( followAlpha ) );

        // Follow beta
        float followBeta = decodeFloatCodon( genome,
                _followBetaCodonSize,
                _followBetaScaling,
                codonIdx );
        codonIdx += _followBetaCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._FOLLOW_BETA_KEY,
                Float.toString( followBeta ) );

        // Follow k exponent multiplier
        float followkExpMult = decodeFloatCodon( genome,
                _followKExpMultCodonSize,
                _followKExpMultScaling,
                codonIdx )
                + _minKExpMultValue;
        codonIdx += _followKExpMultCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._FOLLOW_K_EXP_MULT_KEY,
                Float.toString( followkExpMult ) );

        // Follow k exponent offset
        float followKExpOffset = decodeFloatCodon( genome,
                _followKExpOffsetCodonSize,
                _followKExpOffsetScaling,
                codonIdx )
                + _minKExpOffsetValue;
        codonIdx += _followKExpOffsetCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._FOLLOW_K_EXP_OFFSET_KEY,
                Float.toString( followKExpOffset ) );

//        // Follow MRV difference sigma
//        float followMRVDiffSigma = decodeCodon( genome,
//                _followMRVDiffSigmaCodonSize,
//                _followMRVDiffSigmaScaling,
//                codonIdx )
//                + _minSigmaValue;
//        codonIdx += _followMRVDiffSigmaCodonSize;
//        props.setProperty( DefaultProbabilityDecisionCalculator._FOLLOW_MRV_DIFF_SIGMA_KEY,
//                Float.toString( followMRVDiffSigma ) );
//
        // Follow mean position relative distance sigma
        float followMeanPosRelDistSigma = decodeFloatCodon( genome,
                _followMeanPosRelDistSigmaCodonSize,
                _followMeanPosRelDistSigmaScaling,
                codonIdx )
                + _minSigmaValue;
        codonIdx += _followMeanPosRelDistSigmaCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._FOLLOW_MEAN_POS_REL_DIST_SIGMA_KEY,
                Float.toString( followMeanPosRelDistSigma ) );
        boolean followMeanPosRelDistFlag = genome[ codonIdx++ ];
        props.setProperty( DefaultProbabilityDecisionCalculator._FOLLOW_MEAN_POS_REL_DIST_FLAG_KEY,
                Boolean.toString( followMeanPosRelDistFlag ) );

        // Follow MRV direction difference sigma
        float followMRVDirDiffSigma = decodeFloatCodon( genome,
                _followMRVDirDiffSigmaCodonSize,
                _followMRVDirDiffSigmaScaling,
                codonIdx )
                + _minSigmaValue;
        codonIdx += _followMRVDirDiffSigmaCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._FOLLOW_MRV_DIR_DIFF_SIGMA_KEY,
                Float.toString( followMRVDirDiffSigma ) );
        boolean followMRVDirDiffFlag = genome[ codonIdx++ ];
        props.setProperty( DefaultProbabilityDecisionCalculator._FOLLOW_MRV_DIR_DIFF_FLAG_KEY,
                Boolean.toString( followMRVDirDiffFlag ) );

        // Follow MRV magnitude difference sigma
        float followMRVMagDiffSigma = decodeFloatCodon( genome,
                _followMRVMagDiffSigmaCodonSize,
                _followMRVMagDiffSigmaScaling,
                codonIdx )
                + _minSigmaValue;
        codonIdx += _followMRVMagDiffSigmaCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._FOLLOW_MRV_MAG_DIFF_SIGMA_KEY,
                Float.toString( followMRVMagDiffSigma ) );
        boolean followMRVMagDiffFlag = genome[ codonIdx++ ];
        props.setProperty( DefaultProbabilityDecisionCalculator._FOLLOW_MRV_MAG_DIFF_FLAG_KEY,
                Boolean.toString( followMRVMagDiffFlag ) );

        // Forage base rate
        float forageBaseRate = decodeFloatCodon( genome,
                _forageBaseRateCodonSize,
                _forageBaseRateScaling,
                codonIdx );
        codonIdx += _forageBaseRateCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._FORAGE_RATE_KEY,
                Float.toString( forageBaseRate ) );

        // Forage k exponent multiplier
        float forageKExpMult = decodeFloatCodon( genome,
                _forageKExpMultCodonSize,
                _forageKExpMultScaling,
                codonIdx )
                + _minKExpMultValue;
        codonIdx += _forageKExpMultCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._FORAGE_K_EXP_MULT_KEY,
                Float.toString( forageKExpMult ) );

        // Forage k exponent offset
        float forageKExpOffset = decodeFloatCodon( genome,
                _forageKExpOffsetCodonSize,
                _forageKExpOffsetScaling,
                codonIdx )
                + _minKExpOffsetValue;
        codonIdx += _forageKExpOffsetCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._FORAGE_K_EXP_OFFSET_KEY,
                Float.toString( forageKExpOffset ) );

        // Forage patch value sigma
        float foragePatchValueSigma = decodeFloatCodon( genome,
                _foragePatchValueSigmaCodonSize,
                _foragePatchValueSigmaScaling,
                codonIdx )
                + _minSigmaValue;
        codonIdx += _foragePatchValueSigmaCodonSize;
        props.setProperty( DefaultProbabilityDecisionCalculator._FORAGE_PATCH_VALUE_SIGMA_KEY,
                Float.toString( foragePatchValueSigma ) );
        boolean foragePatchValueFlag = genome[ codonIdx++ ];
        props.setProperty( DefaultProbabilityDecisionCalculator._FORAGE_PATCH_VALUE_FLAG_KEY,
                Boolean.toString( foragePatchValueFlag ) );

//        // 
//        float  = decodeCodon( genome,
//                ,
//                ,
//                codonIdx );
//        codonIdx += ;
//        props.setProperty( DefaultProbabilityDecisionCalculator.,
//                Float.toString(  ) );


//        // Build the destinations
//        DestinationRunCounts[] destinationInfo =
//                new DestinationRunCounts[ _destinationFiles.length ];
//        for(int i = 0; i < destinationInfo.length; i++ )
//        {
//            long seed = 0;
//            if( null != random )
//            {
//                seed = random.nextInt();
//            }
//            destinationInfo[i] = new DestinationRunCounts(
//                    _destinationFiles[i],
//                    _destinationSimCounts[i],
//                    seed );
//
//        }

        // Store the values
//        EvolutionInputParameters inputParameters = new EvolutionInputParameters(
//                alpha,
//                beta,
//                s,
//                q,
//                alphaC,
//                betaC,
//                destinationInfo );
//        EvolutionInputParameters inputParameters = new EvolutionInputParameters(
//                0.006161429f,
//                0.013422819f,
//                4,
//                1,
//                0.009f,
//                -0.009f,
//                destinationInfo );

        // Log it
//        if( _LOG.isDebugEnabled() )
//        {
//            StringWriter writer = new StringWriter();
//            try
//            {
//                props.store( writer, "" );
//                _LOG.debug( "Decoded genome: " + writer.getBuffer() );
//            }
//            catch( IOException ioe )
//            {
//                _LOG.error( "Unable to display decoded genome", ioe );
//            }
//        }

        // Return them
        return props;
    }

    protected int loadIntParameter( String key,
            String description,
            EvolutionState state,
            Parameter base )
    {
        Validate.isTrue( state.parameters.exists(
                base.push( key ), null ),
                "Required parameter missing [" + description + "]" );
        int intValue = state.parameters.getInt( base.push(
                 key ),
                 null );
        _LOG.info( "Using "
                + description
                + " ["
                + _initiateBaseRateCodonSize
                + "]" );

        return intValue;
    }
    
    protected float loadFloatParameter( String key,
            String description,
            EvolutionState state,
            Parameter base )
    {
        Validate.isTrue( state.parameters.exists(
                base.push( key ), null ),
                "Required parameter missing [" + description + "]" );
        float floatValue = state.parameters.getFloat( base.push(
                 key ),
                 null );
        _LOG.info( "Using "
                + description
                + " ["
                + _initiateBaseRateCodonSize
                + "]" );

        return floatValue;
    }

    protected float decodeFloatCodon( boolean[] genome,
            int codonSize,
            float scaling,
            int startIdx )
    {
        int rawValue = decodeAndConvert( genome,
                startIdx,
                codonSize );
        float maxValue = (float) Math.pow( 2, codonSize );
        float value = (rawValue / maxValue )
                * scaling;

        return value;
    }
    
    /**
     * Decode a codon to a gray code and convert it to binary
     *
     * @param genome The genome containing the codon
     * @param startIdx The starting index of the codon
     * @param codongSize The size of the codon
     * @return The decoded value
     */
    protected int decodeAndConvert( boolean[] genome, int startIdx, int codonSize )
    {
        // Decode it into the gray code
        int grayCode = MiscUtils.decodeBitArray( genome, startIdx, codonSize );

        // Convert it to binary
        int binary = MiscUtils.convertGrayCodeToBinary( grayCode );

        return binary;
    }

}
