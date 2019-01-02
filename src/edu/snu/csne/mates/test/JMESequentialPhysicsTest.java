package edu.snu.csne.mates.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class JMESequentialPhysicsTest extends SimpleApplication
    implements PhysicsTickListener
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            JMESequentialPhysicsTest.class.getName() );
    
    private BulletAppState bulletAppState = null;
    
    private boolean waiting = false;

    public static void main( String[] args )
    {
        JMESequentialPhysicsTest app = new JMESequentialPhysicsTest();
        app.start();
    }

    @Override
    public void simpleInitApp()
    {
        bulletAppState = new BulletAppState();
        bulletAppState.setSpeed( 0.1f );
        bulletAppState.setThreadingType( BulletAppState.ThreadingType.SEQUENTIAL );
        bulletAppState.startPhysics();
        stateManager.attach( bulletAppState );
//        bulletAppState.setDebugEnabled(true);
        bulletAppState.getPhysicsSpace().addTickListener( this );
        
        /** Configure cam to look at scene */
        cam.setLocation(new Vector3f(0, 4f, 6f));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        // Create a floor
        Box floorBox = new Box(10f, 0.1f, 5f);
        Geometry floor = new Geometry("Floor", floorBox);
        floor.setLocalTranslation( 0,  -0.1f, 0 );
        floor.addControl( new RigidBodyControl( new BoxCollisionShape( new Vector3f( 10f, 0.1f, 5f)), 0 ));

        // Create a simple material
        Material floorMaterial = new Material( assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md" );
        floorMaterial.setColor( "Color", ColorRGBA.Blue );
        floor.setMaterial(floorMaterial);

        // Make it visible
        rootNode.attachChild(floor);
        bulletAppState.getPhysicsSpace().add( floor );
        
        // Create a ball
        Sphere ball = new Sphere( 32, 32, 0.5f, true, false );
        Geometry ballGeom = new Geometry( "Ball", ball );
        ball.setTextureMode( TextureMode.Projected );
        floor.setLocalTranslation( 0, 20, 0 );
        SphereCollisionShape ballCollision = new SphereCollisionShape( 0.5f );
        RigidBodyControl ballControl =  new RigidBodyControl( ballCollision, 1 );
        ballControl.setGravity( new Vector3f( 0, -10f, 0 ) );
        ballGeom.addControl( ballControl );
        rootNode.attachChild( ballGeom );
        bulletAppState.getPhysicsSpace().add( ballGeom );
        ballControl.setLinearVelocity( new Vector3f( 0, 4f, 0 ) );
        
        
        Material ballMaterial = new Material( assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md" );
        ballMaterial.setColor( "Color", ColorRGBA.Red );
        ballGeom.setMaterial(ballMaterial);

//        // We add light so we see the scene
//        AmbientLight al = new AmbientLight();
//        al.setColor(ColorRGBA.White.mult(1.3f));
//        rootNode.addLight(al);
//
//        DirectionalLight dl = new DirectionalLight();
//        dl.setColor(ColorRGBA.White);
//        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
//        rootNode.addLight(dl);

    }

    @Override
    public void simpleUpdate( float tpf )
    {
        _LOG.debug( "simpleUpdate: " + Thread.currentThread().getName() );

        if( !waiting )
        {
            _LOG.debug( "Ready" );
            waiting = true;
        }
//        try
//        {
//            Thread.sleep( 50l );
//        }
//        catch( InterruptedException e )
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    @Override
    public void physicsTick( PhysicsSpace arg0, float arg1 )
    {
        _LOG.debug( "physicsTick: " + Thread.currentThread().getName() );
        waiting = false;
    }

    @Override
    public void prePhysicsTick( PhysicsSpace arg0, float arg1 )
    {
        _LOG.debug( "prePhysicsTick: " + Thread.currentThread().getName() );
    }

    
}
