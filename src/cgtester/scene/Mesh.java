package cgtester.scene;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import cgtester.GLEvents;
import cgtester.Util;

public class Mesh extends Resource {
    
    private GL3 gl;
    private int vao, vbo, ebo;
    private int indexCount;
    
    private Mesh(GL3 gl, float[] vertexData, int[] faceIndices, MeshProperties properties) {        
        this.gl = gl;
        indexCount = faceIndices.length;
        
        IntBuffer ib;
        
        // create vao
        ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenVertexArrays(1, ib);
        //System.out.println("error: " + gl.glGetError());
        vao = ib.get(0);
        //System.out.println("vao: " + vao);
        gl.glBindVertexArray(vao);
        
        // create vbo
        ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenBuffers(1, ib);
        vbo = ib.get(0);
        //System.out.println("vbo: " + vbo);
        // copy vertex data
        FloatBuffer vertexDataBuffer = GLBuffers.newDirectFloatBuffer(vertexData);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
        //System.out.println(gl.getBoundBuffer(GL3.GL_ARRAY_BUFFER));
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
    
    public static Mesh fromJsonFile(File jsonFile) throws IOException { // TODO: less datatstructure conversions
        //System.out.println("Mesh fromJsonFile " + jsonFile + " start");
        // create MeshProperties
        MeshProperties properties = Util.loadFileObject(jsonFile, MeshProperties.class);
        
        // load obj
        ArrayList<float[]> positions = new ArrayList<>();
        ArrayList<float[]> normals = new ArrayList<>();
        ArrayList<float[]> uvs = new ArrayList<>();
        ArrayList<Float> vertexDataList = new ArrayList<>();
        ArrayList<Integer> faceIndicesList = new ArrayList<>();
        int nextFaceIndex = 0;
        
        Scanner objScanner = new Scanner(new File(properties.meshFile));
        while(objScanner.hasNextLine()) {
            String line = objScanner.nextLine();
            
            if(line.startsWith("v ")) positions.add(parseFloatArray(line.substring(2)));
            if(line.startsWith("vn ")) normals.add(parseFloatArray(line.substring(3)));
            if(line.startsWith("vt ")) uvs.add(parseFloatArray(line.substring(3)));
            
            if(line.startsWith("f ")) {
                int[][] indices = parseVertexIndices(line.substring(2));
                for(int[] vertex : indices) {
                    for(float f : positions.get(vertex[0] - 1)) vertexDataList.add(f);
                    for(float f : normals.get(vertex[2] - 1)) vertexDataList.add(f);
                    for(float f : uvs.get(vertex[1] - 1)) vertexDataList.add(f);
                    
                    faceIndicesList.add(nextFaceIndex++);
                }
            }
        }
        objScanner.close();
        
        // convert ArrayLists to Arrays
        float[] vertexData = new float[vertexDataList.size()];
        for(int i = 0; i < vertexDataList.size(); i++) vertexData[i] = vertexDataList.get(i);
        int[] faceIndices = new int[faceIndicesList.size()];
        for(int i = 0; i < faceIndicesList.size(); i++) faceIndices[i] = faceIndicesList.get(i);
        
        return new Mesh(GLEvents.getGL(), vertexData, faceIndices, properties);
    }
    
    private static float[] parseFloatArray(String string) {
        String[] parts = string.split(" ");
        float[] floats = new float[parts.length];
        for(int i = 0; i < parts.length; i++) {
            floats[i] = Float.parseFloat(parts[i]);
        }
        return floats;
    }
    
    private static int[][] parseVertexIndices(String string) {
        String[] parts = string.split(" ");
        int[][] indices = new int[parts.length][3];
        for(int i = 0; i < parts.length; i++) {
            String[] subParts = parts[i].split("/");
            for(int j = 0; j < 3; j++) indices[i][j] = Integer.parseInt(subParts[j]);
        }
        return indices;
    }
    
    public void draw() {
        gl.glBindVertexArray(vao);
        gl.glDrawElements(GL3.GL_TRIANGLES, indexCount, GL3.GL_UNSIGNED_INT, 0);
    }
    
    @Override
    public void onDispose() {
        IntBuffer ib;
        
        ib = GLBuffers.newDirectIntBuffer(new int[] {vao});
        gl.glDeleteVertexArrays(1, ib);
        
        ib = GLBuffers.newDirectIntBuffer(new int[] {vbo, ebo});
        gl.glDeleteBuffers(2, ib);
    }
    
    private static class MeshProperties {
        public String meshFile;
    }
    
}
