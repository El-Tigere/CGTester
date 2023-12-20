package cgtester.scene;

import java.util.ArrayList;

import com.jogamp.opengl.math.Vec3f;

import cgtester.TesterState;

public class Scene {
    
    private ArrayList<Instance> instances;
    private Camera mainCamera;
    private TesterState testerState;
    
    private Scene(Camera mainCamera, TesterState testerState) {
        this.mainCamera = mainCamera;
        this.testerState = testerState;
    }
    
    public Camera getMainCamera() {
        return mainCamera;
    }
    
    public TesterState getTesterState() {
        return testerState;
    }
    
    public static Scene createTestScene() {
        Camera mainCamera = new Camera(new Vec3f(0f, 0f, 1f), new Vec3f(0f, 0f, 0f), (float) Math.toRadians(70f), 0.1f, 100f);
        TesterState testerState = new TesterState();
        Scene s = new Scene(mainCamera, testerState);
        return s;
    }
    
}
