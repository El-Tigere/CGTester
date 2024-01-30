package cgtester;

public class TesterState {
    
    private static TesterState instance;
    
    private Runnable resetWindow;
    
    private boolean[] keyStates; // TODO: perhaps move this somewhere else
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
    
    public boolean[] getKeyStates() {
        return keyStates;
    }
    
    public enum VertexAttributes {
        UV("uv", 0b0001),
        POS_UV("positionUv", 0b1001),
        POS_NORMAL_UV("positionNormalUv", 0b1101),
        POS_NORMAL_COLOR("positionNormalColor", 0b1110);
        
        private String name;
        private int attibuteMask;
        private int valueCount;
        
        VertexAttributes(String name, int attributeMask) {
            this.name = name;
            this.attibuteMask = attributeMask;
            valueCount = calcValueCount(attributeMask);
        }
        
        public String getName() {
            return name;
        }
        
        public int getValueCount() {
            return valueCount;
        }
        
        public int getAttributeMask() {
            return attibuteMask;
        }
        
        public static int calcValueCount(int attributeMask) {
            return ((attributeMask & 0b1000) > 0 ? 3 : 0)
                + ((attributeMask & 0b0100) > 0 ? 3 : 0)
                + ((attributeMask & 0b0010) > 0 ? 3 : 0)
                + ((attributeMask & 0b0001) > 0 ? 2 : 0);
        }
    }
    
}
