package cgtester.scene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.math.Vec3f;

import cgtester.GLEvents;
import cgtester.Util;

public class Scene extends Resource {
    
    private SceneProperties properties;
    private boolean initialized;
    private ArrayList<Instance> instances;
    private Camera mainCamera;
    private Vec3f sunDirection;
    
    private Scene(SceneProperties properties) {
        initialized = false;
        this.properties = properties;
    }
    
    public Camera getMainCamera() {
        assert initialized;
        return mainCamera;
    }
    
    public static Scene fromJsonFile(File jsonFile) throws IOException {
        // create SceneProperties
        SceneProperties properties = Util.loadFileObject(jsonFile, SceneProperties.class);
        
        // create scene
        Scene scene = new Scene(properties);
        
        return scene;
    }
    
    public void init() throws IOException {
        assert GLEvents.getGL() != null;
        
        // create camera
        SceneProperties.CameraProperties cp = properties.camera;
        mainCamera = new Camera(new Vec3f(cp.position), new Vec3f(cp.rotation), cp.fov, 1f, cp.near, cp.far);
        
        // create instances
        instances = new ArrayList<>();
        for(SceneProperties.InstanceProperties ip : properties.instances) {
            Mesh mesh = ResourceManager.getFromName(ip.mesh, Mesh.class);
            Material material = ResourceManager.getFromName(ip.material, Material.class);
            Transform transform = new Transform(new Vec3f(ip.position), new Vec3f(ip.rotation), new Vec3f(ip.scale));
            instances.add(new Instance(mesh, material, transform));
        }
        
        // set sun direction
        sunDirection = new Vec3f(properties.sunDirection);
        
        // scene is initialized
        initialized = true;
    }
    
    public void update(float deltaTime) {
        assert initialized;
        mainCamera.update(deltaTime);
    }
    
    public void draw() {
        assert initialized;
        
        Matrix4f cameraMatrix = mainCamera.getCameraMatrix();
        
        for(Instance instance : instances) {
            instance.draw(cameraMatrix, sunDirection);
        }
    }
    
    @Override
    public void onDispose() {
        assert initialized;
        
        for(Instance instance : instances) {
            instance.dispose();
        }
    }
    
    private static class SceneProperties {
        public CameraProperties camera;
        public InstanceProperties[] instances;
        public float[] sunDirection;
        
        public static class CameraProperties {
            public float[] position, rotation;
            public float fov, near, far;
        }
        
        public static class InstanceProperties {
            public String mesh, material;
            public float[] position, rotation, scale;
        }
    }
    
}
