package cgtester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.util.GLBuffers;

import cgtester.scene.Mesh;
import cgtester.scene.Scene;
import cgtester.scene.Texture;

public class GLEvents implements GLEventListener {
    
    public static GL3 gl;
    
    private Scene scene;
    private TesterState testerState;
    
    private long lastNanos;
    
    private FloatBuffer clearColor = GLBuffers.newDirectFloatBuffer(4);
    private Mesh mesh;
    private String vertexShaderCode;
    private String fragmentShaderCode;
    private int shaderProgram;
    private int matrixUniformLocation;
    private int samplerUniformLocation;
    
    private Texture texure;
    
    public GLEvents(Scene scene) {
        this.scene = scene;
        testerState = scene.getTesterState();
        lastNanos = -1;
        
        // load shader code
        try {
            vertexShaderCode = loadShaderCode("./src/cgtester/shaders/test.vsh");
            fragmentShaderCode = loadShaderCode("./src/cgtester/shaders/test.fsh");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL3();
        
        // set clear color
        clearColor.put(0, 0f).put(1, 0f).put(2, 0f).put(3, 1f);
        
        // load Mesh
        //mesh = Mesh.fromArrays(gl, vertices, vertIndices);
        try {
            mesh = Mesh.fromJsonFile(gl, new File("src/cgtester/resources/meshes/suzanne.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // create shaders
        int vertexShader = compileShader(gl, vertexShaderCode, GL3.GL_VERTEX_SHADER);
        int fragmentShader = compileShader(gl, fragmentShaderCode, GL3.GL_FRAGMENT_SHADER);
        
        // create shader programs
        shaderProgram = linkShaderProgram(gl, vertexShader, fragmentShader);
        
        // create texture
        try {
            texure = Texture.fromJsonFile(gl, new File("src/cgtester/resources/textures/test.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // delete shaders
        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);
        
        // get uniform location
        matrixUniformLocation = gl.glGetUniformLocation(shaderProgram, "matr");
        samplerUniformLocation = gl.glGetUniformLocation(shaderProgram, "testSampler");
        
        // further gl settings
        gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_FILL);
        gl.glEnable(GL3.GL_DEPTH_TEST);
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        
        // delta Time
        long currentNanos = System.nanoTime();
        float deltaTime = lastNanos == -1 ? 0f : (currentNanos - lastNanos) / 1_000_000_000f;
        lastNanos = currentNanos;
        
        // update scene
        scene.getMainCamera().update(testerState, deltaTime);

        // clear color buffer
        gl.glClearBufferfv(GL2ES3.GL_COLOR, 0, clearColor);
        gl.glClear(GL3.GL_DEPTH_BUFFER_BIT);
        
        // draw triangles
        gl.glUseProgram(shaderProgram);
        
        mesh.bindVAO();
        
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        texure.bindTexture();
        gl.glUniform1i(samplerUniformLocation, 0);
        
        Matrix4f matrix = scene.getMainCamera().getCameraMatrix();
        gl.glUniformMatrix4fv(matrixUniformLocation, 1, false, matrix.get(new float[16]), 0);
        
        gl.glDrawElements(GL3.GL_TRIANGLES, mesh.getElementCount(), GL3.GL_UNSIGNED_INT, 0);
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        gl.glViewport(0, 0, width, height);
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
    }
    
    private String loadShaderCode(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        StringBuilder sb = new StringBuilder();
        while(scanner.hasNextLine()) sb.append(scanner.nextLine() + '\n');
        scanner.close();
        return sb.toString();
    }
    
    private int compileShader(GL3 gl, String shaderCode, int shaderType) {
        // create shader
        int shaderID = gl.glCreateShader(shaderType);
        gl.glShaderSource(shaderID, 1, new String[] {shaderCode}, null);
        gl.glCompileShader(shaderID);
        
        // get success
        IntBuffer ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGetShaderiv(shaderID, GL3.GL_COMPILE_STATUS, ib);
        //System.out.println(ib.get(0));
        
        // get error message if necessary
        if(ib.get(0) == 0) {
            ByteBuffer bb = ByteBuffer.wrap(new byte[512]);
            gl.glGetShaderInfoLog(shaderID, 512, null, bb);
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < bb.limit(); i++) {
                sb.append((char) bb.get(i));
            }
            System.out.println(sb.toString());
        }
        
        return shaderID;
    }
    
    private int linkShaderProgram(GL3 gl, int vert, int frag) {
        // create shader program
        int shaderProgramID = gl.glCreateProgram();
        gl.glAttachShader(shaderProgramID, vert);
        gl.glAttachShader(shaderProgramID, frag);
        gl.glLinkProgram(shaderProgramID);
        
        // check program status
        IntBuffer ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGetProgramiv(shaderProgramID, GL3.GL_LINK_STATUS, ib);
        //System.out.println(ib.get(0));
        if(ib.get(0) == 0) {
            ByteBuffer bb = ByteBuffer.wrap(new byte[512]);
            gl.glGetProgramInfoLog(shaderProgramID, 512, null, bb);
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < bb.limit(); i++) {
                sb.append((char) bb.get(i));
            }
            System.out.println(sb.toString());
        }
        
        return shaderProgramID;
    }
    
}
