import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;

public class BasicJMEApp extends SimpleApplication
{
    public static void main( String[] args )
    {
        BasicJMEApp app = new BasicJMEApp();
        app.start();
    }

    @Override
    public void simpleInitApp()
    {
        // Create a simple cube
        Box box = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", box);

        // Create a simple material
        Material mat = new Material( assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md" );
        mat.setColor( "Color", ColorRGBA.Blue );
        geom.setMaterial(mat);

        // Make it visible
        rootNode.attachChild(geom);
    }
}
