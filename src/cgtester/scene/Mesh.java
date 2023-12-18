package cgtester.scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

public class Mesh {
    
    private int vao, vbo, ebo;
    private GL3 gl;
    private FloatBuffer vertexDataBuffer;
    private IntBuffer faceIndicesBuffer;
    
    private Mesh(GL3 gl) {
        this.gl = gl;
    }
    
    public static Mesh fromArrays(GL3 gl, float[] vertexData, int[] faceIndices) {
        Mesh m = new Mesh(gl);
        
        IntBuffer ib;
        
        // create vao
        ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenVertexArrays(1, ib);
        m.vao = ib.get(0);
        
        // create vbo
        ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenBuffers(1, ib);
        m.vbo = ib.get(0);
        
        // create ebo
        ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenBuffers(1, ib);
        m.ebo = ib.get(0);
        
        // copy vertex data
        m.vertexDataBuffer = GLBuffers.newDirectFloatBuffer(vertexData);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, m.vbo);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, m.vertexDataBuffer.limit() * 4, m.vertexDataBuffer, GL3.GL_STATIC_DRAW);
        
        // copy indices
        m.faceIndicesBuffer = GLBuffers.newDirectIntBuffer(faceIndices);
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, m.ebo);
        gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, m.faceIndicesBuffer.limit() * 4, m.faceIndicesBuffer, GL3.GL_STATIC_DRAW);
        
        gl.glBindVertexArray(m.vao);
        
        // vertex attributes
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 8 * 4, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, 8 * 4, 3 * 4);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(2, 2, GL3.GL_FLOAT, false, 8 * 4, 6 * 4);
        gl.glEnableVertexAttribArray(2);
        
        return m;
    }
    
    public void bindVAO() {
        gl.glBindVertexArray(vao);
    }
    
    public int getElementCount() {
        return faceIndicesBuffer.limit();
    }
    
}
