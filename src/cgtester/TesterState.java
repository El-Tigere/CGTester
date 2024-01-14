package cgtester;

public class TesterState {
    
    private static TesterState instance;
    
    private Runnable resetWindow;
    
    public boolean[] keyStates; // TODO: perhaps move this somewhere else
    private VertexAttributes vertexAttributes;
    
    private TesterState(Runnable resetWindow) {
        this.resetWindow = resetWindow;
        
        keyStates = new boolean[256];
        vertexAttributes = VertexAttributes.POS_NORMAL_UV;
    }
    
    static void create(Runnable resetWindow) {
        instance = new TesterState(resetWindow);
    }
    
    public static TesterState get() {
        assert instance != null;
        
        return instance;
    }
    
    public VertexAttributes getVertexAttributes() {
        return vertexAttributes;
    }
    
    void setVertexAttributes(VertexAttributes vertexAttributes) {
        this.vertexAttributes = vertexAttributes;
        resetWindow.run();
    }
    
    public enum VertexAttributes {
        UV,
        POS_UV,
        POS_NORMAL_UV,
        POS_NORMAL_COLOR
    }
    
}
