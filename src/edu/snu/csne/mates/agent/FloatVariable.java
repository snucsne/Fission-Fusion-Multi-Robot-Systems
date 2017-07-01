/*
 *  The Fission-Fusion in Multi-Robot Systems Toolkit is open-source
 *  software for investigating fission-fusion processes in
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
package edu.snu.csne.mates.agent;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public interface FloatVariable
{
    /**
     * Returns the name of this float variable
     *
     * @return The name
     */
    public String getName();
    
    /**
     * Sets the value of this float variable
     *
     * @param value The value
     * @throws UnsupportedOperationException If this method is not supported
     */
    public void setValue( float value );
    
    /**
     * Returns the value of this float variable
     *
     * @return The value
     */
    public float getValue();
    
    /**
     * Returns the scaling factor of this float variable.
     *
     * @return The scaling factor
     */
    public float getScalingFactor();
}
