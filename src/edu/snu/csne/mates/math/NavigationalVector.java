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

// imports
import org.apache.commons.lang3.Validate;
import com.jme3.math.Vector3f;


/**
 * A vector using navigational coordinates.  This is similar to spherical
 * coordinate, but using different starting angles and orientations.
 *
 * @author Brent Eskridge
 */
public class NavigationalVector
{
    /** PI */
    public static final float PI = (float) Math.PI;

    /** 2PI */
    public static final float TWO_PI = 2.0f * (float) Math.PI;

    /** PI / 2 */
    public static final float HALF_PI = (float) Math.PI / 2.0f;

    /** The radius of the vector */
    public float r = 0.0f;

    /** The azimuth angle in the x-y plane in radians */
    public float theta = 0.0f;

    /** The polar angle in the z axis in radians */
    public float phi = 0.0f;

    /**
     * Builds this navigational vector.
     */
    public NavigationalVector()
    {
        this( 0.0f, 0.0f, 0.0f );
    }

    /**
     * Builds this navigational vector.
     *
     * @param r The radius
     * @param theta The azimuth angle i radians
     * @param phi The polar angle in radians
     */
    public NavigationalVector( float r, float theta, float phi )
    {
        // TODO Validate them
        this.r = r;
        this.theta = theta;
        this.phi = phi;
    }

    /**
     * Builds this navigational vector from a cartesian x,y,z vector.
     *
     * @param vector The cartesian vector
     */
    public NavigationalVector( Vector3f vector )
    {
        // Verify the vector
        Validate.notNull( vector, "Cartesian vector may not be null" );
        
        // Is it a zero vector?
        if( Vector3f.ZERO.equals( vector ) )
        {
            r = 0.0f;
            theta = 0.0f;
            phi = 0.0f;
        }
        else
        {
            // Create a spherical vector and modify it
            SphericalVector sv = new SphericalVector( vector );

            // Convert it to a nav vector
            this.r = sv.r;
            this.theta = HALF_PI - sv.theta;
            if( this.theta < -1.0f * Math.PI )
            {
                this.theta += TWO_PI;
            }
            this.phi = HALF_PI - sv.phi;
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
     *
     * @return The length of this vector
     */
    public float length()
    {
        return r;
    }

    /**
     * Returns a navigational vector that points in the opposite direction.
     *
     * @return The reverse of this navigational vector
     */
    public NavigationalVector reverse()
    {
        NavigationalVector reverse = new NavigationalVector( r, theta, phi );

        // Reverse it
        reverse.theta += Math.PI;
        if( TWO_PI <= reverse.theta )
        {
            reverse.theta -= TWO_PI;
        }
        reverse.phi *= -1.0f;

        return reverse;
    }

    /**
     * Converts this spherical vector to a cartesian vector.
     *
     * @return The cartesian vector
     */
    public Vector3f toVector3f()
    {
        // Create a spherical vector
        float thetaSpherical = HALF_PI - theta;
        if( thetaSpherical < 0.0f )
        {
            thetaSpherical += TWO_PI;
        }
        float phiSpherical = HALF_PI - phi;

        SphericalVector vector = new SphericalVector( r,
                thetaSpherical,
                phiSpherical );

        return vector.toVector3f();
    }

    /**
     * Returns the string representation of this vector.
     *
     * @return The string representation
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder( "NavigationalVector{r=[");
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
        StringBuilder builder = new StringBuilder( "NavigationalVector{r=[");
        builder.append( r );
        builder.append( "],theta=[" );
        builder.append( theta * 180.0f / (float) Math.PI );
        builder.append( "],phi=[" );
        builder.append( phi * 180.0f / (float) Math.PI );
        builder.append( "]}" );

        return builder.toString();
    }

}
