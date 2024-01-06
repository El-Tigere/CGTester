package cgtester;

public class TesterState {
    
    private static TesterState instance;
    
    private Runnable resetWindow;
    
    public boolean[] keyStates; // TODO: perhaps move this somewhere else
    
    private TesterState(Runnable resetWindow) {
        this.resetWindow = resetWindow;
        
        keyStates = new boolean[256];
    }
    
    static void create(Runnable resetWindow) {
        instance = new TesterState(resetWindow);
    }
    
    public static TesterState get() {
        assert instance != null;
        
        return instance;
    }
    
}
