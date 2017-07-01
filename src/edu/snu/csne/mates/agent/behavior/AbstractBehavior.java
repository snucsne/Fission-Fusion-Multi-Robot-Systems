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
package edu.snu.csne.mates.agent.behavior;

// Imports
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.snu.csne.mates.agent.Agent;
import edu.snu.csne.mates.agent.FloatVariable;
import edu.snu.csne.mates.agent.parameter.Parameter;
import edu.snu.csne.mates.agent.parameter.ParameterKey;
import edu.snu.csne.mates.agent.parameter.ParameterUtils;
import edu.snu.csne.mates.sim.SimulationState;
import edu.snu.fuzzy.util.NotYetImplementedException;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public abstract class AbstractBehavior implements Behavior
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            AbstractBehavior.class.getName() );
    
    /** The unique identifier of this behavior */
    private Object _id = null;

    /** The weight variable ID */
    private String _weightVarID = null;

    /** The weight for this behavior */
    private FloatVariable _weight = null;

    /** The threshold variable ID */
    private String _thresholdVarID = null;

    /** The threshold for this behavior */
    private FloatVariable _threshold = null;

    
    /**
     * Builds this AbstractBehavior object
     *
     * @param id The behavior's unique ID
     */
    public AbstractBehavior( Object id )
    {
        // Validate the ID
        Validate.notNull( id, "ID may not be null" );
        _id = id;
    }
    
    /**
     * Stores the parameters for this behavior 
     *
     * @param params The parameters
     * @see edu.snu.csne.mates.agent.behavior.Behavior#storeParameters(java.util.Map)
     */
    @Override
    public void storeParameters( Map<ParameterKey, Parameter> params )
    {
        _LOG.trace( "Entering storeParameters( params )" );

        // Get the weight variable ID
        _weightVarID = ParameterUtils.getStringParameterValue( params,
                ParameterKey.BEHAVIOR_WEIGHT_VAR_ID,
                true );
        Validate.notEmpty( _weightVarID,
                "Behavior weight variable ID may not be null or empty" );
        
        // Get the threshold variable ID
        _thresholdVarID = ParameterUtils.getStringParameterValue( params,
                ParameterKey.BEHAVIOR_THRESHOLD_VAR_ID,
                true );
        Validate.notEmpty( _thresholdVarID,
                "Behavior threshold variable ID may not be null or empty" );

        if( _LOG.isDebugEnabled() )
        {
            _LOG.debug( "Loaded weightVarID=[" + _weightVarID + "]" );
            _LOG.debug( "Loaded thresholdVarID=[" + _thresholdVarID + "]" );
        }

        _LOG.trace( "Leaving storeParameters( params )" );
    }

    /**
     * Initializes this behavior
     *
     * @param agent The agent to which this behavior belongs
     * @param simState The current state of the simulation
     * @see edu.snu.csne.mates.agent.behavior.Behavior#initialize(edu.snu.csne.mates.agent.Agent, edu.snu.csne.mates.sim.SimulationState)
     */
    @Override
    public void initialize( Agent agent, SimulationState simState )
    {
        // Get the weight and threshold variables
        throw new NotYetImplementedException();
    }

    /**
     * Returns the unique identifier of this behavior
     *
     * @return The unique identifier
     * @see edu.snu.csne.mates.agent.behavior.Behavior#getID()
     */
    @Override
    public Object getID()
    {
        return _id;
    }

    /**
     * Returns the weight for this behavior.
     *
     * @return The weight as a FloatVariable
     * @see edu.snu.csne.mates.agent.behavior.Behavior#getWeight()
     */
    @Override
    public FloatVariable getWeight()
    {
        return _weight;
    }

    /**
     * Returns the threshold for this behavior
     *
     * @return The threshold has a FloatVariable
     * @see edu.snu.csne.mates.agent.behavior.Behavior#getThreshold()
     */
    @Override
    public FloatVariable getThreshold()
    {
        return _threshold;
    }

    /**
     * Determines if this behavior's weight is above the threshold and,
     * therefore, active
     *
     * @return <code>true</code> if the behavior is active, otherwise,
     * <code>false</code>
     * @see edu.snu.csne.mates.agent.behavior.Behavior#isActive()
     */
    @Override
    public boolean isActive()
    {
        return ((_weight.getValue() * _weight.getScalingFactor())
                >= _threshold.getValue());
    }

    /**
     * Destroys this behavior
     *
     * @see edu.snu.csne.mates.agent.behavior.Behavior#destroy()
     */
    @Override
    public void destroy()
    {
        // Do nothing
    }

    /**
     * Returns the string representation of this behavior
     *
     * @return The string representation
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder( this.getClass().getName() );
        builder.append( "=[" );
        builder.append( _id );
        builder.append( "]" );

        return builder.toString();
    }

}
