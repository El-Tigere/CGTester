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
    
    public Matrix4f getTransformationMatrix() {
        Matrix4f transformTranslation = new Matrix4f().setToTranslation(position.mul(-1));
        Matrix4f transformRotation = new Matrix4f().setToRotation(new Quaternion().setFromEuler(rotation).invert());
        Matrix4f transformScale = new Matrix4f().setToScale(scale);
        
        return transformTranslation.mul(transformRotation).mul(transformScale);
    }
    
}
