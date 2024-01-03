package cgtester.scene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.math.Vec3f;

import cgtester.TesterState;
import cgtester.Util;

public class Scene {
    
    private SceneProperties properties;
    private boolean initialized;
    private ArrayList<Instance> instances;
    private Camera mainCamera;
    private TesterState testerState;
    
    private Scene(SceneProperties properties, TesterState testerState) {
        initialized = false;
        this.properties = properties;
        this.testerState = testerState;
    }
    
    public Camera getMainCamera() {
        assert initialized;
        return mainCamera;
    }
    
    public TesterState getTesterState() {
        return testerState;
    }
    
    public static Scene fromJsonFile(File jsonFile) throws IOException {
        // create SceneProperties
        SceneProperties properties = Util.loadFileObject(jsonFile, SceneProperties.class);
        
        // create scene
        Scene scene = new Scene(properties, new TesterState());
        
        return scene;
    }
    
    public void init() throws IOException {
        // create camera
        SceneProperties.CameraProperties cp = properties.camera;
        mainCamera = new Camera(new Vec3f(cp.position), new Vec3f(cp.rotation), cp.fov, 1f, cp.near, cp.far);
        
        // create instances
        instances = new ArrayList<>();
        for(SceneProperties.InstanceProperties ip : properties.instances) {
            Mesh mesh = ResourceManager.getFromName(ip.mesh);
            Material material = ResourceManager.getFromName(ip.material);
            Transform transform = new Transform(new Vec3f(ip.position), new Vec3f(ip.rotation), new Vec3f(ip.scale));
            instances.add(new Instance(mesh, material, transform));
        }
        
        // scene is initialized
        initialized = true;
    }
    
    public void update(float deltaTime) {
        mainCamera.update(testerState, deltaTime);
    }
    
    public void draw() {
        Matrix4f cameraMatrix = mainCamera.getCameraMatrix();
        
        for(Instance instance : instances) {
            instance.draw(cameraMatrix);
        }
    }
    
    private static class SceneProperties {
        public CameraProperties camera;
        public InstanceProperties[] instances;
        
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
