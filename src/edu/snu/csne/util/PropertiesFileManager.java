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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
// Imports
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class PropertiesFileManager
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            PropertiesFileManager.class.getName() );
    

    private Map<String,Properties> _loadedProps =
            new HashMap<String,Properties>();


    public Properties getPropertiesByFilename( String filename )
    {
        _LOG.trace( "Entering getPropertiesByFilename( filename )" );

        // Ensure it is a valid parameter
        Validate.notEmpty( filename,
                "Properties file name may not be null or empty" );

        // Try to get it from storage first
        Properties originalProps = _loadedProps.get( filename );
        if( null == originalProps )
        {
            // We haven't loaded it yet.  Do that now.
            originalProps = loadPropertiesFromFile( filename );
            _loadedProps.put( filename, originalProps );
        }

        /* Create a new properties object so we don't accidentally
         * change the original somewhere */
        Properties props = new Properties();
        props.putAll( originalProps );

        _LOG.trace( "Leaving getPropertiesByFilename( filename )" );

        return props;
    }
    /**
     * Loads the properties file specified using the given key
     *
     * @param propsFileKey The property key corresponding to the experiment properties
     * @return The properties
     */
    public Properties loadPropertiesByKey( String propsFileKey )
    {
        _LOG.trace( "Entering loadPropertiesByKey( propsFileKey )" );

        // Load the specified properties file
        String propsFilename = System.getProperty( propsFileKey );
        Validate.notEmpty( propsFilename, "Property filename (key=["
                + propsFileKey
                + "] may not be empty" );
        Properties props = getPropertiesByFilename( propsFilename );

        _LOG.trace( "Leaving loadPropertiesByKey( propsFileKey )" );

        return props;
    }

    /**
     * Loads the properties file specified using the given key
     *
     * @param propsFilename The filename
     * @return The properties
     */
    private Properties loadPropertiesFromFile( String propsFilename )
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

}
