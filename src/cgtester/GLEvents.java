package cgtester;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class GLEvents implements GLEventListener {
    
    private TesterState testerState;
    
    public GLEvents(TesterState testerState) {
        this.testerState = testerState;
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        System.out.println(testerState.keyStates[KeyEvent.VK_H]);
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
    }
    
}
