package cgtester;

import java.io.IOException;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

import cgtester.scene.Material;
import cgtester.scene.Mesh;
import cgtester.scene.ResourceManager;
import cgtester.scene.Scene;
import cgtester.scene.ShaderProgram;
import cgtester.scene.Texture;

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
        // init TesterState
        TesterState.create(() -> reset());
        
        // init ResourceManager
        ResourceManager.registerType(Material.class, "src/cgtester/resources/materials/", (f) -> Material.fromJsonFile(f));
        ResourceManager.registerType(Mesh.class, "src/cgtester/resources/meshes/", (f) -> Mesh.fromJsonFile(f));
        ResourceManager.registerType(Scene.class, "src/cgtester/resources/scenes/", (f) -> Scene.fromJsonFile(f));
        ResourceManager.registerType(ShaderProgram.class, "src/cgtester/resources/shaders/", (f) -> ShaderProgram.fromJsonFile(f));
        ResourceManager.registerType(Texture.class, "src/cgtester/resources/textures/", (f) -> Texture.fromJsonFile(f));
        
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
        // create scene
        try {
            scene = ResourceManager.getFromName("scene0", Scene.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // create gl panel
        panel = new GLJPanel(new GLCapabilities(GLProfile.get(GLProfile.GL3)));
        keys = new Keys();
        panel.addKeyListener(keys);
        glEvents = new GLEvents(scene);
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
            GLEvents.getGL().glFinish(); // wait for GL Objects to be deleted
            
            // create new scene
            createPanel();
            
            startRenderLoop();
        }).start();
    }
}
