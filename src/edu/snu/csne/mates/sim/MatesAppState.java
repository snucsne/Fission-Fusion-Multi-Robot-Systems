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
package edu.snu.csne.mates.sim;

// Imports
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import edu.snu.csne.mates.agent.Agent;
import edu.snu.csne.mates.sim.event.SimUpdateEvent;
import edu.snu.csne.mates.sim.event.SimulatorEventListener;
import edu.snu.csne.mates.util.NotYetImplementedException;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class MatesAppState extends AbstractAppState
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            MatesAppState.class.getName() );

    
    /** The simulation state */
    private SimulationState _simState = null;
    
    /** The list of event listeners for this engine */
    private List<SimulatorEventListener> _listeners =
            new LinkedList<SimulatorEventListener>();

    
    /**
     * Builds this MatesAppState object
     *
     * @param simState
     */
    public MatesAppState( SimulationState simState )
    {
        // Validate and save the simulation state
        Validate.notNull( simState, "Simulation state may not be null" );
        _simState = simState;
    }
    
    /**
     * Called by AppStateManager when transitioning this AppState from
     * initializing to running.
     * 
     * @param stateManager
     * @param app
     * @see com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application)
     */
    @Override
    public void initialize( AppStateManager stateManager, Application app )
    {
        _LOG.trace( "Entering initialize( stateManager, app )" );

        // Call the superclass implementation
        super.initialize( stateManager, app );
        
        // Create a control for each agent
        // TODO
        
        _LOG.trace( "Leaving initialize( stateManager, app )" );
    }

    /**
     * Called to update the AppState.
     *
     * @param tpf
     * @see com.jme3.app.state.AbstractAppState#update(float)
     */
    @Override
    public void update( float tpf )
    {
        // Notify all the listeners
        if( _listeners.size() > 0 )
        {
            SimUpdateEvent evt = new SimUpdateEvent( _simState );
            Iterator<SimulatorEventListener> iter = _listeners.iterator();
            while( iter.hasNext() )
            {
                iter.next().updateStarted( evt );
            }
        }

        // Execute all the agents
        List<Agent> agents = _simState.getAgentLibrary().getAllAgents();
        Iterator<Agent> agentIter = agents.iterator();
        while( agentIter.hasNext() )
        {
            // Get the next agent
            Agent current = agentIter.next();
            
            // Is it active?
            if( current.isActive() )
            {
                // TODO Put in collision detection code?
                // Yup
                current.execute();
            }
        }
        
        // Notify all the listeners
        if( _listeners.size() > 0 )
        {
            SimUpdateEvent evt = new SimUpdateEvent( _simState );
            Iterator<SimulatorEventListener> iter = _listeners.iterator();
            while( iter.hasNext() )
            {
                iter.next().updateFinished( evt );
            }
        }
    }

    /**
     * Called by AppStateManager when transitioning this AppState from
     * terminating to detached.
     *
     * @see com.jme3.app.state.AbstractAppState#cleanup()
     */
    @Override
    public void cleanup()
    {
        _LOG.trace( "Entering cleanup()" );

        // Call the superclass implementation
        super.cleanup();

        _LOG.trace( "Leaving cleanup()" );
    }

    /**
     * Enable or disable the functionality of the AppState.
     *
     * @param enabled
     * @see com.jme3.app.state.AbstractAppState#setEnabled(boolean)
     */
    @Override
    public void setEnabled( boolean enabled )
    {
        _LOG.trace( "Entering setEnabled( enabled )" );

        // Call the superclass implementation
        super.setEnabled( enabled );
        
        if( enabled )
        {
            // TODO
            throw new NotYetImplementedException();
        }
        else
        {
            // TODO
            throw new NotYetImplementedException();
        }

//        _LOG.trace( "Leaving setEnabled( enabled )" );
    }


}
