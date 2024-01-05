package cgtester;

public class TesterState {
    
    private static TesterState instance;
    
    //private Runnable resetWindow;
    
    public boolean[] keyStates;
    
    private boolean useVAOs;
    
    private TesterState(/*Runnable resetWindow*/) {
        //this.resetWindow = resetWindow;
        
        keyStates = new boolean[256];
        useVAOs = true;
    }
    
    static void create(/*Runnable resetWindow*/) {
        instance = new TesterState(/*resetWindow*/);
    }
    
    public static TesterState get() {
        assert instance != null;
        
        return instance;
    }
    
    void setUseVAOs(boolean useEBOs) {
        this.useVAOs = useEBOs;
        //resetWindow.run();
    }
    
    public boolean getUseVAOs() {
        return useVAOs;
    }
    
}
