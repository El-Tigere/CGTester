package cgtester;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Keys implements KeyListener {
    
    private boolean[] keyStates;
    
    public Keys() {
        keyStates = TesterState.get().keyStates;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        keyStates[e.getKeyCode()] = true;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        keyStates[e.getKeyCode()] = false;
    }
    
}
