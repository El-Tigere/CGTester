package cgtester;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;

import cgtester.scene.Scene;

public class MainWindow { // TODO: change to JFrame
    
    private GLWindow window;
    private Animator animator;
    
    private Keys keys;
    private GLEvents glEvents;
    
    private Scene scene;
    
    public MainWindow() {
        window = GLWindow.create(new GLCapabilities(GLProfile.get(GLProfile.GL3)));
        
        scene = Scene.createTestScene();
        keys = new Keys(scene.getTesterState());
        window.addKeyListener(keys);
        glEvents = new GLEvents(scene);
        window.addGLEventListener(glEvents);
        
        window.setSize(800, 800);
        window.setTitle("CGTester");
        
        animator = new Animator();
        animator.add(window);
        animator.start();
        
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyed(WindowEvent e) {
                new Thread(() -> {
                    animator.stop();
                    System.exit(1); // should not be necessary
                }).start();
            }
        });
        
        window.setVisible(true);
    }
    
}
