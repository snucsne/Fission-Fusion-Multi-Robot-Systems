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
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class Simulator
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            Simulator.class.getName() );

    /**
     * Main entry into the simulator
     *
     * @param args
     */
    public static void main( String[] args )
    {
        _LOG.info( "Starting simulator..." );

        // Turn off the default Java logging in JME so we can use Log4j
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
        
        SimulatorEngine engine = null;
        
        /* Wrap the execution in a try-catch block so we can log any
         * stray exceptions. */
        try
        {
            // Create and start the simulator engine
            engine = new SimulatorEngine();
            engine.initializeSimulation();
            engine.start();
        }
        catch( Throwable t )
        {
            // Log it and try to shut down
            _LOG.error( "An uncaught exception was thrown", t );
            engine.stop();
        }
        
        _LOG.info( "Shutting down simulator..." );
    }

}
