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
package edu.snu.csne.mates.math;

//Imports
import org.apache.commons.lang3.Validate;
import com.jme3.math.Vector3f;


/**
 * A vector in spherical coordinates
 *
 * @author Brent Eskridge
 */
public class SphericalVector
{
    /** 2PI */
    public static final float TWO_PI = 2.0f * (float) Math.PI;
    
    /** The radius of the vector */
    public float r = 0.0f;
    
    /** The azimuth angle in the x-y plane in radians */
    public float theta = 0.0f;
    
    /** The polar angle in the z axis in radians */
    public float phi = 0.0f;
    
    /**
     * Builds this spherical vector.
     * 
     * @param r The radius
     * @param theta The azimuth angle i radians
     * @param phi The polar angle in radians
     */
    public SphericalVector( float r, float theta, float phi )
    {
        // Validate and save the parameters
        Validate.isTrue( (0.0f <= r), "Radius r may not be negative ["
                + r
                + "]" );
        this.r = r;
        this.theta = theta;
        this.phi = phi;
    }
    
    /**
     * Builds this spherical vector from a cartesian x,y,z vector.
     * 
     * @param vector The cartesian vector
     */
    public SphericalVector( Vector3f vector )
    {
        // Verify the vector
        Validate.notNull( vector, "Cartesian vector may not be null" );
        
        // Is it a zero vector?
        if( Vector3f.ZERO.equals( vector ) )
        {
            // Yup
            r = 0.0f;
            theta = 0.0f;
            phi = 0.0f;
        }
        else
        {
            // Nope
            float xSqrd = vector.x * vector.x;
            float ySqrd = vector.y * vector.y;
            float zSqrd = vector.z * vector.z;
            
            // Calculate the radius
            r = (float) Math.sqrt( xSqrd + ySqrd + zSqrd );
            
            // Calculate theta, but make sure the denominator isn't 0
            float s = (float) Math.sqrt( xSqrd + ySqrd );
            theta = (float) Math.asin( vector.y / s );
            if( 0.0f > vector.x )
            {
                theta = (float) Math.PI - theta;
            }
            while( theta < 0.0f )
            {
                theta += TWO_PI;
            }
            while( theta > TWO_PI )
            {
                theta -= TWO_PI;
            }
            
            // Calculate phi, but make sure the denominator isn't 0
            if( 0.0f != r )
            {
                phi = (float) Math.acos( vector.z / r );
            }
            else
            {
                phi = 0.0f;
            }
            while( phi < 0.0f )
            {
                phi += (float) Math.PI;
            }
            while( phi > (float) Math.PI )
            {
                phi -= (float) Math.PI;
            }
        }
    }
    
    /**
     * Normalizes this vector
     */
    public void normalize()
    {
        // Just set the radius to 1
        r = 1.0f;
    }
    
    /**
     * Returns the length of this vector
     */
    public float length()
    {
        return r;
    }
    
    /**
     * Converts this spherical vector to a cartesian vector.
     * 
     * @return The cartesian vector
     */
    public Vector3f toVector3f()
    {
        float x = r * (float) Math.sin( phi ) * (float) Math.cos( theta );
        float y = r * (float) Math.sin( phi ) * (float) Math.sin( theta );
        float z = r * (float) Math.cos( phi );
        
        return new Vector3f( x, y, z );
    }
    
    /**
     * Returns the string representation of this vector.
     * 
     * @return The string representation
     */
    public String toString()
    {
        StringBuilder builder = new StringBuilder( "SphericalVector{r=[");
        builder.append( r );
        builder.append( "],theta=[" );
        builder.append( theta );
        builder.append( "],phi=[" );
        builder.append( phi );
        builder.append( "]}" );
        
        return builder.toString();
    }

    /**
     * Returns the string representation of this vector in degrees.
     * 
     * @return The string representation
     */
    public String toStringDegrees()
    {
        StringBuilder builder = new StringBuilder( "SphericalVector{r=[");
        builder.append( r );
        builder.append( "],theta=[" );
        builder.append( theta * 180.0f / (float) Math.PI );
        builder.append( "],phi=[" );
        builder.append( phi * 180.0f / (float) Math.PI );
        builder.append( "]}" );
        
        return builder.toString();
    }

}
