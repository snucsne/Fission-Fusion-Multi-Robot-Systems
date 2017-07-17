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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jme3.math.Vector3f;
import ec.util.MersenneTwisterFast;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class AgentPositionGenerator
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            AgentPositionGenerator.class.getName() );
    
    /** Simple file spacer comment */
    private static final String _SPACER =
            "# =========================================================";

    
    /** The number of positions to build */
    private int _positionCount = 0;

    /** The min neighbor separation */
    private float _minNeighborSeparation = 0.0f;
    
    /** The min neighbor separation squared */
    private float _minNeighborSeparationSquared = 0.0f;

    /** The maximum radius of positions to generate */
    private float _maxPositionRadius = 0.0f;
    
    /** The maximum velocity */
    private float _maxVelocity = 0.0f;
    
    /** The random seed */
    private long _randomSeed = 0;

    /** The random number generator */
    private MersenneTwisterFast _rng = null;

    
    
    public AgentPositionGenerator( int positionCount,
            float minNeighborSeparation,
            float maxPositionRadius,
            float maxVelocity,
            long randomSeed )
    {
        _positionCount = positionCount;
        _minNeighborSeparation = minNeighborSeparation;
        _minNeighborSeparationSquared = minNeighborSeparation
                * minNeighborSeparation;
        _maxPositionRadius = maxPositionRadius;
        _maxVelocity = maxVelocity;
        _randomSeed = randomSeed;
        _rng = new MersenneTwisterFast( randomSeed );
        
        _LOG.debug( "_positionCount=[" + _positionCount + "]" );
        _LOG.debug( "_minNeighborSeparation=[" + _minNeighborSeparation + "]" );
        _LOG.debug( "_maxPositionRadius=[" + _maxPositionRadius + "]" );
        _LOG.debug( "_maxVelocity=[" + _maxVelocity + "]" );
        _LOG.debug( "_randomSeed=[" + _randomSeed + "]" );
    }
    
    
    public List<Vector3f> buildPositions()
    {
        _LOG.trace( "Entering buildPositions()" );

        List<Vector3f> positions = new LinkedList<Vector3f>();
        
        // Default the first position to the origin
        positions.add( new Vector3f( 0.0f, 0.0f, 0.0f ) );
        
        while( positions.size() < _positionCount )
        {
            // Generate a random point in a circle
            float angle = (float) (_rng.nextFloat() * Math.PI * 2.0f);
            float radius = (float) Math.sqrt( _rng.nextFloat() )
                    * _maxPositionRadius;
//            _LOG.debug( "angle=[" + angle + "]  radius=[" + radius + "]" ); 
            
            float x = radius * (float) Math.cos( angle );
            float y = radius * (float) Math.sin( angle );
            Vector3f newPosition = new Vector3f( x, y, 0.0f );
            
            // Check to ensure it isn't too close to the other positions
            if( isValid( newPosition, positions ) )
            {
                positions.add( newPosition );
                _LOG.info( "Added position #"
                        + positions.size() );
                      
            }
//            else
//            {
//                _LOG.debug( "Invalid position ["
//                        + newPosition
//                        + "]" );
//            }
        }
        
        _LOG.trace( "Leaving buildPositions()" );
        
        return positions;
    }
    
    public List<Vector3f> buildVelocities()
    {
        List<Vector3f> velocities = new LinkedList<Vector3f>();
        while( velocities.size() < _positionCount )
        {
            // Generate a random point in a circle
            float angle = (float) (_rng.nextFloat() * Math.PI * 2.0f);
            float radius = (float) Math.sqrt( _rng.nextFloat() )
                    * _maxVelocity;
            float x = radius * (float) Math.cos( angle );
            float y = radius * (float) Math.sin( angle );
            Vector3f newVelocity = new Vector3f( x, y, 0.0f );
            velocities.add( newVelocity );
        }
        
        return velocities;
    }
    
    public void writeToFile( List<Vector3f> positions,
            List<Vector3f> velocities,
            String filename )
    {
        _LOG.trace( "Entering writeToFile( positions, filename )" );

        _LOG.debug( "Writing to file [" + filename + "]" );
        
        // Create the writer
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter( new BufferedWriter(
                    new FileWriter( filename ) ) );
        }
        catch( IOException ioe )
        {
            _LOG.error( "Unable to open simple file ["
                    + filename
                    + "]", ioe );
            throw new RuntimeException( "Unable to open simple file ["
                    + filename
                    + "]", ioe );
        }

        writer.println( _SPACER );
        Date today = new Date();
        writer.println( "# generated on: "
                + today.toString() );
        writer.println( "# position count ["
                + _positionCount
                + "]" );
        writer.println( "# minimum neighbor separation ["
                + _minNeighborSeparation
                + "]" );
        writer.println( "# maximum position radius ["
                + _maxPositionRadius
                + "]" );
        writer.println( "# maximum velocity ["
                + _maxVelocity
                + "]" );
        writer.println( "# random seed ["
                + _randomSeed
                + "]" );
        writer.println( _SPACER );
        writer.println();

        writer.println( "agent-count = "
                + positions.size() );
        
        // Create a initial team ID
        String initialTeamID = "Team000";
        
        for( int i = 0; i < positions.size(); i++ )
        {
            Vector3f position = positions.get( i );
            Vector3f velocity = velocities.get( i );
            writer.println( "agent."
                    + String.format( "%03d", i )
                    + ".position = "
                    + String.format( "%+07.3f", position.x )
                    + ","
                    + String.format( "%+07.3f", position.y )
                    + ","
                    + String.format( "%+07.3f", position.z ) );
            writer.println( "agent."
                    + String.format( "%03d", i )
                    + ".velocity = "
                    + String.format( "%+07.3f", velocity.x )
                    + ","
                    + String.format( "%+07.3f", velocity.y )
                    + ","
                    + String.format( "%+07.3f", velocity.z ) );
            writer.println( "agent."
                    + String.format( "%03d", i )
                    + ".team = "
                    + initialTeamID );
        }
        
        // Close the writer
        writer.close();
        
        _LOG.trace( "Leaving writeToFile( positions, filename )" );
    }
    
    private boolean isValid( Vector3f position, List<Vector3f> positions )
    {
        // Walk through all the current positions
        boolean valid = true;
        Iterator<Vector3f> positionIter = positions.iterator();
        while( valid && positionIter.hasNext() )
        {
            // Is it too close to this one?
            Vector3f current = positionIter.next();
            if( _minNeighborSeparationSquared > position.distanceSquared( current ) )
            {
                // Yup
                valid = false;
            }
        }
        
        return valid;
    }
    
    public static void main( String[] args )
    {
      // Get the number of positions to build
      int positionCount = Integer.parseInt( args[0] );

      // Get the minimum neighbor distance
      float minNeighborDistance = Float.parseFloat( args[1] );

      // Get the maximum radius of positions to generate
      float maxPositionRadius = Float.parseFloat( args[2] );
      
      // Get the maximum velocity
      float maxVelocity = Float.parseFloat( args[3] );

      // Get the random seed
      long randomSeed = Long.parseLong( args[4] );

      // Get the filename
      String filename = args[5];
      
      // Build the generator
      AgentPositionGenerator generator = new AgentPositionGenerator(
              positionCount,
              minNeighborDistance,
              maxPositionRadius,
              maxVelocity,
              randomSeed );
      List<Vector3f> positions = generator.buildPositions();
      List<Vector3f> velocities = generator.buildVelocities();
      generator.writeToFile( positions, velocities, filename );
    }
}
