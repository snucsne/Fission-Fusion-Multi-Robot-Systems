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
package edu.snu.csne.forage;

import com.jme3.math.Vector3f;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class ForageUtils
{
    public static void limitMagnitude( Vector3f vec, float max )
    {
        // Is the magnitude larger than the max?
        float maxSquared = max * max;
        if( vec.lengthSquared() > maxSquared )
        {
            // Yup, scale it appropriately
            vec.normalizeLocal();
            vec.multLocal( max );
        }
    }
}
