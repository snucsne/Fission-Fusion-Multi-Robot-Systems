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
package edu.snu.csne.mates.util;

// Imports
import com.jme3.math.Vector3f;

/**
 * Miscellaneous math utilities
 *
 * @author Brent Eskridge
 */
public class MathUtils
{
    /**
     * Parses a vector3f object from a comma-separated string
     * 
     * @param vectorDef The vector3f definition
     * @return The vector3f
     */
    public static Vector3f parseVector( String vectorDef )
    {
        // Get the individual components
        String[] components = vectorDef.split( "," );
        if( components.length != 3 )
        {
            throw new IllegalArgumentException( "Invalid vector3f definition ["
                    + vectorDef
                    + "]" );
        }
        
        // Get the components as floats
        float x, y, z;
        try
        {
            x = Float.parseFloat( components[0] );
            y = Float.parseFloat( components[1] );
            z = Float.parseFloat( components[2] );
        }
        catch( NumberFormatException nfe )
        {
            throw new IllegalArgumentException( "Invalid vector3f definition ["
                    + vectorDef
                    + "]" );
        }
        
        return new Vector3f( x, y, z );
    }

}
