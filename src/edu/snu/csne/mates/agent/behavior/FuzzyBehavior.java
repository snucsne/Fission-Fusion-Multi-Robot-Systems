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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.snu.csne.mates.agent.Agent;
import edu.snu.csne.mates.agent.parameter.Parameter;
import edu.snu.csne.mates.agent.parameter.ParameterKey;
import edu.snu.csne.mates.sim.SimulationState;
import edu.snu.csne.mates.util.NotYetImplementedException;
import edu.snu.fuzzy.RuleSet;
import edu.snu.fuzzy.ValueDatabase;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class FuzzyBehavior extends AbstractBehavior
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            FuzzyBehavior.class.getName() );
    
    /** The set of rules for this behavior */
    private RuleSet _ruleSet = null;
    
    /** The fuzzy value database */
    private ValueDatabase _fuzzyValueDB = null;
    
    
    /**
     * Builds this FuzzyBehavior object
     *
     * @param id The unique ID
     */
    public FuzzyBehavior( Object id )
    {
        // Call the superclass constructor
        super( id );
    }
    
    /**
     * Stores the parameters for this behavior 
     *
     * @param params The parameters
     * @see edu.snu.csne.mates.agent.behavior.AbstractBehavior#storeParameters(java.util.Map)
     */
    @Override
    public void storeParameters( Map<ParameterKey, Parameter> params )
    {
        // Call the superclass implementation
        super.storeParameters( params );
        
        // TODO
        throw new NotYetImplementedException();
    }

    /**
     * Initializes this behavior
     *
     * @param agent The agent to which this behavior belongs
     * @param simState The current state of the simulation
     * @see edu.snu.csne.mates.agent.behavior.AbstractBehavior#initialize(edu.snu.csne.mates.agent.Agent, edu.snu.csne.mates.sim.SimulationState)
     */
    @Override
    public void initialize( Agent agent, SimulationState simState )
    {
        // Call the superclass implementation
        super.initialize( agent, simState );
        
        // Get the fuzzy value database
        _fuzzyValueDB = agent.getBlackboard().getFuzzyValueDatabase();
    }


    /**
     * Executes this behavior.
     *
     * @see edu.snu.csne.mates.agent.behavior.Behavior#execute()
     */
    @Override
    public void execute()
    {
        // Don't execute if we aren't active
        if( !isActive() )
        {
            return;
        }
        
        // Get the doa
        float doa = getWeight().getValue();
        doa *= getWeight().getScalingFactor();
        
        // Evaluate the ruleset
        _ruleSet.evaluate( _fuzzyValueDB, doa );
    }

}
