package cgtester.scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

public class Mesh {
    
    private GL3 gl;
    private int vao, vbo, ebo;
    private int indexCount;
    
    private Mesh(GL3 gl, float[] vertexData, int[] faceIndices) {
        this.gl = gl;
        indexCount = faceIndices.length;
        
        IntBuffer ib;
        
        // create vao
        ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenVertexArrays(1, ib);
        vao = ib.get(0);
        gl.glBindVertexArray(vao);
        
        // create vbo
        ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenBuffers(1, ib);
        vbo = ib.get(0);
        // copy vertex data
        FloatBuffer vertexDataBuffer = GLBuffers.newDirectFloatBuffer(vertexData);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertexDataBuffer.limit() * 4, vertexDataBuffer, GL3.GL_STATIC_DRAW);
        
        // create ebo
        ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenBuffers(1, ib);
        ebo = ib.get(0);
        // copy indices
        IntBuffer faceIndicesBuffer = GLBuffers.newDirectIntBuffer(faceIndices);
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ebo);
        gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, faceIndicesBuffer.limit() * 4, faceIndicesBuffer, GL3.GL_STATIC_DRAW);
        
        // vertex attributes
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 8 * 4, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, 8 * 4, 3 * 4);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(2, 2, GL3.GL_FLOAT, false, 8 * 4, 6 * 4);
        gl.glEnableVertexAttribArray(2);
    }
    
    public static Mesh fromArrays(GL3 gl, float[] vertexData, int[] faceIndices) {
        Mesh m = new Mesh(gl, vertexData, faceIndices);
        
        return m;
    }
    
    public void bindVAO() {
        gl.glBindVertexArray(vao);
    }
    
    public int getElementCount() {
        return indexCount;
    }
    
}
