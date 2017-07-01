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

// Imports
import org.apache.commons.lang3.Validate;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class StringParameter extends AbstractParameter
{
    /** The parameter's string value */
    private String _value = null;


    /**
     * Builds this IntegerParameter object
     *
     * @param key The parameter's key
     * @param value The parameter's value
     */
    public StringParameter( ParameterKey key, String value )
    {
        // Call the superclass constructor
        super( Parameter.Type.INTEGER, key );

        // Validate the value
        Validate.notNull( value, "Parameter string value may not be null" );
        _value = value;
    }

    /**
     * Returns this parameter's value
     *
     * @return The value
     * @see edu.snu.csne.mates.agent.parameter.Parameter#getValue()
     */
    @Override
    public String getValue()
    {
        return _value;
    }

    /**
     * Returns the string representation of this parameter
     *
     * @return The string representation
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(
                this.getClass().getCanonicalName() );
        builder.append( ": [" );
        builder.append( getKey() );
        builder.append( "]=[" );
        builder.append( _value );
        builder.append( "]" );
        
        return builder.toString();
    }
}