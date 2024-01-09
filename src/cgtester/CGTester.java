package cgtester;

import java.io.IOException;

import javax.swing.JFrame;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.Animator;

import cgtester.scene.ResourceManager;
import cgtester.scene.Scene;

public class CGTester {
    
    private JFrame frame;
    private GLJPanel panel;
    
    private Animator animator;
    
    private Keys keys;
    private GLEvents glEvents;
    
    private Scene scene;
    
    public CGTester() {
        TesterState.create(() -> reset());
        
        // create scene
        try {
            scene = ResourceManager.getFromName("scene0");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // create gl panel
        panel = new GLJPanel(new GLCapabilities(GLProfile.get(GLProfile.GL3)));
        keys = new Keys();
        panel.addKeyListener(keys);
        glEvents = new GLEvents(scene);
        panel.addGLEventListener(glEvents);
        
        animator = new Animator();
        animator.add(panel);
        animator.start();
        animator.setUpdateFPSFrames(60, System.out);
        
        // create window
        frame = new TesterWindow(panel);
        frame.setSize(800, 600);
        frame.setTitle("CGTester");
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                new Thread(() -> { // TODO: why new thread here?
                    animator.stop();
                    System.exit(1); // should not be necessary
                }).start();
            }
        });
        
        frame.setVisible(true);
    }
    
    public void reset() {
        // dispose resources
        scene.dispose();
        ResourceManager.clear();
        
        // create new scene
        try {
            scene = ResourceManager.getFromName("scene0");
            glEvents.setScene(scene);
            scene.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
