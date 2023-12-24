package cgtester.scene;

public class Instance {
    
    private Mesh mesh;
    private Transform transform;
    
    public Instance(Mesh mesh) {
        this.mesh = mesh;
        transform = new Transform();
    }
    
    public Transform getTransform() {
        return transform;
    }
    
}
