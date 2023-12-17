package cgtester;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.GLBuffers;

public class GLEvents implements GLEventListener {
    
    private TesterState testerState;
    
    private FloatBuffer vertices = GLBuffers.newDirectFloatBuffer(new float[] {
        -0.5f, 0.5f, 0f,    1f, 0f, 0f,   0f, 0f,
        -0.5f, -0.5f, 0f,   0f, 1f, 0f,   0f, 1f,
        0.5f, 0.5f, 0f,     0f, 0f, 1f,   1f, 0f,
        0.5f, -0.5f, 0f,    0f, 0f, 0f,   1f, 1f
    });
    private IntBuffer vertIndices = GLBuffers.newDirectIntBuffer(new int[] {
        0, 1, 2,
        1, 3, 2
    });
    
    private FloatBuffer clearColor = GLBuffers.newDirectFloatBuffer(4);
    private String vertexShaderCode;
    private String fragmentShaderCode;
    private int shaderProgram;
    private int matrixUniformLocation;
    private int vao;
    
    private BufferedImage textureData;
    private int texture;
    
    public GLEvents(TesterState testerState) {
        this.testerState = testerState;
        
        // load shader code
        try {
            vertexShaderCode = loadShaderCode("./src/cgtester/shaders/test.vsh");
            fragmentShaderCode = loadShaderCode("./src/cgtester/shaders/test.fsh");
            textureData = ImageIO.read(new File("src/cgtester/resources/test.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        IntBuffer ib;
        
        // set clear color
        clearColor.put(0, 0f).put(1, 0f).put(2, 0f).put(3, 1f);
        
        // create shaders
        int vertexShader = compileShader(gl, vertexShaderCode, GL3.GL_VERTEX_SHADER);
        int fragmentShader = compileShader(gl, fragmentShaderCode, GL3.GL_FRAGMENT_SHADER);
        
        // create shader programs
        shaderProgram = linkShaderProgram(gl, vertexShader, fragmentShader);
        
        // create texture
        ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenTextures(1, ib);
        texture = ib.get(0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, texture);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR_MIPMAP_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
        Buffer textureBuffer = GLBuffers.newDirectByteBuffer(((DataBufferByte) textureData.getData().getDataBuffer()).getData());
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGB, textureData.getWidth(), textureData.getHeight(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, textureBuffer);
        gl.glGenerateMipmap(GL3.GL_TEXTURE_2D);
        
        // delete shaders
        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);
        
        // create vao
        ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenVertexArrays(1, ib);
        vao = ib.get(0);
        
        // create vbo
        ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenBuffers(1, ib);
        int vbo = ib.get(0);
        
        // create ebo
        ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenBuffers(1, ib);
        int ebo = ib.get(0);
        
        // setup vao
        gl.glBindVertexArray(vao);
        // copy vertex data
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertices.limit() * 4, vertices, GL3.GL_STATIC_DRAW);
        // copy indices
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ebo);
        gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, vertIndices.limit() * 4, vertIndices, GL3.GL_STATIC_DRAW);
        // vertex attributes
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 8 * 4 /* size of 3 floats */, 0 /* offset is 0 */);
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, 8 * 4 /* size of 3 floats */, 3 * 4 /* offset is 0 */);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(2, 2, GL3.GL_FLOAT, false, 8 * 4 /* size of 3 floats */, 6 * 4 /* offset is 0 */);
        gl.glEnableVertexAttribArray(2);
        
        // get uniform location
        matrixUniformLocation = gl.glGetUniformLocation(shaderProgram, "matr");
        
        // set polygon mode
        gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_FILL);
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        
        // clear color buffer
        gl.glClearBufferfv(GL2ES3.GL_COLOR, 0, clearColor);
        
        // draw triangles
        gl.glUseProgram(shaderProgram);
        gl.glBindVertexArray(vao);
        gl.glUniformMatrix4fv(matrixUniformLocation, 1, false, new float[] {
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            testerState.keyStates[KeyEvent.VK_H] ? 0.5f : 0f, 0f, 0f, 1f
        }, 0);
        gl.glDrawElements(GL3.GL_TRIANGLES, vertIndices.limit(), GL3.GL_UNSIGNED_INT, 0);
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
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
