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
package edu.snu.csne.forage.decision;

import java.util.Map;

// Imports
import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.forage.SimulationState;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public interface ProbabilityDecisionCalculator
{
    /**
     * Initialize this agent decision calculator
     *
     * @param simState The state of the simulation
     */
    public void initialize( SimulationState simState );
    
    /**
     * Calculate the probability of a given navigation decision
     *
     * @param patch The patch to which the agent would navigate
     * @param agent The agent making the decision
     * @return The probability
     */
    public float calculateNavigateProbability( Patch patch, Agent agent );
    
    /**
     * Calculate the probability of a given follow decision
     *
     * @param leader The leader which the agent would follow
     * @param agent The agent making the decision
     * @return The probability
     */
    public float calculateFollowProbability( Agent leader, Agent agent );

    /**
     * Calculate all the probabilities that a given agent forages in the
     * patches it currently is in
     * 
     * @param agent The agent making the decision
     * @return The probabilities for foraging at each patch indexed by patch ID
     */
    public Map<String, Float> calculatePatchForageProbabilities( Agent agent );
}
