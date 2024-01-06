package cgtester;

import java.awt.event.KeyListener;

public class Keys implements KeyListener {
    
    private TesterState testerState;
    
    public Keys() {
        testerState = TesterState.get();
    }
    
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {
    }
    
    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        testerState.keyStates[e.getKeyCode()] = true;
    }
    
    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {
        testerState.keyStates[e.getKeyCode()] = false;
    }
    
}
