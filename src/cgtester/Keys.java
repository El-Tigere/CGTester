package cgtester;

import java.awt.event.KeyListener;

public class Keys implements KeyListener {
    
    private boolean[] keyStates;
    
    public Keys() {
        keyStates = TesterState.get().keyStates;
    }
    
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {
    }
    
    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        keyStates[e.getKeyCode()] = true;
    }
    
    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {
        keyStates[e.getKeyCode()] = false;
    }
    
}
