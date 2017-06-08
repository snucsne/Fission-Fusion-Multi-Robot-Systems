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
import com.jme3.math.ColorRGBA;

/**
 * Miscellaneous utilities
 *
 * @author Brent Eskridge
 */
public class MiscUtils
{
    /**
     * Parses a color object from a comma-separated string
     * 
     * @param colorDef The color definition
     * @return The color
     */
    public static ColorRGBA parseColor( String colorDef )
    {
        // Get the individual components
        String[] components = colorDef.split( "," );
        if( components.length != 4 )
        {
            throw new IllegalArgumentException( "Invalid color definition ["
                    + colorDef
                    + "]" );
        }
        
        // Get the components as floats
        float red, green, blue, alpha;
        try
        {
            red = Float.parseFloat( components[0] );
            green = Float.parseFloat( components[1] );
            blue = Float.parseFloat( components[2] );
            alpha = Float.parseFloat( components[3] );
        }
        catch( NumberFormatException nfe )
        {
            throw new IllegalArgumentException( "Invalid color definition ["
                    + colorDef
                    + "]" );
        }
        
        return new ColorRGBA( red, green, blue, alpha );
    }

}
