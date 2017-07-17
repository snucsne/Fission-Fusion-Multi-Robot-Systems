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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.snu.csne.forage.SimulationState;

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
        
        // Create the graphics panel
        _panel = new GraphicalPositionPanel( 800,
                600,
                5,
                Color.WHITE,
                Color.DARK_GRAY,
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
