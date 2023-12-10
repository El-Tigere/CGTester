package cgtester;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

public class Keys implements KeyListener {
    
    public boolean[] keyStates;
    
    public Keys() {
        keyStates = new boolean[256];
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
