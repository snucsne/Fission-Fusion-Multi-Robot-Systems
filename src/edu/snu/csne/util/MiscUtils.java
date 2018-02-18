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

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class MiscUtils
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            MiscUtils.class.getName() );
    
    /**
     * Loads the properties file specified using the given key
     *
     * @param propsFileKey The property key corresponding to the experiment properties
     * @return The properties
     */
    public static Properties loadProperties( String propsFileKey )
    {
        _LOG.trace( "Entering loadProperties( propsFileKey )" );

        // Load the specified properties file
        String propsFilename = System.getProperty( propsFileKey );
        Validate.notEmpty( propsFilename, "Property filename (key=["
                + propsFileKey
                + "] may not be empty" );
        Properties props = loadPropertiesFromFile( propsFilename );

        _LOG.trace( "Leaving loadProperties( propsFileKey )" );

        return props;
    }

    /**
     * Loads the properties file specified using the given key
     *
     * @param propsFilename The filename
     * @return The properties
     */
    public static Properties loadPropertiesFromFile( String propsFilename )
    {
        _LOG.trace( "Entering loadPropertiesFromFile( propsFilename )" );

        // Load the specified properties file
        Properties props = new Properties();
        File propsFile = new File( propsFilename );
        if( !propsFile.exists() )
        {
            _LOG.error( "Unable to find properties file with name ["
                    + propsFilename
                    + "]" );
            throw new RuntimeException(
                    "Unable to find properties file with name ["
                    + propsFilename
                    + "]" );
        }
        try
        {
            props.load( new FileInputStream( propsFile ) );
        }
        catch( IOException ioe )
        {
            _LOG.error( "Unable to load properties file ["
                    + propsFile.getAbsolutePath()
                    + "]", ioe );
            throw new RuntimeException( "Unable to load properties file ["
                    + propsFile.getAbsolutePath()
                    + "]", ioe );
        }

        // Iterate through all the properties from the command line
        Properties systemProps = System.getProperties();
        Iterator<String> iter = props.stringPropertyNames().iterator();
        while( iter.hasNext() )
        {
            String key = iter.next();

            // Was there an override specified on the command line?
            String value = systemProps.getProperty( key );
            if( null != value )
            {
                // Yup
                props.setProperty( key, value );
            }
        }

        _LOG.trace( "Leaving loadPropertiesFromFile( propsFilename )" );

        return props;
    }

    /**
     * Loads and instantiates the class with the specified filename
     *
     * @param className The name of the class to load and instantiate
     * @param desc Description of the class for error logging
     * @return The instantiated class
     */
    static public Object loadAndInstantiate( String className, String desc )
    {
        Object instantiated = null;
        try
        {
            // Get the class
            Class instantiatedClass = Class.forName( className );

            // Instantiate it
            instantiated = instantiatedClass.newInstance();
        }
        catch( ClassNotFoundException cnfe )
        {
            _LOG.error( "Unable to find "
                    + desc
                    + " class with name ["
                    + className
                    + "]", cnfe );
            throw new RuntimeException(
                    "Unable to find "
                    + desc
                    + " class with name ["
                    + className
                    + "]", cnfe );
        }
        catch( IllegalAccessException iae )
        {
            _LOG.error( "Unable to access constructor for "
                    + desc
                    + " class ["
                    + className
                    + "]", iae );
            throw new RuntimeException(
                    "Unable to access constructor for "
                    + desc
                    + " class ["
                    + className
                    + "]", iae );
        }
        catch( InstantiationException ie )
        {
            _LOG.error( "Unable to instantiate "
                    + desc
                    + " class ["
                    + className
                    + "]", ie );
            throw new RuntimeException(
                    "Unable to instantiate "
                    + desc
                    + " class ["
                    + className
                    + "]", ie );
        }

        return instantiated;
    }

    /**
     * Loads a non-empty float value from properties
     *
     * @param props All the properties
     * @param key The key of the value
     * @param description A description of the error to generate if the value
     * is not found
     * @return The float value
     */
    public static float loadNonEmptyFloatProperty( Properties props,
            String key,
            String description )
    {
        String valueStr = props.getProperty( key );
        org.apache.commons.lang3.Validate.notEmpty( valueStr,
                description
                + "(key="
                + key
                + ") may not be empty" );
        float value = Float.parseFloat( valueStr );
        
        return value;
    }

    /**
     * Loads an optional float value from properties
     *
     * @param props All the properties
     * @param key The key of the value
     * @param defaultValue The default value to use if the property doesn't exist
     * @return The float value
     */
    public static float loadOptionalFloatProperty( Properties props,
            String key,
            float defaultValue )
    {
        // Use the default
        float value = defaultValue;
        
        // Was the value specified?
        String valueStr = props.getProperty( key );
        if( null != valueStr )
        {
            value = Float.parseFloat( valueStr );
        }
        
        return value;
    }

    /**
     * Loads a non-empty integer value from properties
     *
     * @param props All the properties
     * @param key The key of the value
     * @param description A description of the error to generate if the value
     * is not found
     * @return The float value
     */
    public static int loadNonEmptyIntegerProperty( Properties props,
            String key,
            String description )
    {
        String valueStr = props.getProperty( key );
        org.apache.commons.lang3.Validate.notEmpty( valueStr,
                description
                + "(key="
                + key
                + ") may not be empty" );
        int value = Integer.parseInt( valueStr );
        
        return value;
    }

    /**
     * Loads a non-empty long value from properties
     *
     * @param props All the properties
     * @param key The key of the value
     * @param description A description of the error to generate if the value
     * is not found
     * @return The float value
     */
    public static long loadNonEmptyLongProperty( Properties props,
            String key,
            String description )
    {
        String valueStr = props.getProperty( key );
        org.apache.commons.lang3.Validate.notEmpty( valueStr,
                description
                + "(key="
                + key
                + ") may not be empty" );
        long value = Long.parseLong( valueStr );
        
        return value;
    }
    
    /**
     * Loads a non-empty boolean value from properties
     *
     * @param props All the properties
     * @param key The key of the value
     * @param description A description of the error to generate if the value
     * is not found
     * @return The boolean value
     */
    public static boolean loadNonEmptyBooleanProperty( Properties props,
            String key,
            String description )
    {
        String valueStr = props.getProperty( key );
        org.apache.commons.lang3.Validate.notEmpty( valueStr,
                description
                + "(key="
                + key
                + ") may not be empty" );
        Boolean value = Boolean.parseBoolean( valueStr );
        
        return value;
    }

    /**
     * Loads a non-empty Vector3f value from properties
     *
     * @param props All the properties
     * @param key The key of the value
     * @param description A description of the error to generate if the value
     * is not found
     * @return The float value
     */
    public static Vector3f loadNonEmptyVector3fProperty( Properties props,
            String key,
            String description )
    {
        String valueStr = props.getProperty( key );
        org.apache.commons.lang3.Validate.notEmpty( valueStr,
                description
                + "(key="
                + key
                + ") may not be empty" );
        Vector3f value = parseVector( valueStr );
        
        return value;
    }

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

    /**
     * Parses a color object from a comma-separated string
     * 
     * @param colorDef The color definition
     * @return The color
     */
    public static ColorRGBA parseJMEColor( String colorDef )
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

    /**
     * Parses a color object from a comma-separated string
     * 
     * @param colorDef The color definition
     * @return The color
     */
    public static Color parseAWTColor( String colorDef )
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
        
        return new Color( red, green, blue, alpha );
    }

}
