package cgtester;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

public class Keys implements KeyListener {
    
    private TesterState testerState;
    
    public Keys(TesterState testerState) {
        this.testerState = testerState;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if(!e.isAutoRepeat()) testerState.keyStates[e.getKeyCode()] = true;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if(!e.isAutoRepeat()) testerState.keyStates[e.getKeyCode()] = false;
    }
    
}
