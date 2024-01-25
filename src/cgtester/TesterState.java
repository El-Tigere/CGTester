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
    
    private void reset() {
        keyStates = new boolean[256];
        resetWindow.run();
    }
    
    public VertexAttributes getVertexAttributes() {
        return vertexAttributes;
    }
    
    void setVertexAttributes(VertexAttributes vertexAttributes) {
        this.vertexAttributes = vertexAttributes;
        reset();
    }
    
    public enum VertexAttributes {
        UV (2, 0b0001),
        POS_UV (3 + 2, 0b1001),
        POS_NORMAL_UV (3 + 3 + 2, 0b1101),
        POS_NORMAL_COLOR (3 + 3 + 3, 0b1110);
        
        private int valueCount;
        private int attibuteMask;
        
        VertexAttributes(int attributeCount, int attributeMask) {
            this.valueCount = attributeCount;
            this.attibuteMask = attributeMask;
        }
        
        public int getValueCount() {
            return valueCount;
        }
        
        public int getAttributeMask() {
            return attibuteMask;
        }
    }
    
}
