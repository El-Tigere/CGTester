package cgtester.scene;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.math.Vec3f;

import cgtester.TesterState;

public class Camera {
    
    public Vec3f position, rotation;
    public float fov, aspect, near, far;
    
    private static final float HALF_PI = (float) Math.PI / 2;
    
    public Camera(Vec3f position, Vec3f rotation, float fov, float aspect, float near, float far) {
        this.position = position;
        this.rotation = rotation;
        this.fov = fov;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
    }
    
    public Matrix4f getCameraMatrix() {
        Matrix4f view = new Matrix4f().setToPerspective(fov, aspect, near, far);
        Matrix4f cameraTranslation = new Matrix4f().setToTranslation(position.mul(-1));
        Matrix4f cameraRotation = new Matrix4f().setToRotation(new Quaternion().setFromEuler(rotation).invert());
        
        return view.mul(cameraRotation).mul(cameraTranslation);
    }
    
    public void update(float deltaTime) {
        boolean[] k = TesterState.get().keyStates;
        float move = (k[KeyEvent.VK_SHIFT] ? 10f : 2f) * deltaTime;
        
        // rotation
        if(k[KeyEvent.VK_UP]) rotation.add(1f * deltaTime, 0f, 0f);
        if(k[KeyEvent.VK_DOWN]) rotation.add(-1f * deltaTime, 0f, 0f);
        if(k[KeyEvent.VK_LEFT]) rotation.add(0f, 1f * deltaTime, 0f);
        if(k[KeyEvent.VK_RIGHT]) rotation.add(0f, -1f * deltaTime, 0f);
        
        // translation
        float x = rotation.x();
        rotation.setX(x < -HALF_PI ? -HALF_PI : x > HALF_PI ? HALF_PI : x);
        Quaternion q = new Quaternion().setFromEuler(0f, rotation.y(), 0f);
        if(k[KeyEvent.VK_W]) position.add(q.rotateVector(new Vec3f(0f, 0f, -move), new Vec3f()));
        if(k[KeyEvent.VK_A]) position.add(q.rotateVector(new Vec3f(-move, 0f, 0f), new Vec3f()));
        if(k[KeyEvent.VK_S]) position.add(q.rotateVector(new Vec3f(0f, 0f, move), new Vec3f()));
        if(k[KeyEvent.VK_D]) position.add(q.rotateVector(new Vec3f(move, 0f, 0f), new Vec3f()));
        if(k[KeyEvent.VK_E]) position.add(/*q.rotateVector(*/new Vec3f(0f, move, 0f)/*, new Vec3f())*/);
        if(k[KeyEvent.VK_Q]) position.add(/*q.rotateVector(*/new Vec3f(0f, -move, 0f)/*, new Vec3f())*/);
    }
    
}
