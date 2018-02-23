package edu.snu.csne.forage;


public abstract AbstractForageProblem extends Problem
        implements SimpleProblemForm, IndividualDescriber
{
    /** Default serial version UID */
    private static final long serialVersionUID = 1L;


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

        // Cast it to the proper type
        BitVectorIndividual bitInd = (BitVectorIndividual) ind;

        // Decode the genome
        EvolutionInputParameters inputParameters = decodeGenome( bitInd.genome,
                state.random[threadnum] );
    }

    /**
     * TODO Method description
     *
     * @param ind
     * @param prefix
     * @param statDir
     * @return
     * @see edu.snu.leader.util.IndividualDescriber#describe(ec.Individual, java.lang.String, java.lang.String)
     */
    @Override
    public String describe( Individual ind, String prefix, String statDir )
    {
        // Cast it to the proper type
        BitVectorIndividual bitInd = (BitVectorIndividual) ind;

        // Decode the genome
        EvolutionInputParameters inputParameters = decodeGenome( bitInd.genome, null );

        StringBuilder builder = new StringBuilder();
        builder.append( prefix );
        builder.append( "decoded-parameters = " );

        // Describe the parameters
        builder.append( "alpha=[" );
        builder.append( String.format( "%08.6f", inputParameters.getAlpha() ) );
        builder.append( "] beta=[" );
        builder.append( String.format( "%08.6f", inputParameters.getBeta() ) );
        builder.append( "] S=[" );
        builder.append( String.format( "%2d", inputParameters.getS() ) );
        builder.append( "] q=[" );
        builder.append( String.format( "%08.6f", inputParameters.getQ() ) );
        builder.append( "] alphaC=[" );
        builder.append( String.format( "%08.6f", inputParameters.getAlphaC() ) );
        builder.append( "] betaC=[" );
        builder.append( String.format( "%+09.6f", inputParameters.getBetaC() ) );
        builder.append( "]" );
        builder.append( System.getProperty( "line.separator" ) );

        return builder.toString();
    }

    /**
     * Decode the genome
     *
     * @param genome
     */
    protected EvolutionInputParameters decodeGenome( boolean[] genome,
            MersenneTwisterFast random )
    {
        int codonIdx = 0;

        // Decode each codon in the genome, starting with alpha
        float maxValue = (float) Math.pow( 2.0, _alphaCodonSize );
        int rawAlpha = decodeAndConvert( genome, 0, _alphaCodonSize );
        float normalizedAlpha = ( rawAlpha / maxValue );
        float alpha = normalizedAlpha * _alphaScalingFactor;
        codonIdx += _alphaCodonSize;

        maxValue = (float) Math.pow( 2.0, _betaCodonSize );
        int rawBeta = decodeAndConvert( genome, codonIdx, _betaCodonSize );
        float normalizedBeta = rawBeta / maxValue;
        float beta = normalizedBeta * _betaScalingFactor;
        codonIdx += _betaCodonSize;

        int rawS = decodeAndConvert( genome, codonIdx, _sCodonSize );
        int s = rawS % _sModulus;
        codonIdx += _sCodonSize;

        maxValue = (float) Math.pow( 2.0, _qCodonSize );
        int rawQ = decodeAndConvert( genome, codonIdx, _qCodonSize );
        float normalizedQ = rawQ / maxValue;
        float q = normalizedQ * _qScalingFactor;
        codonIdx += _qCodonSize;

        maxValue = (float) Math.pow( 2.0, _alphaCCodonSize );
        int rawAlphaC = decodeAndConvert( genome, codonIdx, _alphaCCodonSize );
        float normalizedAlphaC = rawAlphaC / maxValue;
        float alphaC = normalizedAlphaC * _alphaCScalingFactor;
        codonIdx += _alphaCCodonSize;

        maxValue = (float) Math.pow( 2.0, _betaCCodonSize );
        int rawBetaC = decodeAndConvert( genome, codonIdx, _betaCCodonSize );
        float normalizedBetaC = rawBetaC / maxValue;
        float betaC = normalizedBetaC * _betaCScalingFactor;

        // Build the destinations
        DestinationRunCounts[] destinationInfo =
                new DestinationRunCounts[ _destinationFiles.length ];
        for(int i = 0; i < destinationInfo.length; i++ )
        {
            long seed = 0;
            if( null != random )
            {
                seed = random.nextInt();
            }
            destinationInfo[i] = new DestinationRunCounts(
                    _destinationFiles[i],
                    _destinationSimCounts[i],
                    seed );

        }

        // Store the values
        EvolutionInputParameters inputParameters = new EvolutionInputParameters(
                alpha,
                beta,
                s,
                q,
                alphaC,
                betaC,
                destinationInfo );
//        EvolutionInputParameters inputParameters = new EvolutionInputParameters(
//                0.006161429f,
//                0.013422819f,
//                4,
//                1,
//                0.009f,
//                -0.009f,
//                destinationInfo );

        // Log it
        _LOG.debug( inputParameters.toString() );

        // Return them
        return inputParameters;
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
