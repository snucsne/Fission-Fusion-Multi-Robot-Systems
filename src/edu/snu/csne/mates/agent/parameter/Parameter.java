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
package edu.snu.csne.mates.agent.parameter;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public interface Parameter
{
    /**
     * The potential types of parameters
     *
     * @author Brent Eskridge
     */
    public enum Type
    {
        DEFINITION_ID,
        STRING,
        FLOAT,
        INTEGER,
        BOOLEAN,
        VECTOR3F,
        GEOMTRY
    }

    /**
     * Returns this parameter's type
     *
     * @return The type
     */
    public Type getType();
    
    /**
     * Returns this parameter's key
     *
     * @return The key
     */
    public ParameterKey getKey();
    
    /**
     * Returns this parameter's value
     *
     * @return The value
     */
    public Object getValue();
}
