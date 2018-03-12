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
package edu.snu.csne.util;

// Imports
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.math.Vector3f;

import ec.util.MersenneTwisterFast;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.mates.math.NavigationalVector;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class PatchRingBuilder
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            PatchRingBuilder.class.getName() );
    
    /** Simple file spacer comment */
    private static final String _SPACER =
            "# =========================================================";

    /** Delta to apply to separations to prevent rounding errors */
    private static final float _DELTA = 0.01f;

    /** The random number generator */
    private MersenneTwisterFast _rng = null;

    /** The random seed */
    private long _randomSeed = 0;

    /** The minimum number of patches */
    private int _minPatchCount = 0;
    
    /** The maximum number of patches */
    private int _maxPatchCount = 0;
    
    /** The minimum distance between patches */
    private float _minPatchSeparation = 0.0f;
    
    /** The minimum distance from the origin of patches */
    private float _minPatchDistance = 0.0f;
    
    /** The maximum distance from the origin of patches */
    private float _maxPatchDistance = 0.0f;

    /** The minimum amount of resources in a patch */
    private float _minPatchResources = 0.0f;
    
    /** The maximum amount of resources in a patch */
    private float _maxPatchResources = 0.0f;
    
    /** The minimum patch radius */
    private float _minPatchRadius = 0.0f;

    /** The maximum patch radius */
    private float _maxPatchRadius = 0.0f;

    /** The minimum predation probability of a patch */
    private float _minPredationProbability = 0.0f;
    
    /** The maximum predation probability of a patch */
    private float _maxPredationProbability = 0.0f;
    
    /** The minimum number of foragers for patches */
    private int _minForagerCount = 0;
    
    /** The maximum number of foragers for a patch */
    private int _maxForagerCount = 0;
    
    
    public PatchRingBuilder( int minPatchCount,
            int maxPatchCount,
            float minPatchSeparation,
            float minPatchDistance,
            float maxPatchDistance,
            float minPatchResources,
            float maxPatchResources,
            float minPatchRadius,
            float maxPatchRadius,
            float minPredationProbability,
            float maxPredationProbability,
            int minForagerCount,
            int maxForagerCount,
            long randomSeed )
    {
        _minPatchCount = minPatchCount;
        _maxPatchCount = maxPatchCount;
        _minPatchSeparation = minPatchSeparation;
        _minPatchDistance = minPatchDistance;
        _maxPatchDistance = maxPatchDistance;
        _minPatchResources = minPatchResources;
        _maxPatchResources = maxPatchResources;
        _minPatchRadius = minPatchRadius;
        _maxPatchRadius = maxPatchRadius;
        _minPredationProbability = minPredationProbability;
        _maxPredationProbability = maxPredationProbability;
        _minForagerCount = minForagerCount;
        _maxForagerCount = maxForagerCount;
        reseedRNG( randomSeed );
    }
    
    public void reseedRNG( long randomSeed )
    {
        _randomSeed = randomSeed;
        _rng = new MersenneTwisterFast( randomSeed );
    }
    
    public Patch[] buildPatches()
    {
        // How many do we build?
        int patchCount = _rng.nextInt( _maxPatchCount - _minPatchCount + 1 )
                + _minPatchCount;
        Patch[] patches = new Patch[ patchCount ];
        
        // Create some handy variables
        float patchDistanceDiff = _maxPatchDistance - _minPatchDistance;
        float patchRadiusDiff = _maxPatchRadius - _minPatchRadius;
        float patchResourcesDiff = _maxPatchResources - _minPatchResources;
        float predationProbabilityDiff = _maxPredationProbability - _minPredationProbability;
        int foragerCountDiff = _maxForagerCount - _minForagerCount;
        
        for( int i = 0; i < patchCount; i++ )
        {
            // Loop until the patch is valid
            boolean valid = false;
            do
            {
                // Get a random angle
                float angle = _rng.nextFloat() * 2.0f * (float) Math.PI;
                
                // Get a random distance
                float distance = _rng.nextFloat() * patchDistanceDiff
                        + _minPatchDistance;
                
                // Get a random radius
                float radius = _rng.nextInt( (int) Math.ceil( patchRadiusDiff ) )
                        + _minPatchRadius;
                
                // Create the position
                NavigationalVector navPosition = new NavigationalVector( distance, angle, 0 );
                Vector3f position = navPosition.toVector3f(); 

                // Get a random resource amount
                float resources = _rng.nextFloat() * patchResourcesDiff
                        + _minPatchResources;
                
                // Get a random predation probability
                float predationProb = _rng.nextFloat() * predationProbabilityDiff
                        + _minPredationProbability;
                
                // Get a random agent forage count
                int minAgentForageCount = _rng.nextInt( foragerCountDiff + 1 )
                        + _minForagerCount;
                
                // Build the patch
                patches[i] = new Patch( "id",
                        position,
                        radius,
                        resources,
                        predationProb,
                        minAgentForageCount );
                
                // See if it is valid
                valid = isValidPatch( patches[i], patches, i );

            } while( !valid );
        }
        
        return patches;
    }

    public void sentToFile( String filename, Patch[] patches )
    {
        // Create the writer
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter( new BufferedWriter(
                    new FileWriter( filename ) ) );
        }
        catch( IOException ioe )
        {
            _LOG.error( "Unable to open file ["
                    + filename
                    + "]", ioe );
            throw new RuntimeException( "Unable to open file ["
                    + filename
                    + "]", ioe );
        }

        writer.println( _SPACER );
        
        Date today = Calendar.getInstance().getTime();
        writer.println( "# Built on " + DateFormatUtils.format( today,
                "yyyy-MM-dd HH:mm:SS") );
        writer.println( "# minPatchCount=["
                + _minPatchCount
                + "]" );
        writer.println( "# maxPatchCount=["
                + _maxPatchCount
                + "]" );
        writer.println( "# minPatchSeparation=["
                + _minPatchSeparation
                + "]" );
        writer.println( "# minPatchDistance=["
                + _minPatchDistance
                + "]" );
        writer.println( "# maxPatchDistance=["
                + _maxPatchDistance
                + "]" );
        writer.println( "# minPatchResources=["
                + _minPatchResources
                + "]" );
        writer.println( "# maxPatchResources=["
                + _maxPatchResources
                + "]" );
        writer.println( "# minPatchRadius=["
                + _minPatchRadius
                + "]" );
        writer.println( "# maxPatchRadius=["
                + _maxPatchRadius
                + "]" );
        writer.println( "# minPredationProbability=["
                + _minPredationProbability
                + "]" );
        writer.println( "# maxPredationProbability=["
                + _maxPredationProbability
                + "]" );
        writer.println( "# minForagerCount=["
                + _minForagerCount
                + "]" );
        writer.println( "# maxForagerCount=["
                + _maxForagerCount
                + "]" );
        writer.println( "# randomSeed=["
                + _randomSeed
                + "]" );

        writer.println( _SPACER );
        writer.println();
        
        writer.println( "patch-count = " + patches.length );
        writer.println( "" );

        for( int i = 0; i < patches.length; i++ )
        {
            String prefix = String.format( "patch.%02d.", i );
            Vector3f position = patches[i].getPosition();
            writer.println( prefix
                    + "position = "
                    + String.format( "%06.2f", position.x )
                    + ","
                    + String.format( "%06.2f", position.y )
                    + ",0.0");
            writer.println( prefix
                    + "resources = "
                    + String.format( "%03.2f", patches[i].getRemainingResources() ) );
            writer.println( prefix
                    + "radius = "
                    + (int) patches[i].getRadius() );
            writer.println( prefix
                    + "predation-probability = "
                    + String.format( "%09.6f", patches[i].getPredationProbability() ) );
            writer.println( prefix
                    + "min-agent-forage-count = "
                    + patches[i].getMinAgentForageCount());
            writer.println( "" );
        }
        
        // Close the writer
        writer.close();
    }
    
    private boolean isValidPatch( Patch patch,
            Patch[] patches,
            int maxIndex )
    {
        boolean valid = true;
        for( int i = 0; (i < maxIndex) && valid; i++ )
        {
            float radiiSum = patch.getRadius() + patches[i].getRadius();
            float distance = patch.getPosition().distance( patches[i].getPosition() );
            if( _minPatchSeparation > (distance - radiiSum) )
            {
                valid = false;
            }
        }

        return valid;
    }
    
    public static void main( String[] args )
    {
        // Get the arguments
        String filePrefix = args[0];
        int fileCount = Integer.parseInt( args[1] );
        int minPatchCount = Integer.parseInt( args[2] );
        int maxPatchCount = Integer.parseInt( args[3] );
        float minPatchSeparation = Float.parseFloat( args[4] );
        float minPatchDistance = Float.parseFloat( args[5] );
        float maxPatchDistance = Float.parseFloat( args[6] );
        float minPatchResources = Float.parseFloat( args[7] );
        float maxPatchResources = Float.parseFloat( args[8] );
        float minPatchRadius = Float.parseFloat( args[9] );
        float maxPatchRadius = Float.parseFloat( args[10] );
        float minPredationProbability = Float.parseFloat( args[11] );
        float maxPredationProbability = Float.parseFloat( args[12] );
        int minForagerCount = Integer.parseInt( args[13] );
        int maxForagerCount = Integer.parseInt( args[14] );
        
        long randomSeed = 0;
        
        PatchRingBuilder builder = new PatchRingBuilder(
                minPatchCount,
                maxPatchCount,
                minPatchSeparation,
                minPatchDistance,
                maxPatchDistance,
                minPatchResources,
                maxPatchResources,
                minPatchRadius,
                maxPatchRadius,
                minPredationProbability,
                maxPredationProbability,
                minForagerCount,
                maxForagerCount,
                randomSeed );
        
        // Build the specified number of files
        for( int i = 0; i < fileCount; i++, randomSeed++ )
        {
            // Reseed the RNG
            builder.reseedRNG( randomSeed );

            // Build the output file name
            String fileName = filePrefix
                    + String.format( "%03d", randomSeed )
                    + ".parameters";

            // Build the patches
            Patch[] patches = builder.buildPatches();
            
            // Send them to a file
            builder.sentToFile( fileName, patches );
        }
    }
}
