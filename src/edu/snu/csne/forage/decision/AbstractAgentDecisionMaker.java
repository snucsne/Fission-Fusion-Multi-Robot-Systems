/*
 * COPYRIGHT
 */
package edu.snu.csne.forage.decision;

// Imports
import java.util.Properties;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.snu.csne.forage.SimulationState;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public abstract class AbstractAgentDecisionMaker implements AgentDecisionMaker
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            AbstractAgentDecisionMaker.class.getName() );
    
    /** The current simulation state */
    protected SimulationState _simState = null;
    
    
    /**
     * Initialize this agent decision-maker
     *
     * @param simState The state of the simulation
     * @param props The configuration properties
     * @see edu.snu.csne.forage.decision.AgentDecisionMaker#initialize(edu.snu.csne.forage.SimulationState, java.util.Properties)
     */
    @Override
    public void initialize( SimulationState simState, Properties props )
    {
        _LOG.trace( "Entering initialize( simState, props )" );

        // Validate and store the simulation state
        Validate.notNull( simState, "Simulation state may not be null" );
        _simState = simState;
        
        _LOG.trace( "Leaving initialize( simState, props )" );
    }

}
