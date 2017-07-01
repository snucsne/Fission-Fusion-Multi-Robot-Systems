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

//Imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.snu.fuzzy.ValueDatabase;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class AgentBlackboard
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            AgentBlackboard.class.getName() );
    
    /** Map of all the float variables */
    private Map<String, FloatVariable> _floatVariables =
            new HashMap<String, FloatVariable>();
    
    /** The fuzzy variable database */
    private ValueDatabase _fuzzyValueDB = new ValueDatabase();
    
    /** Map of generic data */
    private Map<AgentBlackBoardDataKey,Object> _data = new HashMap<>();
    
    
    /**
     * Adds a float variable to this agent blackboard
     *
     * @param var The float variable to add
     */
    public void addFloatVariable( FloatVariable var )
    {
        // Validate it
        Validate.notNull( "FloatVariable may not be null" );
        _floatVariables.put( var.getName(), var );
    }
    
    /**
     * Returns the float variable specified by the given name
     *
     * @param name The name of the float variable
     * @return The float variable
     */
    public FloatVariable getFloatVariable( String name )
    {
        // Validate the name
        Validate.notBlank( name, "FloatVariable name may not be null" );
        
        // Get the variable
        FloatVariable var = _floatVariables.get( name );
        
        // Validate we got something
        Validate.notNull( var, "No FloatVariable with name ["
                + name
                + "] found" );
        
        return var;
    }
    
    /**
     * Returns a list of all the float variable names
     *
     * @return The list of names
     */
    public List<String> getAllFloatVariableNames()
    {
        return new ArrayList<String>( _floatVariables.keySet() );
    }
    
    /**
     * Returns the fuzzy value database 
     *
     * @return The fuzzy value database
     */
    public ValueDatabase getFuzzyValueDatabase()
    {
        return _fuzzyValueDB;
    }
    
    /**
     * Add generic data to this blackboard
     *
     * @param key The data key
     * @param value The data value
     */
    public void addData( AgentBlackBoardDataKey key, Object value )
    {
        // Validate them
        Validate.notNull( key, "Data key may not be null" );
        Validate.notNull( value, "Data value may not be null" );
        
        _data.put( key, value );
    }
    
    /**
     * Returns the data value associated with the specified key
     *
     * @param key The data's key
     * @return The data's value
     */
    public Object getData( AgentBlackBoardDataKey key )
    {
        // Validate it
        Validate.notNull( key, "Data key may not be null" );
        
        Object value = _data.get( key );
        
        // Validate the value
        Validate.notNull( value, "No data value found for key ["
                + key
                + "]" );
        
        return value;
    }
    
    public Object removeData( Object key )
    {
        // Validate it
        Validate.notNull( key, "Data key may not be null" );
        
        Object value = _data.remove( key );
        
        // Validate the value
        Validate.notNull( value, "No data value found for key ["
                + key
                + "]" );
        
        return value;
    }
    
    /**
     * Returns a list of all the data key values
     *
     * @return The keys
     */
    public List<Object> getAllDataKeys()
    {
        return new ArrayList<Object>( _data.keySet() );
    }
    
    /**
     * Clears all the data from this agent blackboard
     */
    public void clear()
    {
        // Clear out all the maps
        _floatVariables.clear();
        _data.clear();
        
        // Build a new fuzzy value database
        _fuzzyValueDB = new ValueDatabase();
    }
}
