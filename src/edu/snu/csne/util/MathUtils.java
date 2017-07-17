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

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class MathUtils
{
    /**
     * Round an integer value using the specified step size
     *
     * @param value The value to round
     * @param step The step size to which the value will be rounded
     * @return The rounded value
     */
    public static int round(int value, int step) 
    {
        return (int) ((Math.round( value / (float) step )) * step);
    }

}
