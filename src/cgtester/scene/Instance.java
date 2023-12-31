package cgtester.scene;

import com.jogamp.opengl.math.Matrix4f;

public class Instance {
    
    private Mesh mesh;
    private Material material;
    private Transform transform;
    
    public Instance(Mesh mesh, Material material, Transform transform) {
        this.mesh = mesh;
        this.material = material;
        this.transform = transform;
    }
    
    public void draw(Matrix4f cameraMatrix) {
        Matrix4f matrix = new Matrix4f(cameraMatrix).mul(transform.getTransformationMatrix());
        material.use(matrix);
        mesh.draw();
    }
    
    public Transform getTransform() {
        return transform;
    }
    
}
