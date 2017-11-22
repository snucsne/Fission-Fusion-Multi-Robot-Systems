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
package edu.snu.csne.forage.event;

// Imports
import java.awt.Color;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.util.MiscUtils;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class GraphicalPositionEventListener
        extends AbstractSimulationEventListener
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            GraphicalPositionEventListener.class.getName() );
    
    /** Key prefix for the display */
    private static final String _DISPLAY_PREFIX_KEY = "display";
    
    /** Key for the display height */
    private static final String _HEIGHT_KEY = "height";
    
    /** Key for the display width */
    private static final String _WIDTH_KEY = "width";
    
    /** Key for the background color */
    private static final String _BG_COLOR_KEY = "bg-color";
    
    /** Key for the grid color */
    private static final String _GRID_COLOR_KEY = "grid-color";
    
    /** Key for the agent color */
    private static final String _AGENT_COLOR_KEY = "agent-color";
    
    /** Key for the follower to leader color */
    private static final String _LEADER_LINE_COLOR_KEY = "leader-line-color";

    /** Key for the grid step size */
    private static final String _GRID_STEP_KEY = "grid-step";
    
    /** Key for the display scale */
    private static final String _SCALE_KEY = "scale";
    
    /** Key for the agent drawing size */
    private static final String _AGENT_SIZE = "agent-size";
    
    
    /** The panel displaying all the agents */
    private GraphicalPositionPanel _panel = null;
    
    /**
     * Initializes this event listener
     *
     * @param simState The simulation state
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#initialize(edu.snu.csne.forage.SimulationState)
     */
    @Override
    public void initialize( SimulationState simState )
    {
        _LOG.trace( "Entering initialize( simState )" );

        // Call the superclass implementation
        super.initialize( simState );
        
        // Grab the properties
        Properties props = simState.getProps();
        
        // Get the display configuration
        int width = MiscUtils.loadNonEmptyIntegerProperty( props,
                _DISPLAY_PREFIX_KEY + "." + _WIDTH_KEY,
                "Display width " );
        int height = MiscUtils.loadNonEmptyIntegerProperty( props,
                _DISPLAY_PREFIX_KEY + "." + _HEIGHT_KEY,
                "Display height " );
        int gridStep = MiscUtils.loadNonEmptyIntegerProperty( props,
                _DISPLAY_PREFIX_KEY + "." + _GRID_STEP_KEY,
                "Display grid step " );
        float displayScale = MiscUtils.loadNonEmptyFloatProperty( props,
                _DISPLAY_PREFIX_KEY + "." + _SCALE_KEY,
                "Display scale " );
        float agentDrawSize = MiscUtils.loadNonEmptyFloatProperty( props,
                _DISPLAY_PREFIX_KEY + "." + _AGENT_SIZE,
                "Agent display size " );

        // Get the colors
        String bgColorStr = props.getProperty( _DISPLAY_PREFIX_KEY
                + "."
                + _BG_COLOR_KEY );
        Validate.notEmpty( bgColorStr, "Background color may not be blank" );
        Color bgColor = MiscUtils.parseAWTColor( bgColorStr );
        String gridColorStr = props.getProperty( _DISPLAY_PREFIX_KEY
                + "."
                + _GRID_COLOR_KEY );
        Validate.notEmpty( gridColorStr, "Grid color may not be blank" );
        Color gridColor = MiscUtils.parseAWTColor( gridColorStr );;
        String agentColorStr = props.getProperty( _DISPLAY_PREFIX_KEY
                + "."
                + _AGENT_COLOR_KEY );
        Validate.notEmpty( agentColorStr, "Agent color may not be blank" );
        Color agentColor = MiscUtils.parseAWTColor( agentColorStr );;
        String leaderLineColorStr = props.getProperty( _DISPLAY_PREFIX_KEY
                + "."
                + _LEADER_LINE_COLOR_KEY );
        Validate.notEmpty( leaderLineColorStr, "Leader line color may not be blank" );
        Color leaderLineColor = MiscUtils.parseAWTColor( leaderLineColorStr );;
        
        // Create the graphics panel
        _panel = new GraphicalPositionPanel( width,
                height,
                gridStep,
                bgColor,
                gridColor,
                agentColor,
                leaderLineColor,
                displayScale,
                agentDrawSize,
                simState );
        
        // Create our display
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JFrame frame = new JFrame( "Foraging Simulation" );
                frame.setContentPane( _panel );
                frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                frame.pack();
                frame.setVisible( true );
            }
        } );
        
        _LOG.trace( "Leaving initialize( simState )" );
    }

    /**
     * Performs any cleanup after a simulation step
     *
     * @see edu.snu.csne.forage.event.AbstractSimulationEventListener#simStepTearDown()
     */
    @Override
    public void simStepTearDown()
    {
        // Update the display
        _panel.simUpdate( _simState );
    }

}
