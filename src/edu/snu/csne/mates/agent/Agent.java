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
package edu.snu.csne.mates.agent;

// Imports
import edu.snu.csne.mates.agent.module.AgentModule;
import edu.snu.csne.mates.sim.SimulationState;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public interface Agent extends Comparable<Agent>
{
    /**
     * Adds a module to this agent
     *
     * @param module The agent module to add
     * @param rank The rank of the module with respect to the other modules
     */
    public void addModule( AgentModule module, int rank );
    
    /**
     * Returns the unique ID of this agent
     *
     * @return The unique ID
     */
    public Object getID();
    
    /**
     * Returns the name of this agent
     *
     * @return The name
     */
    public String getName();
    
    /**
     * Returns the full name of this agent
     * 
     * @return The full name
     */
    public String getFullName();

    /**
     * Returns the team of this agent
     *
     * @return The team
     */
    public AgentTeam getTeam();
    
    /**
     * Returns the blackboard for this agent
     *
     * @return The blackboard
     */
    public AgentBlackboard getBlackboard();
    
    /**
     * Initializes this agent
     */
    public void initialize( SimulationState simState );
    
    /**
     * Executes this agent
     */
    public void execute();
    
    /**
     * Destroys this agent
     */
    public void destroy();
    
    /**
     * Indicates whether or not the agent is active
     *
     * @return <code>true</code> if the agent is active, otherwise,
     * <code>false</code>
     */
    public boolean isActive();
    
    /**
     * Indicates whether or not the agent has been initialized
     *
     * @return <code>true</code> if the agent has been initialized, otherwise,
     * <code>false</code>
     */
    public boolean isInitialized();
}
