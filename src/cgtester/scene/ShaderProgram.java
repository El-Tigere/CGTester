package cgtester.scene;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.math.Vec3f;
import com.jogamp.opengl.util.GLBuffers;

import cgtester.GLEvents;
import cgtester.TesterState;
import cgtester.Util;

public class ShaderProgram extends Resource {
    
    private GL3 gl;
    private int shaderProgram;
    private int matrixUniformLocation;
    private int sunDirectionUniformLocation;
    private int[] samplerLocations;
    
    private ShaderProgram(GL3 gl, String vertexShaderCode, String fragmentShaderCode, ShaderProgramProperties.Variant variant) {
        this.gl = gl;
        
        // create shaders
        int vertexShader = compileShader(vertexShaderCode, GL3.GL_VERTEX_SHADER);
        int fragmentShader = compileShader(fragmentShaderCode, GL3.GL_FRAGMENT_SHADER);
        
        // create shader programs
        shaderProgram = linkShaderProgram(vertexShader, fragmentShader);
        
        // delete shaders
        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);
        
        // get uniform locations
        matrixUniformLocation = gl.glGetUniformLocation(shaderProgram, "matr");
        sunDirectionUniformLocation = gl.glGetUniformLocation(shaderProgram, "sunDirection");
        samplerLocations = new int[variant.samplers.length];
        for(int i = 0; i < variant.samplers.length; i++) { // TODO: check if more than 32 samplers were defined
            samplerLocations[i] = gl.glGetUniformLocation(shaderProgram, variant.samplers[i]);
        }
    }
    
    public static ShaderProgram fromJsonFile(File jsonFile) throws IOException {
        // create ShaderProgramProperties
        ShaderProgramProperties properties = Util.loadFileObject(jsonFile, ShaderProgramProperties.class);
        
        // get variant
        String vertexAttributes = TesterState.get().getVertexAttributes().getName();
        ShaderProgramProperties.Variant variant = null;
        for(ShaderProgramProperties.Variant v : properties.variants) {
            if(v.vertexAttributes.equals(vertexAttributes)) variant = v;
        }
        assert variant != null;
        
        // load shader code
        String vertexShaderCode = Util.loadFileString(variant.vertexShaderFile);
        String fragmentShaderCode = Util.loadFileString(variant.fragmentShaderFile);
        
        return new ShaderProgram(GLEvents.getGL(), vertexShaderCode, fragmentShaderCode, variant);
    }
    
    private int compileShader(String shaderCode, int shaderType) {
        // create shader
        int shaderID = gl.glCreateShader(shaderType);
        gl.glShaderSource(shaderID, 1, new String[] {shaderCode}, null);
        gl.glCompileShader(shaderID);
        
        // get success
        IntBuffer ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGetShaderiv(shaderID, GL3.GL_COMPILE_STATUS, ib);
        
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
    
    private int linkShaderProgram(int vert, int frag) {
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
    
    public void use(Matrix4f matrix, Vec3f sunDirection) {
        gl.glUseProgram(shaderProgram);
        
        // set uniforms
        gl.glUniformMatrix4fv(matrixUniformLocation, 1, false, matrix.get(new float[16]), 0);
        if(sunDirectionUniformLocation >= 0) gl.glUniform3fv(sunDirectionUniformLocation, 1, sunDirection.get(new float[3]), 0);
        for(int i = 0; i < samplerLocations.length; i++) {
            gl.glUniform1i(samplerLocations[i], i);
        }
    }
    
    @Override
    public void onDispose() {
        gl.glDeleteProgram(shaderProgram);
        IntBuffer ib = GLBuffers.newDirectIntBuffer(samplerLocations);
        gl.glDeleteSamplers(samplerLocations.length, ib);
    }
    
    private static class ShaderProgramProperties {
        public Variant variants[];
        
        public static class Variant {
            public String vertexAttributes;
            public String vertexShaderFile;
            public String fragmentShaderFile;
            public String[] samplers;
        }
    }
    
}
