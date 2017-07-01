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

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public enum AgentBlackBoardDataKey
{
    /* General simulator-wide data keys */
    WORLD_OBJ_MIN_SAFE_DISTANCE,
    
    MAX_TIME_TO_COLLISION,
    MAX_COLLISION_DISTANCE,
    
    /* Agent sensor data keys */
    SENSED_AGENT_OBJECTS,
    
    SENSED_COLLIDABLE_OBJECTS,
    SENSED_NAVIGATION_OBJECTS,
    SENSED_WORLD_OBJECTS,
    SENSED_WORLD_OBJECTS_MAP,
    
    SENSED_TEAMMATES,
    SENSED_TEAMMATES_MEAN_POSITION,
    SENSED_TEAMMATES_MEAN_VELOCITY,
    SENSED_TEAMMATES_MEAN_ORIENTATION,
    SENSED_TEAMMATES_MAX_RANGE,
    
    /* Behavior specific data keys */
    BEHAVIOR_MODULATION_WEIGHT_SUM,
    
    // Goal seek data keys
    GOAL_SEEK_MAX_DISTANCE,
    GOAL_POSITION,
    GOAL_THETA_DIR_DELTA,
    
    CURRENT_CLOSEST_COLLISION_TIME
    
    
}
