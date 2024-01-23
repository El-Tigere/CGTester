package cgtester.scene;

public abstract class Resource {
    
    private boolean disposed = false;
    
    public void dispose() {
        if(disposed) return;
        
        onDispose();
        disposed = true;
    }
    
    public abstract void onDispose();
    
}
