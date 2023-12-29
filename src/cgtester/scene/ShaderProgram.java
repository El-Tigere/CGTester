package cgtester.scene;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.util.GLBuffers;

import cgtester.Util;

public class ShaderProgram {
    
    private GL3 gl;
    private ShaderProgramProperties properties;
    private int shaderProgram;
    private int matrixUniformLocation;
    private int[] samplerLocations;
    
    // private String vertexShaderCode;
    // private String fragmentShaderCode;
    // private int matrixUniformLocation;
    // private int samplerUniformLocation;
    
    private ShaderProgram(GL3 gl, String vertexShaderCode, String fragmentShaderCode, ShaderProgramProperties properties) {
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
        samplerLocations = new int[properties.samplers.length];
        for(int i = 0; i < properties.samplers.length; i++) { // TODO: check if more than 32 samplers were defined
            samplerLocations[i] = gl.glGetUniformLocation(shaderProgram, properties.samplers[i].name);
        }
        
        // set sampler uniforms
        for(int i = 0; i < samplerLocations.length; i++) {
            gl.glUniform1i(samplerLocations[i], i);
        }
    }
    
    public static ShaderProgram fromJsonFile(GL3 gl, File jsonFile) throws IOException {
        // create ShaderProgramProperties
        ShaderProgramProperties properties = Util.loadFileObject(jsonFile, ShaderProgramProperties.class);
        
        // load shader code
        String vertexShaderCode = Util.loadFileString(properties.vertexShaderFile);
        String fragmentShaderCode = Util.loadFileString(properties.fragmentShaderFile);
        
        return new ShaderProgram(gl, vertexShaderCode, fragmentShaderCode, properties);
    }
    
    private int compileShader(String shaderCode, int shaderType) {
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
    
    public void use() {
        gl.glUseProgram(shaderProgram);
    }
    
    public void setMatrix(Matrix4f matrix) {
        gl.glUniformMatrix4fv(matrixUniformLocation, 1, false, matrix.get(new float[16]), 0);
    }
    
    private static class ShaderProgramProperties {
        public String vertexShaderFile;
        public String fragmentShaderFile;
        public SamplerUniform[] samplers;
        
        public static class SamplerUniform {
            public String name;
            public String texture;
        }
    }
    
}
