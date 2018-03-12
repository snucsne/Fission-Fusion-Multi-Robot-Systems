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
package edu.snu.csne.forage;

// Imports
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jme3.math.Vector3f;
import ec.util.MersenneTwisterFast;
import edu.snu.csne.forage.decision.AgentDecisionMaker;
import edu.snu.csne.forage.event.DecisionEvent;
import edu.snu.csne.forage.event.SimulationEventListener;
import edu.snu.csne.forage.sensor.AgentSensor;
import edu.snu.csne.forage.sensor.PatchSensor;
import edu.snu.csne.forage.util.PatchValueCalculator;
import edu.snu.csne.util.MiscUtils;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class SimulationState
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            SimulationState.class.getName() );
    
    /** Key for the the random number seed */
    private static final String _RANDOM_SEED_KEY = "random-seed";

    /** Key for the number of times to run the simulator */
    private static final String _SIMULATION_COUNT_KEY = "simulation-count";

    /** Key for the number of steps for each simulation */
    private static final String _SIMULATION_STEP_COUNT_KEY = "simulation-step-count";
    
    /** Key for the agent sensor class */
    private static final String _AGENT_SENSOR_CLASS_KEY = "agent-sensor-class";
    
    /** Key for the patch sensor class */
    private static final String _PATCH_SENSOR_CLASS_KEY = "patch-sensor-class";
    
    /** Key for the decision-maker class */
    private static final String _DECISION_MAKER_CLASS_KEY = "decision-maker-class";
    
    /** Key for the agent properties file */
    private static final String _AGENT_PROPS_FILE_KEY = "agent-properties-file";
    
    /** Key for the patch properties file */
    private static final String _PATCH_PROPS_FILE_KEY = "patch-properties-file";
    
    
    /** Key for the number of agents */
    private static final String _AGENT_COUNT_KEY = "agent-count";
    
    /** Key prefix for agents */
    private static final String _AGENT_PREFIX_KEY = "agent";

    /** Key for the agent's maximum speed */
    private static final String _MAX_SPEED_KEY = "max-speed";
    
    /** Key for the agent's maximum force */
    private static final String _MAX_FORCE_KEY = "max-force";
    
    /** Key for the agent's arrival scale distance */
    private static final String _ARRIVAL_SCALE_DISTANCE_KEY = "arrival-scale-distance";
    
    /** Key for the agent's minimum separation */
    private static final String _MIN_SEPARATION_KEY = "min-separation";

    /** Key for the agent's desired separation */
    private static final String _DESIRED_SEPARATION_KEY = "desired-separation";
    
    /** Key for the agent's max foraging area */
    private static final String _MAX_FORAGING_AREA_KEY = "max-foraging-area";
    
    /** Key for the number of patches */
    private static final String _PATCH_COUNT_KEY = "patch-count";
    
    /** Key for the resource consumption rate */
    private static final String _RESOURCE_CONSUMPTION_RATE_KEY =
            "resource-consumption-rate";

    /** Key for the resource consumption max */
    private static final String _RESOURCE_CONSUMPTION_MAX_KEY =
            "resource-consumption-max";

    /** Key prefix for patches */
    private static final String _PATCH_PREFIX_KEY = "patch";
    
    /** Key postfix for a position */
    private static final String _POSITION_KEY = "position";
    
    /** Key postfix for a velocity */
    private static final String _VELOCITY_KEY = "velocity";

    /** Key postfix for a radius value */
    private static final String _RADIUS_KEY = "radius";

    /** Key postfix for a resources value */
    private static final String _RESOURCES_KEY = "resources";
    
    /** Key postfix for a predation probability value */
    private static final String _PREDATION_PROBABILITY_KEY = "predation-probability";
    
    /** Key postfix for a minimum agent forage count value */
    private static final String _MIN_AGENT_FORAGE_COUNT_KEY = "min-agent-forage-count";
    
    /** Key postfix for a team value */
    private static final String _TEAM_KEY = "team";
    
    /** Key format string for an agent index */
    private static final String _AGENT_INDEX_FORMAT_STR = "%03d";
    
    /** Key format string for a patch index */
    private static final String _PATCH_INDEX_FORMAT_STR = "%02d";

    /** Key for the number of event listeners */
    private static final String _EVENT_LISTENER_COUNT_KEY = "event-listener-count";
    
    /** Key prefix for event listeners */
    private static final String _EVENT_LISTENER_PREFIX_KEY = "event-listener";
    
    /** Key postfix for a class */
    private static final String _EVENT_LISTENER_CLASS_POSTFIX_KEY = "class";
    
    /** Key format string for event listener indices */
    private static final String _EVENT_LISTENER_INDEX_FORMAT_STR = "%02d";

    /** Key for the flag denoting whether or not agent's have an initial velocity */
    private static final String _ALLOW_INITIAL_VELOCITY_KEY = "allow-initial-velocity";
    
    
    /** The simulation properties */
    private Properties _props = null;

    /** Random number generator */
    private MersenneTwisterFast _random = null;

    /** The number of times to run the simulator */
    private int _simulationRunCount = 0;

    /** The number of simulation steps to perform */
    private long _simulationRunStepCount = 0;

    /** The current simulation run */
    private int _currentSimulationRun = 0;

    /** The current simulation step */
    private long _currentSimulationStep = 0;

    /** Number of new teams created */
    private int _newTeamCount = 0;


    /** All the agents in the simulation */
    private Map<String,Agent> _agents = new HashMap<String,Agent>();
    
    /** All the teams in the simulation */
    private Map<String,AgentTeam> _teams = new HashMap<String,AgentTeam>();
    
    /** All the food patches in the simulation */
    private Map<String,Patch> _patches = new HashMap<String,Patch>();
    
    /** All the event listeners */
    private List<SimulationEventListener> _listeners =
            new LinkedList<SimulationEventListener>();

    
    /**
     * Initialize the simulation state
     *
     * @param props
     */
    public void initialize( Properties props )
    {
        _LOG.trace( "Entering initialize( props )" );

        // (Re)Set some values
        _currentSimulationRun = 0;
        _currentSimulationStep = 0;
        _newTeamCount = 0;
        
        // Save the properties
        _props = props;

        // Get the random number generator seed
        String randomSeedStr = props.getProperty( _RANDOM_SEED_KEY );
        Validate.notEmpty( randomSeedStr, "Random seed is required" );
        long seed = Long.parseLong( randomSeedStr );
        _random = new MersenneTwisterFast( seed );
        _LOG.debug( "seed=["
                + seed
                + "]" );

        // Get the simulation count
        _simulationRunCount = MiscUtils.loadNonEmptyIntegerProperty( _props,
                _SIMULATION_COUNT_KEY,
                "Simulation count " );
        _LOG.debug( "_simulationRunCount=["
                + _simulationRunCount
                + "]" );
        
        // Get the simulation step count
        _simulationRunStepCount = MiscUtils.loadNonEmptyIntegerProperty( _props,
                _SIMULATION_STEP_COUNT_KEY,
                "Simulation step count " );
        _LOG.debug( "_simulationRunStepCount=["
                + _simulationRunStepCount
                + "]" );

        /* Create the food patches first so agent decision makers can have
         * access to them if needed */
        createPatches();

        // Create the agents
        createAgents();
                
        // Load all the event listeners
        int eventListenerCount = MiscUtils.loadNonEmptyIntegerProperty(
                _props,
                _EVENT_LISTENER_COUNT_KEY,
                "Event listener count " );
        _LOG.debug( "eventListenerCount=["
                + eventListenerCount
                + "]" );
        for( int i = 0; i < eventListenerCount; i++ )
        {
            // Get the class name
            String formattedIdx = String.format( _EVENT_LISTENER_INDEX_FORMAT_STR, i );
            String key = _EVENT_LISTENER_PREFIX_KEY
                    + "."
                    + formattedIdx
                    + "."
                    + _EVENT_LISTENER_CLASS_POSTFIX_KEY;
            String eventListenerClassName = _props.getProperty( key );
            Validate.notEmpty( eventListenerClassName,
                    "Event listener name for index ["
                    + formattedIdx
                    + "] may not be null or empty" );
            _LOG.debug( "key=["
                    + eventListenerClassName
                    + "]" );
            SimulationEventListener eventListener = (SimulationEventListener)
                    MiscUtils.loadAndInstantiate( eventListenerClassName,
                            "Event listener class is required - specified class is not valid ["
                            + eventListenerClassName
                            + "]" );
            
            // Add it
            addEventListener( eventListener );
        }

        
        _LOG.trace( "Leaving initialize( props )" );
    }

    /**
     * TODO Method description
     *
     * @param listener
     */
    public void addEventListener( SimulationEventListener listener )
    {
        // Initialize it
        listener.initialize( this );
        
        // Add it to the list
        _listeners.add( listener );
    }
    
    /**
     * Determines if the simulations are finished
     *
     * @return <code>true</code> if the simulations are finished, otherwise
     * <code>false</code>
     */
    public boolean isSimFinished()
    {
        return _currentSimulationRun >= _simulationRunCount;
    }
    
    /**
     * Determines if the current simulation run is finished
     *
     * @return <code>true</code> if the simulation run is finished, otherwise
     * <code>false</code>
     */
    public boolean isRunFinished()
    {
        return _currentSimulationStep >= _simulationRunStepCount;
    }
    
    /**
     * Returns all the agents in the simulation
     *
     * @return All the agents
     */
    public Map<String,Agent> getAllAgents()
    {
        return new HashMap<String,Agent>( _agents );
    }
    
    /**
     * Returns an agent specified by their unique ID
     *
     * @param id The agent's unique ID
     * @return The agent
     */
    public Agent getAgent( String id )
    {
        Agent agent = _agents.get( id );
        Validate.notNull( agent, "No agent with id=["
                + id
                + "] was found" );
        
        return agent;
    }
    
    /**
     * Returns all the teams in the simulation
     * 
     * @return All the teams
     */
    public Map<String,AgentTeam> getAllTeams()
    {
        return new HashMap<String,AgentTeam>( _teams );
    }
    
    /**
     * Returns all the active teams in the simulation
     *
     * @return All the active teams
     */
    public Map<String,AgentTeam> getAllActiveTeams()
    {
        Map<String,AgentTeam> activeTeams = new HashMap<String,AgentTeam>(); 
        Iterator<AgentTeam> teamIter = getAllTeams().values().iterator();
        while( teamIter.hasNext() )
        {
            AgentTeam current = teamIter.next();
            if( current.isActive() )
            {
                activeTeams.put( current.getID(), current );
            }
        }
        
        return activeTeams;
    }
    
    /**
     * Returns the team specified by the ID.  If the team does not exist,
     * it can create it.
     *
     * @param id The unique id
     * @param Flag indiicating whether or not to create the team if it
     * doesn't exist
     * @return The team
     */
    public AgentTeam getTeam( String id, boolean createIfNotExist )
    {
        // Try to get the team
        AgentTeam team = _teams.get( id );
        
        // Does it exist?
        if( null == team )
        {
            // Nope.  DO we create it?
            if( createIfNotExist )
            {
                team = new AgentTeam( id );
                _teams.put( id, team );
                _LOG.debug( "Created team with ID ["
                        + id
                        + "]" );
            }
            else
            {
                // Nope
                throw new IllegalArgumentException( "No team with id=["
                        + id
                        + "] was found" );
            }
        }
        
        return team;
    }
    
    /**
     * Creates a new team
     *
     * @return The newly created team
     */
    public AgentTeam createNewTeam()
    {
        // Generate an ID
        String id = "GenTeam" + String.format( "%05d", ++_newTeamCount );
        
        // Build the team
        AgentTeam team = new AgentTeam( id );
        
        return team;
    }
    
    /**
     * Returns all the food patches in the simulation
     *
     * @return All the food patches
     */
    public Map<String,Patch> getAllPatches()
    {
        return new HashMap<String,Patch>( _patches );
    }
    
    /**
     * Returns a patch psecified by its unique ID
     *
     * @param id The patch'es unique ID
     * @return The patch
     */
    public Patch getPatch( String id )
    {
        Patch patch = _patches.get( id );
        Validate.notNull( patch, "No patch with id=["
                + id
                + "] was found" );
        return patch;
    }
    
    /**
     * Resets the simulation state
     */
    public void reset()
    {
        _currentSimulationStep = - 1;
        _agents.clear();
        _patches.clear();
    }
    
    /**
     * Signal all the listeners that the simulator is about to start
     */
    public void signalSimSetup()
    {
        // Iterate through all the event listeners
        Iterator<SimulationEventListener> iter = _listeners.iterator();
        while( iter.hasNext() )
        {
            // Send the signal
            iter.next().simSetup();
        }
    }

    /**
     * Signal all the listeners that a simulation run is about to start
     */
    public void signalSimRunSetup()
    {
        // Update the current simulation run
        _currentSimulationRun++;
        
        // Iterate through all the event listeners
        Iterator<SimulationEventListener> iter = _listeners.iterator();
        while( iter.hasNext() )
        {
            // Send the signal
            iter.next().simRunSetup();
        }
    }

    /**
     * Signal all the listeners that a simulation step is about to be executed
     */
    public void signalSimStepSetup()
    {
        // Update the current simulation run step
        _currentSimulationStep++;
        
        // Iterate through all the event listeners
        Iterator<SimulationEventListener> iter = _listeners.iterator();
        while( iter.hasNext() )
        {
            // Send the signal
            iter.next().simStepSetup();
        }
    }

    /**
     * Signal all the listeners that a simulation step finished
     */
    public void signalSimStepTeardown()
    {
        // Iterate through all the event listeners
        Iterator<SimulationEventListener> iter = _listeners.iterator();
        while( iter.hasNext() )
        {
            // Send the signal
            iter.next().simStepTearDown();
        }
    }

    /**
     * Signal all the listeners that a simulation run has finished
     */
    public void signalSimRunTeardown()
    {
        // Iterate through all the event listeners
        Iterator<SimulationEventListener> iter = _listeners.iterator();
        while( iter.hasNext() )
        {
            // Send the signal
            iter.next().simRunTearDown();
        }
    }

    /**
     * Signal all the listeners that a simulation has finished
     */
    public void signalSimTeardown()
    {
        // Iterate through all the event listeners
        Iterator<SimulationEventListener> iter = _listeners.iterator();
        while( iter.hasNext() )
        {
            // Send the signal
            iter.next().simTearDown();
        }
    }

    /**
     * Signal all the listeners that the simulator is shutting down and they
     * should clean up
     */
    public void signalCleanup()
    {
        // Iterate through all the event listeners
        Iterator<SimulationEventListener> iter = _listeners.iterator();
        while( iter.hasNext() )
        {
            // Send the signal
            iter.next().cleanup();
        }
    }

    /**
     * Signal all the listeners that an agent made a decision
     */
    public void signalAgentDecision( DecisionEvent event )
    {
        // Iterate through all the event listeners
        Iterator<SimulationEventListener> iter = _listeners.iterator();
        while( iter.hasNext() )
        {
            // Send the signal
            iter.next().agentDecided( event );
        }
    }
    
    /**
     * Builds the next new team ID
     *
     * @return A new team ID
     */
    public String buildNextNewTeamID()
    {
        return "Team" + String.format( "%02d", (_newTeamCount++) + 1 );
    }

    
    /**
     * Returns the props for this object
     *
     * @return The props
     */
    public Properties getProps()
    {
        return _props;
    }

    /**
     * Sets the simulation run
     *
     * @param currentSimulationRun The specified simulation run
     */
    public void setCurrentSimulationRun( int currentSimulationRun )
    {
        _currentSimulationRun = currentSimulationRun;
    }

    /**
     * Returns the current simulation run
     *
     * @return The current simulation run
     */
    public int getCurrentSimulationRun()
    {
        return _currentSimulationRun;
    }

    /**
     * Returns the currentSimulationStep for this object
     *
     * @return The currentSimulationStep
     */
    public long getCurrentSimulationStep()
    {
        return _currentSimulationStep;
    }

    /**
     * Returns the simulationCount for this object
     *
     * @return The simulationCount
     */
    public int getSimulationRunCount()
    {
        return _simulationRunCount;
    }

    public MersenneTwisterFast getRNG()
    {
        return _random;
    }
    
    /**
     * Create all the agents used in the simulation
     */
    private void createAgents()
    {
        _LOG.trace( "Entering createAgents()" );
        
        // (Re)Create the agent map
        _agents = new HashMap<String,Agent>();
        
        // Get the max speed
        float maxSpeed = MiscUtils.loadNonEmptyFloatProperty( _props,
                _MAX_SPEED_KEY,
                "Maximum speed is required " );
        _LOG.debug( "maxSpeed=[" + maxSpeed + "]" );
        
        // Get the max force
        float maxForce = MiscUtils.loadNonEmptyFloatProperty( _props,
                _MAX_FORCE_KEY,
                "Maximum force is required " );
        _LOG.debug( "maxForce=[" + maxForce + "]" );

        // Get the arrival scale distance
        float arrivalScaleDistance = MiscUtils.loadNonEmptyFloatProperty( _props,
                _ARRIVAL_SCALE_DISTANCE_KEY,
                "Arrival scale distance is required " );
        _LOG.debug( "arrivalScaleDistance=[" + arrivalScaleDistance + "]" );

        // Get the minimum separation
        float minSeparation = MiscUtils.loadNonEmptyFloatProperty( _props,
                _MIN_SEPARATION_KEY,
                "Minimum separation is required " );
        _LOG.debug( "minSeparation=[" + minSeparation + "]" );

        // Get the desired separation
        float desiredSeparation = MiscUtils.loadNonEmptyFloatProperty( _props,
                _DESIRED_SEPARATION_KEY,
                "Desired separation is required " );
        _LOG.debug( "desiredSeparation=[" + desiredSeparation + "]" );

        // Get the resource consumption rate
        float resourceConsumptionRate = MiscUtils.loadNonEmptyFloatProperty( _props,
                _RESOURCE_CONSUMPTION_RATE_KEY,
                "Resource consumption rate is required " );
        _LOG.debug( "resourceConsumptionRate=[" + resourceConsumptionRate + "]" );

        // Get the resource consumption rate
        float resourceConsumptionMax = MiscUtils.loadNonEmptyFloatProperty( _props,
                _RESOURCE_CONSUMPTION_MAX_KEY,
                "Resource consumption rate is required " );
        _LOG.debug( "resourceConsumptionMax=[" + resourceConsumptionMax + "]" );
        
        // Get the max foraging area
        float maxForagingArea = MiscUtils.loadNonEmptyFloatProperty( _props,
                _MAX_FORAGING_AREA_KEY,
                "Resource consumption rate is required " );
        _LOG.debug( "maxForagingArea=[" + maxForagingArea + "]" );
        
        // Do we allow initial velocities?
        boolean allowInitialVelocity = MiscUtils.loadNonEmptyBooleanProperty(
                _props,
                _ALLOW_INITIAL_VELOCITY_KEY,
                "Allow initial velocity" );
        
        // Get the agent sensor class
        String agentSensorClassName = _props.getProperty( _AGENT_SENSOR_CLASS_KEY );
        AgentSensor agentSensor = (AgentSensor) MiscUtils.loadAndInstantiate(
                agentSensorClassName,
                "Agent sensor class is required - specified class is not valid ["
                + agentSensorClassName
                + "]" );
        agentSensor.initialize( this, _props );
        
        // Get the food patch sensor class
        String patchSensorClassName = _props.getProperty( _PATCH_SENSOR_CLASS_KEY );
        PatchSensor patchSensor = (PatchSensor) MiscUtils.loadAndInstantiate(
                patchSensorClassName,
                "Patch sensor class is required - specified class is not valid ["
                + patchSensorClassName
                + "]" );
        patchSensor.initialize( this, _props );

        // Get the decision maker class
        String decisionMakerClassName = _props.getProperty( _DECISION_MAKER_CLASS_KEY );
        AgentDecisionMaker decisionMaker = (AgentDecisionMaker) MiscUtils.loadAndInstantiate(
                decisionMakerClassName,
                "Agent decision maker class is required - specified class is not valid ["
                + decisionMakerClassName
                + "]" );
        decisionMaker.initialize( this, _props );

        // Get the agent configuration properties
        String agentPropertiesFile = _props.getProperty( _AGENT_PROPS_FILE_KEY );
        Validate.notEmpty( agentPropertiesFile,
                "Agent properties file may not be empty ["
                + agentPropertiesFile
                + "]" );
        
        PatchValueCalculator patchValueCalc = new PatchValueCalculator();
        patchValueCalc.initialize( this );
        
        // Load the properties
        Properties agentProps = MiscUtils.loadPropertiesFromFile( agentPropertiesFile );
        
        // Process them
        int agentCount = MiscUtils.loadNonEmptyIntegerProperty( agentProps,
                _AGENT_COUNT_KEY,
                "Agent count " );
        for( int i = 0; i < agentCount; i++ )
        {
            // Build the id
            String formattedIdx = String.format( _AGENT_INDEX_FORMAT_STR, i );
            String id = "Agent" + formattedIdx;
            
            // Build the properties prefix
            String prefix = _AGENT_PREFIX_KEY
                    + "."
                    + formattedIdx
                    + ".";
            
            // Get the position
            Vector3f position = MiscUtils.loadNonEmptyVector3fProperty( agentProps,
                    prefix + _POSITION_KEY,
                    "Agent [" + formattedIdx + "] position " );
            
            // Get the velocity
            Vector3f velocity = new Vector3f();
            if( allowInitialVelocity )
            {
                velocity = MiscUtils.loadNonEmptyVector3fProperty( agentProps,
                    prefix + _VELOCITY_KEY,
                    "Agent [" + formattedIdx + "] velocity " );
            }

            // Get the team
            String teamID = agentProps.getProperty( prefix + _TEAM_KEY );
            Validate.notEmpty( teamID, "Team id may not be null or empty" );
            
            // Get the team from the library
            AgentTeam team = getTeam( teamID, true );
            
            // Create the agent
            Agent agent = new Agent( id,
                    position,
                    velocity,
                    team,
                    resourceConsumptionRate,
                    resourceConsumptionMax,
                    maxSpeed,
                    maxForce,
                    arrivalScaleDistance,
                    minSeparation,
                    desiredSeparation,
                    maxForagingArea,
                    agentSensor,
                    patchSensor,
                    decisionMaker,
                    patchValueCalc,
                    this );
            
            // Add it to the map
            _agents.put( id, agent );
            
            // Add it to its team
            team.join( agent );
        }
        
        _LOG.trace( "Leaving createAgents()" );
    }

    /**
     * Create all the food paches in all the simulations
     */
    private void createPatches()
    {
        _LOG.trace( "Entering createPatches()" );
        
        // (Re)create the patch list
        _patches = new HashMap<String, Patch>();
        
        // Get the patch configuration properties
        String patchPropertiesFile = _props.getProperty( _PATCH_PROPS_FILE_KEY );
        Validate.notEmpty( patchPropertiesFile,
                "Patch properties file may not be empty ["
                + patchPropertiesFile
                + "]" );

        // Load the properties
        Properties patchProps = MiscUtils.loadPropertiesFromFile( patchPropertiesFile );

        // Get the number of patches
        int patchCount = MiscUtils.loadNonEmptyIntegerProperty( patchProps,
                _PATCH_COUNT_KEY,
                "Patch count " );
        _LOG.debug( "Loading [" + patchCount + "] patches" );

        for( int i = 0; i < patchCount; i++ )
        {
            // Build the id
            String formattedIdx = String.format( _PATCH_INDEX_FORMAT_STR, i );
            String id = "Patch" + formattedIdx;
            
            // Build the properties prefix
            String prefix = _PATCH_PREFIX_KEY
                    + "."
                    + formattedIdx
                    + ".";
            
            // Get the position
            Vector3f position = MiscUtils.loadNonEmptyVector3fProperty( patchProps,
                    prefix + _POSITION_KEY,
                    "Patch [" + formattedIdx + "] position " );
            
            // Get the radius
            float radius = MiscUtils.loadNonEmptyFloatProperty( patchProps,
                    prefix + _RADIUS_KEY,
                    "Patch [" + formattedIdx + "] radius" );
            
            // Get the amount of resources
            float resources = MiscUtils.loadNonEmptyFloatProperty( patchProps,
                    prefix + _RESOURCES_KEY,
                    "Patch [" + formattedIdx + "] resources" );
            
            // Get the radius
            float predationProbability = MiscUtils.loadNonEmptyFloatProperty( patchProps,
                    prefix + _PREDATION_PROBABILITY_KEY,
                    "Patch [" + formattedIdx + "] predation probability " );

            // Get the radius
            int minAgentForageCount = MiscUtils.loadNonEmptyIntegerProperty( patchProps,
                    prefix + _MIN_AGENT_FORAGE_COUNT_KEY,
                    "Patch [" + formattedIdx + "] min agent forage count " );

            // Create the patch and store it
            Patch patch = new Patch( id,
                    position,
                    radius,
                    resources,
                    predationProbability,
                    minAgentForageCount );
            _patches.put( id, patch );
            
            _LOG.debug( "Created patch [" + id + "]" );
        }

        _LOG.trace( "Leaving createPatches()" );
    }

}
