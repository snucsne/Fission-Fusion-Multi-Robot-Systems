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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.Grid;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

import edu.snu.csne.mates.test.MovementTestState;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class SimulatorEngine extends SimpleApplication
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            SimulatorEngine.class.getName() );
    
    /** The simulation configuration */
    private SimulationConfiguration _simConfig = null;
    
    /** The simulation state */
    private SimulationState _simState = null;
    
    
    /**
     * Initializes the simulation environment.  There are things (such as
     * loading configuration for headless simulator) that need to be done
     * before the engine is started.  The normal simpleInitApp() method
     * is executed after starting.
     */
    public void initializeSimulation()
    {
        _LOG.trace( "Entering initializeSimulation()" );

        // Load the simulation configuration
        _simConfig = new SimulationConfiguration();
        
        // Create the simulation state
        _simState = new SimulationState();
        _simState.initiatize( this, _simConfig );
        
        // Load the settings
        // TODO
        AppSettings appSettings = new AppSettings( true );
        appSettings.setBitsPerPixel( 24 );
        appSettings.setResolution( 800, 600 );
        appSettings.setSamples( 4 );
        setSettings( appSettings );
        setShowSettings( false );
        
        _LOG.trace( "Leaving initializeSimulation()" );
    }
    
    /**
     * Initiates the application
     *
     * @see com.jme3.app.SimpleApplication#simpleInitApp()
     */
    @Override
    public void simpleInitApp()
    {
        _LOG.trace( "Entering simpleInitApp()" );

        // Add the physics engine
        // TODO What threading type?
        BulletAppState bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType( BulletAppState.ThreadingType.PARALLEL );
        stateManager.attach( bulletAppState );
        bulletAppState.getPhysicsSpace().setAccuracy( 1.0f / 30.0f );
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, 0));
        
//        // Create the mates application state
//        MatesAppState matesAppState = new MatesAppState( _simState );
//        stateManager.attach( matesAppState );
        
        // Create the movement test
        MovementTestState testState = new MovementTestState();
        stateManager.attach( testState );
        
        // Are we headless?
        if( _simConfig.isSimHeadless() )
        {
            // Yup
        }
        else
        {
            // Nope
            // Change the background color
            viewPort.setBackgroundColor( ColorRGBA.Gray );
            
            // Turn off FPS and stats display
            setDisplayFps( false );
            setDisplayStatView( false );

            // Create a grid on the x-y plane
            int lineCount = 100;
            float lineSpacing = 10;
            float halfGridSize = lineCount * lineSpacing / 2.0f;
            Grid grid = new Grid( lineCount, lineCount, lineSpacing );
            Geometry gridGeometry = new Geometry( "Grid", grid );
            gridGeometry.setLocalTranslation( -halfGridSize, 0, -halfGridSize );
            Material gridMaterial = new Material( assetManager,
                    "Common/MatDefs/Misc/Unshaded.j3md" );
            gridMaterial.setColor( "Color",
                    new ColorRGBA( 0.0f, 0.3f, 0.0f, 0.5f ) );
            gridGeometry.setMaterial( gridMaterial );
            rootNode.attachChild( gridGeometry );

            // Add some ambient light
            AmbientLight ambientLight = new AmbientLight();
            ambientLight.setColor( ColorRGBA.White.mult( 1.3f ) );
            rootNode.addLight( ambientLight );
            
            DirectionalLight sun = new DirectionalLight();
            sun.setColor(ColorRGBA.White);
            sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
            rootNode.addLight(sun);

            
            // Adjust camera speed
            flyCam.setMoveSpeed(50f);
        }
        
        // TODO
        
        _LOG.trace( "Leaving simpleInitApp()" );        
    }

    /**
     * Performs an update of the simulation in the main loop
     *
     * @param tpf
     * @see com.jme3.app.SimpleApplication#simpleUpdate(float)
     */
    @Override
    public void simpleUpdate( float tpf )
    {
        // Most of the updates will occur in the app states and controllers
        super.simpleUpdate(tpf);
    }

    /**
     * Starts the application
     *
     * @see com.jme3.app.SimpleApplication#start()
     */
    @Override
    public void start()
    {
        _LOG.trace( "Entering start()" );

        // Do we run headless?
        boolean headless = _simConfig.isSimHeadless();
        _LOG.debug( "Starting headless=[" + headless + "]" );
        if( headless )
        {
            // Yup
            super.start( JmeContext.Type.Headless );
        }
        else
        {
            // No, run graphically
            super.start();
        }
        
        _LOG.trace( "Leaving start()" );
    }

}
