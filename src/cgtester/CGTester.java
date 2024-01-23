package cgtester;

import java.io.IOException;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

import cgtester.scene.ResourceManager;
import cgtester.scene.Scene;

public class CGTester {
    
    private TesterWindow frame;
    private GLJPanel panel;
    
    private Keys keys;
    private GLEvents glEvents;
    
    private Scene scene;
    
    private volatile boolean running;
    private Thread renderThread;
    private static final int FPS = 60;
    
    public CGTester() {
        TesterState.create(() -> reset());
        
        // create window
        frame = new TesterWindow();
        frame.setSize(800, 600);
        frame.setTitle("CGTester");
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
           @Override
           public void windowClosing(java.awt.event.WindowEvent e) {
               new Thread(() -> { // TODO: why new thread here?
                   running = false; // TODO: this alone should stop the application
                   System.exit(1); // should not be necessary
               }).start();
           }
        });
        
        frame.setVisible(true);
        
        createPanel();
        
        // start render loop
        startRenderLoop();
    }
    
    private void createPanel() {
        //frame.setGlPanel(new Container());
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
        glEvents = new GLEvents(scene, frame);
        panel.addGLEventListener(glEvents);
        
        panel.setSkipGLOrientationVerticalFlip(true); // very important!
        
        frame.setGlPanel(panel);
        
        panel.requestFocus();
    }
    
    private void startRenderLoop() {
        renderThread = new Thread(() -> {
            running = true;
            
            final long nspf = 1_000_000_000 / FPS;
            
            long lastNanos = System.nanoTime();
            long currentNanos;
            long diff;
            long frameTime = 0;
            
            while(running) {
                // delta Time
                currentNanos = System.nanoTime();
                diff = currentNanos - lastNanos;
                lastNanos = currentNanos;
                
                frameTime += diff;
                if(frameTime >= nspf) {
                    frameTime -= nspf;
                    //float deltaTime = diff / 1_000_000_000f;
                    panel.display();
                }
            }
        }, "CGTester-render-thread");
        renderThread.start();
    }
    
    private void reset() {
        new Thread(() -> {
            // pause rendering
            running = false;
            try {
                renderThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // dispose resources
            scene.dispose();
            ResourceManager.clear();
            GLEvents.gl.glFinish(); // wait for GL Objects to be deleted
            
            // create new scene
            createPanel();
            
            startRenderLoop();
        }).run();
    }
}
