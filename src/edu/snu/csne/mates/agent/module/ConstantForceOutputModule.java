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
package edu.snu.csne.mates.agent.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;

import edu.snu.csne.mates.agent.Agent;
import edu.snu.csne.mates.sim.SimulationState;
import edu.snu.csne.util.NotYetImplementedException;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class ConstantForceOutputModule extends AbstractAgentModule
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            ConstantForceOutputModule.class.getName() );
    

    /**
     * Initializes this module
     *
     * @param agent The agent to which this module belongs
     * @param simState The current state of the simulation
     * @see edu.snu.csne.mates.agent.module.AbstractAgentModule#initialize(edu.snu.csne.mates.agent.Agent, edu.snu.csne.mates.sim.SimulationState)
     */
    @Override
    public void initialize( Agent agent, SimulationState simState )
    {
        _LOG.trace( "Entering initialize( agent, simState )" );

        // Call the superclass implementation
        super.initialize( agent, simState );
        
        _LOG.trace( "Leaving initialize( agent, simState )" );
    }

    /**
     * Executes this module
     *
     * @see edu.snu.csne.mates.agent.module.AbstractAgentModule#execute()
     */
    @Override
    public void execute()
    {
        _LOG.trace( "Entering execute()" );

        // Get the force (accelleration/decelleration)
        Vector3f force = null;
        
        // Get the torque (turning)
        Vector3f torque = null;
        
        // Get the control object
        RigidBodyControl control = null;
        
        // Use the control to apply the force and torque
        control.applyCentralForce( force );
        control.applyTorque( torque );
        
        if( _LOG.isDebugEnabled() )
        {
            _LOG.debug( "Applied: force=["
                    + force
                    + "] torque=["
                    + torque
                    + "]" );
        }
        
        _LOG.trace( "Leaving execute()" );
        
        throw new NotYetImplementedException();
    }

}
