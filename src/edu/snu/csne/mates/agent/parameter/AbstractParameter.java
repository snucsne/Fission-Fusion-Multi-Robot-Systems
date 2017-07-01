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

import org.apache.commons.lang3.Validate;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public abstract class AbstractParameter implements Parameter
{
    /** The parameter's type */
    private Parameter.Type _type = null;

    /** The parameter's key */
    private ParameterKey _key = null;


    /**
     * Builds this AbstractParameter object
     *
     * @param type The parameter's type
     * @param key The parameter's key
     */
    public AbstractParameter( Parameter.Type type, ParameterKey key )
    {
        // Validate what we were passed
        Validate.notNull( type, "Parameter type may not be null" );
        Validate.notNull( key, "Parameter key may not be null" );
        
        // Store them
        _type = type;
        _key = key;
    }
    
    /**
     * Returns this parameter's type
     *
     * @return The type
     * @see edu.snu.csne.mates.agent.parameter.Parameter#getType()
     */
    @Override
    public Type getType()
    {
        return _type;
    }

    /**
     * Returns this parameter's key
     *
     * @return The key
     * @see edu.snu.csne.mates.agent.parameter.Parameter#getKey()
     */
    @Override
    public ParameterKey getKey()
    {
        return _key;
    }


}
