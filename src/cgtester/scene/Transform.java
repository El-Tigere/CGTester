package cgtester.scene;

import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.math.Vec3f;

public class Transform {
    
    public Vec3f position, rotation, scale;
    
    public Transform(Vec3f position, Vec3f rotation, Vec3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }
    
    public Transform(float[] position, float[] rotation, float[] scale) {
        this.position = new Vec3f(position);
        this.rotation = new Vec3f(rotation);
        this.scale = new Vec3f(scale);
    }
    
    public Transform() {
        position = new Vec3f(0f, 0f, 0f);
        rotation = new Vec3f(0f, 0f, 0f);
        scale = new Vec3f(1f, 1f, 1f);
    }
    
    public Matrix4f getTransformationMatrix() {
        Matrix4f transformTranslation = new Matrix4f().setToTranslation(position);
        Matrix4f transformRotation = new Matrix4f().setToRotation(new Quaternion().setFromEuler(rotation));
        Matrix4f transformScale = new Matrix4f().setToScale(scale);
        
        return transformTranslation.mul(transformRotation).mul(transformScale);
    }
    
}
