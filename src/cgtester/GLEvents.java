package cgtester;

import java.io.IOException;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import cgtester.scene.Scene;

public class GLEvents implements GLEventListener {
    
    private static GL3 gl;
    
    private Scene scene;
    
    private long lastNanos;
    
    public GLEvents(Scene scene) {
        this.scene = scene;
        lastNanos = -1;
    }
    
    public static GL3 getGL() {
        return gl;
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL3();
        
        // init scene
        try {
            scene.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // further gl settings
        gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_FILL);
        gl.glClearColor(0.5f, 0f, 0.5f, 1f);
        gl.glEnable(GL3.GL_DEPTH_TEST);
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        
        // delta Time
        long currentNanos = System.nanoTime();
        float deltaTime = lastNanos == -1 ? 0f : (currentNanos - lastNanos) / 1_000_000_000f;
        lastNanos = currentNanos;
        
        // update scene
        scene.update(deltaTime);
        
        // clear color buffer
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        
        // draw scene
        scene.draw();
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        gl.glViewport(0, 0, width, height);
        scene.getMainCamera().aspect = (float) width / height;
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
    }
    
}
