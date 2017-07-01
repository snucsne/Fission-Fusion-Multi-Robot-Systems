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

//Imports
import java.util.Map;
import edu.snu.csne.mates.agent.Agent;
import edu.snu.csne.mates.agent.FloatVariable;
import edu.snu.csne.mates.agent.parameter.Parameter;
import edu.snu.csne.mates.agent.parameter.ParameterKey;
import edu.snu.csne.mates.sim.SimulationState;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public interface Behavior
{
    /**
     * Returns the unique identifier of this behavior
     *
     * @return The unique identifier
     */
    public Object getID();

    /**
     * Stores the parameters for this behavior 
     *
     * @param params The parameters
     */
    public void storeParameters( Map<ParameterKey, Parameter> params );

    /**
     * Initializes this behavior.
     *
     * @param agent The agent to which this behavior belongs
     * @param simState The current state of the simulation
     */
    public void initialize( Agent agent, SimulationState simState );

    /**
     * Executes this behavior.
     */
    public void execute();

    /**
     * Returns the weight for this behavior.
     *
     * @return The weight as a FloatVariable
     */
    public FloatVariable getWeight();

    /**
     * Returns the threshold for this behavior.
     *
     * @return The threshold has a FloatVariable
     */
    public FloatVariable getThreshold();

    /**
     * Determines if this behavior's weight is above the threshold and,
     * therefore, active.
     *
     * @return <code>true</code> if the behavior is active, otherwise,
     * <code>false</code>
     */
    public boolean isActive();

    /**
     * Destroys this behavior.
     */
    public void destroy();

}
