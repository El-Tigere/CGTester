package cgtester.scene;

import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.math.Vec3f;

public class Instance {
    
    private Mesh mesh;
    private Material material;
    private Transform transform;
    
    public Instance(Mesh mesh, Material material, Transform transform) {
        this.mesh = mesh;
        this.material = material;
        this.transform = transform;
    }
    
    public void draw(Matrix4f cameraMatrix, Vec3f sunDirection) {
        Matrix4f matrix = new Matrix4f(cameraMatrix).mul(transform.getTransformationMatrix());
        material.use(matrix, sunDirection);
        mesh.draw();
    }
    
    public Transform getTransform() {
        return transform;
    }
    
    public void dispose() {
        mesh.dispose();
        material.dispose();
    }
    
}
