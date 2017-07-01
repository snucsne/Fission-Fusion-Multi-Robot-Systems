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
package edu.snu.csne.mates.agent.parameter;

public enum ParameterKey
{
    /**********************************************
     * Sensor variable ID keys
     **********************************************/
    /* Priority variable IDs */
    SENSOR_PRIORITY_VAR_ID,
    SENSOR_INPUT_PRIORITY_VAR_ID,
    SENSOR_OUTPUT_PRIORITY_VAR_ID,

    /* Misc variable ID keys */
    SENSOR_DISTANCE_VAR_ID,
    SENSOR_STRENGTH_VAR_ID,
    SENSOR_POSITION_VAR_ID,
    SENSOR_FAILURE_VAR_ID,

    /* Speed variable ID keys */
    SENSOR_SPEED_VAR_ID,
    SENSOR_SPEED_DIFF_VAR_ID,

    /* Direction variable ID keys */
    SENSOR_THETA_DIR_VAR_ID,
    SENSOR_THETA_DIR_ERROR_VAR_ID,
    SENSOR_THETA_DIR_DELTA_VAR_ID,
    SENSOR_THETA_DIR_ERROR_DELTA_VAR_ID,
    SENSOR_PHI_DIR_VAR_ID,
    SENSOR_PHI_DIR_ERROR_VAR_ID,
    SENSOR_PHI_DIR_DELTA_VAR_ID,
    SENSOR_PHI_DIR_ERROR_DELTA_VAR_ID,
    SENSOR_MAX_DIR_DELTA_VAR_ID,

    /* Time variable ID keys */
    SENSOR_ARRIVAL_TIME_VAR_ID,

    /* Collision variable ID keys */
    SENSOR_TIME_TILL_COLLISION_VAR_ID,
    SENSOR_COLLISION_DISTANCE_VAR_ID,
    SENSOR_COLLISION_THETA_DIR_VAR_ID,
    SENSOR_COLLISION_THETA_DIR_DELTA_VAR_ID,
    SENSOR_COLLISION_PHI_DIR_VAR_ID,
    SENSOR_COLLISION_PHI_DIR_DELTA_VAR_ID,
    SENSOR_COLLISION_MAX_DIR_DELTA_VAR_ID,

    
    /**********************************************
     * Sensor non-variable ID keys
     **********************************************/

    
    
    /**********************************************
     * Behavior variable ID keys
     **********************************************/

    /* Generic behavior keys */
    BEHAVIOR_WEIGHT_VAR_ID,
    BEHAVIOR_THRESHOLD_VAR_ID,

    /* Fuzzy behavior keys */
    BEHAVIOR_FUZZY_RULESET_DEF_ID,

    /**********************************************
     * Blackboard keys
     **********************************************/
    BB_MIN_SAFE_DISTANCE,
    BB_MAX_ALLOWABLE_DISTANCE,
    BB_MAX_TIME_TO_COLLISION,
    BB_MAX_COLLISION_DISTANCE,
    BB_GOAL_POSITION,
    BB_GOAL_SEEK_MAX_DISTANCE;

}
