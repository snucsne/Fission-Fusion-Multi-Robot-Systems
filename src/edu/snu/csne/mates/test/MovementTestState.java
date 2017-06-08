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
package edu.snu.csne.mates.test;

//Imports
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class MovementTestState extends AbstractAppState
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            MovementTestState.class.getName() );

    
    /** The main application */
    private SimpleApplication _app = null;
    
    /** The root node */
    private Node _rootNode = null;
    
    /** The movement controller */
    private MovementTestControl _hoverControl = null;
    
    
    /**
     * Called by AppStateManager when transitioning this AppState from
     * initializing to running.
     * 
     * @param stateManager
     * @param app
     * @see com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application)
     */
    @Override
    public void initialize( AppStateManager stateManager, Application app )
    {
        _LOG.trace( "Entering initialize( stateManager, app )" );

        // Call the superclass implementation
        super.initialize( stateManager, app );
        
        // Save some important variables
        _app = (SimpleApplication) app;
        _rootNode = _app.getRootNode();
        
        // Create the hovertank
        Spatial hovertank = _app.getAssetManager().loadModel(
                "Models/HoverTank/Tank2.mesh.xml" );
        CollisionShape hovertankColShape = CollisionShapeFactory.createDynamicMeshShape(
                hovertank );
//        hovertank.setShadowMode( ShadowMode.CastAndReceive );
        hovertank.setLocalTranslation( new Vector3f( 0, 0, 0) );
        hovertank.setLocalRotation(new Quaternion(
                new float[]{0, 0.01f, 0} ));

        // Set up the control
        _hoverControl = new MovementTestControl( hovertankColShape,
                1 );
        hovertank.addControl( _hoverControl );
        
        // Add it to the space
        _rootNode.attachChild( hovertank );
        stateManager.getState( BulletAppState.class ).getPhysicsSpace().add(
                _hoverControl );
        _hoverControl.setCollisionGroup( PhysicsCollisionObject.COLLISION_GROUP_02 );
        
        _LOG.trace( "Leaving initialize( stateManager, app )" );
    }

    /**
     * Called to update the AppState.
     *
     * @param tpf
     * @see com.jme3.app.state.AbstractAppState#update(float)
     */
    @Override
    public void update( float tpf )
    {
//        _hoverControl.accelerate( 10 );
//        _hoverControl.steer( 10 );
    }

    /**
     * Called by AppStateManager when transitioning this AppState from
     * terminating to detached.
     *
     * @see com.jme3.app.state.AbstractAppState#cleanup()
     */
    @Override
    public void cleanup()
    {
        _LOG.trace( "Entering cleanup()" );

        // Call the superclass implementation
        super.cleanup();

        _LOG.trace( "Leaving cleanup()" );
    }
    
    
}
