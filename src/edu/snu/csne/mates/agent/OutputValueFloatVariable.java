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

// Imports
import org.apache.commons.lang3.Validate;

import edu.snu.csne.util.NotYetImplementedException;
import edu.snu.fuzzy.OutputValue;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class OutputValueFloatVariable implements FloatVariable
{
    /** The name of this float variable */
    private String _name = null;
    
    /** The output value for this float variable */
    private OutputValue _value = null;

    
    /**
     * Builds this OutputValueFloatVariable object
     *
     * @param name
     * @param value
     */
    public OutputValueFloatVariable( String name, OutputValue value )
    {
        // Validate the parameters
        Validate.notBlank( name, "Name may not be null" );
        Validate.notNull( value, "Output value may not be null" );
        
        // Store them
        _name = name;
        _value = value;
    }
    
    /**
     * Returns the name of this float variable
     *
     * @return The name
     * @see edu.snu.csne.mates.agent.FloatVariable#getName()
     */
    @Override
    public String getName()
    {
        return _name;
    }

    /**
     * Sets the value of this float variable
     *
     * @param value The value
     * @throws UnsupportedOperationException If this method is not supported
     * @see edu.snu.csne.mates.agent.FloatVariable#setValue(float)
     */
    @Override
    public void setValue( float value )
    {
        throw new UnsupportedOperationException(
                "Output value float variables do not support this method" );
    }

    /**
     * Returns the value of this float variable
     *
     * @return The value
     * @see edu.snu.csne.mates.agent.FloatVariable#getValue()
     */
    @Override
    public float getValue()
    {
        return _value.defuzzify();
    }

    /**
     * Returns the string representation of this input value
     *
     * @return The string representation
     * @see java.lang.Object#toString()
     */
    @Override
    public float getScalingFactor()
    {
        return _value.getScalingFactor();
    }

    /**
     * TODO Method description
     *
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder( super.toString() );
        builder.append( ": name=[" );
        builder.append( _name );
        builder.append( "] outputValue=[" );
        builder.append( _value );
        builder.append( "]" );
        
        return builder.toString();
    }

    
}
