package cgtester.scene;

import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.math.Vec3f;

public class Camera {
    
    public Vec3f position, rotation;
    public float fov, near, far;
    
    public Camera(Vec3f position, Vec3f rotation, float fov, float near, float far) {
        this.position = position;
        this.rotation = rotation;
        this.fov = fov;
        this.near = near;
        this.far = far;
    }
    
    public Matrix4f getCameraMatrix() {
        Matrix4f view = new Matrix4f().setToPerspective(fov, fov, near, far);
        Matrix4f cameraTranslation = new Matrix4f().setToTranslation(position.mul(-1));
        Matrix4f cameraRotation = new Matrix4f().setToRotation(new Quaternion().setFromEuler(rotation).invert());
        
        return view.mul(cameraRotation).mul(cameraTranslation);
    }
    
}
