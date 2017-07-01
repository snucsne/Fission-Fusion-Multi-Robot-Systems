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
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class ParameterUtils
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            ParameterUtils.class.getName() );
    
    
    /**
     * Retrieves the specified float parameter value from the parameter map
     *
     * @param parameters All the parameters
     * @param key The parameter value's key
     * @param required <code>true</code> if the parameter value is required,
     * otherwise <code>false</code>
     * @return The float parameter value
     */
    public static Float getFloatParameterValue(
            Map<ParameterKey, Parameter> parameters,
            ParameterKey key,
            boolean required )
    {
        // Validate what we were passed
        Validate.notEmpty( parameters, "Parameters may not be null or empty" );
        Validate.notNull( key, "Key may not be null" );
        if( required )
        {
            Validate.isTrue( parameters.containsKey( key ),
                    "Parameter key ["
                    + key
                    + "] not found" );
        }
        
        // Pull out the parameter
        Float value = null;
        Parameter parameter = parameters.get( key );
        if( null != parameter )
        {
            // Ensure it is the correct type
            Validate.isInstanceOf( FloatParameter.class,
                    parameter,
                    "Parameter with key ["
                    + key
                    + "] is NOT a float: "
                    + parameter.getClass().getCanonicalName().toString() );
            
            value = ((FloatParameter) parameter).getValue();
        }
        
        return value;
    }
    
    /**
     * Retrieves the specified integer parameter value from the parameter map
     *
     * @param parameters All the parameters
     * @param key The parameter value's key
     * @param required <code>true</code> if the parameter value is required,
     * otherwise <code>false</code>
     * @return The integer parameter value
     */
    public static Integer getIntegerParameterValue(
            Map<ParameterKey, Parameter> parameters,
            ParameterKey key,
            boolean required )
    {
        // Validate what we were passed
        Validate.notEmpty( parameters, "Parameters may not be null or empty" );
        Validate.notNull( key, "Key may not be null" );
        if( required )
        {
            Validate.isTrue( parameters.containsKey( key ),
                    "Parameter key ["
                    + key
                    + "] not found" );
        }
        
        // Pull out the parameter
        Integer value = null;
        Parameter parameter = parameters.get( key );
        if( null != parameter )
        {
            // Ensure it is the correct type
            Validate.isInstanceOf( IntegerParameter.class,
                    parameter,
                    "Parameter with key ["
                    + key
                    + "] is NOT a integer: "
                    + parameter.getClass().getCanonicalName().toString() );
            
            value = ((IntegerParameter) parameter).getValue();
        }
        
        return value;
    }
    
    /**
     * Retrieves the specified integer parameter value from the parameter map
     *
     * @param parameters All the parameters
     * @param key The parameter value's key
     * @param required <code>true</code> if the parameter value is required,
     * otherwise <code>false</code>
     * @return The integer parameter value
     */
    public static String getStringParameterValue(
            Map<ParameterKey, Parameter> parameters,
            ParameterKey key,
            boolean required )
    {
        // Validate what we were passed
        Validate.notEmpty( parameters, "Parameters may not be null or empty" );
        Validate.notNull( key, "Key may not be null" );
        if( required )
        {
            Validate.isTrue( parameters.containsKey( key ),
                    "Parameter key ["
                    + key
                    + "] not found" );
        }
        
        // Pull out the parameter
        String value = null;
        Parameter parameter = parameters.get( key );
        if( null != parameter )
        {
            // Ensure it is the correct type
            Validate.isInstanceOf( StringParameter.class,
                    parameter,
                    "Parameter with key ["
                    + key
                    + "] is NOT a string: "
                    + parameter.getClass().getCanonicalName().toString() );
            
            value = ((StringParameter) parameter).getValue();
        }
        
        return value;
    }

}
