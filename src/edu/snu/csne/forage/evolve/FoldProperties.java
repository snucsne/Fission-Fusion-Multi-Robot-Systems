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
package edu.snu.csne.forage.evolve;

import java.io.FileReader;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class FoldProperties
{
    public enum FoldType
    {
        TRAINING,
        TESTING,
        VALIDATION;
    }
    
    public enum PropertyType
    {
        AGENT,
        PATCH;
    }
    
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            FoldProperties.class.getName() );
    
    private Map<FoldType, Map<PropertyType,Properties[]>> _props =
            new EnumMap<FoldType, Map<PropertyType,Properties[]>>( FoldType.class );
    
    public FoldProperties()
    {
        for( FoldType foldType : FoldType.values() )
        {
            
            Map<PropertyType,Properties[]> foldProps =
                    new EnumMap<PropertyType,Properties[]>( PropertyType.class );
            _props.put( foldType, foldProps );
            for( PropertyType propType : PropertyType.values() )
            {
                foldProps.put( propType, new Properties[0] );
            }
        }
    }
    
    public void initialize( String propDefsFile )
    {
        // Load the properties file
        Properties propDefs = new Properties();
        try
        {
            propDefs.load( new FileReader( propDefsFile ) );
        }
        catch( Exception e )
        {
            _LOG.error( "Unable to load fold properties definitions file ["
                    + propDefsFile
                    + "]",
                    e );
            throw new RuntimeException( "Unable to load fold properties definitions file ["
                    + propDefsFile
                    + "]",
                    e );
        }
        
        for( FoldType foldType : FoldType.values() )
        {
            
            Map<PropertyType,Properties[]> foldProps =
                    new EnumMap<PropertyType,Properties[]>( PropertyType.class );
            for( PropertyType propType : PropertyType.values() )
            {
                // Build the property key
                String key = foldType.name().toLowerCase()
                        + "."
                        + propType.name().toLowerCase()
                        + ".prop-files";
                
                // Get all the properties files
                String propFilesStr = propDefs.getProperty( key );
                String[] propFiles = propFilesStr.split( " " );
                
                Properties[] props = new Properties[ propFiles.length ];
                for( int i = 0; i < propFiles.length; i++ )
                {
                    props[i] = new Properties();
                    try
                    {
                        props[i].load( new FileReader( propFiles[i] ) );
                    }
                    catch( Exception e )
                    {
                        _LOG.error( "Unable to load fold properties file: foldType=["
                                + foldType
                                + "] propType=["
                                + propType
                                + "] file=["
                                + propFiles[i]
                                + "]",
                                e );
                        throw new RuntimeException( "Unable to load fold properties file: foldType=["
                                + foldType
                                + "] propType=["
                                + propType
                                + "] file=["
                                + propFiles[i]
                                + "]",
                                e );
                    }
                }
                
                foldProps.put( propType, props );
            }
        }
    }
    
    public void setProperties( FoldType foldType,
            PropertyType propType,
            Properties[] props )
    {
        Map<PropertyType,Properties[]> foldProps = _props.get( foldType );
        foldProps.put( propType, props );
    }
    
    public Properties[] getProperties( FoldType foldType,
            PropertyType propType )
    {
        Map<PropertyType,Properties[]> foldProps = _props.get( foldType );
        return foldProps.get( propType );
    }
}
