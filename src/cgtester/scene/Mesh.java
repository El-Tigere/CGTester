package cgtester.scene;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import cgtester.GLEvents;
import cgtester.TesterState;
import cgtester.Util;
import cgtester.TesterState.VertexAttributes;

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
        VertexAttributes attributes = TesterState.get().getVertexAttributes();
        int attributeMask = attributes.getAttributeMask();
        int locationCounter = 0;
        int offsetCounter = 0;
        for(int i = 0; i < 4; i++) {
            int compareMask = 0b1000 >> i;
            if((attributeMask & compareMask) > 0) {
                int valueCount = VertexAttributes.calcValueCount(compareMask);
                gl.glVertexAttribPointer(locationCounter, valueCount, GL3.GL_FLOAT, false, attributes.getValueCount() * 4, offsetCounter * 4);
                gl.glEnableVertexAttribArray(locationCounter++);
                offsetCounter += valueCount;
            }
        }
    }
    
    public static Mesh fromJsonFile(File jsonFile) throws IOException { // TODO: less datatstructure conversions
        // create MeshProperties
        MeshProperties properties = Util.loadFileObject(jsonFile, MeshProperties.class);
        
        ArrayList<float[]> positionsAndColors = new ArrayList<>();
        ArrayList<float[]> normals = new ArrayList<>();
        ArrayList<float[]> uvs = new ArrayList<>();
        HashMap<Vertex, Integer> vertexHashMap = new HashMap<>();
        ArrayList<Vertex> vertexArrayList = new ArrayList<>();
        ArrayList<Integer> faceIndicesList = new ArrayList<>();
        int nextFaceIndex = 0;
        
        // load obj
        Scanner objScanner = new Scanner(new File(properties.meshFile));
        while(objScanner.hasNextLine()) {
            String line = objScanner.nextLine();
            
            if(line.startsWith("v ")) positionsAndColors.add(parseFloatArray(line.substring(2)));
            if(line.startsWith("vn ")) normals.add(parseFloatArray(line.substring(3)));
            if(line.startsWith("vt ")) uvs.add(parseFloatArray(line.substring(3)));
            
            if(line.startsWith("f ")) {
                int[][] indices = parseVertexIndices(line.substring(2));
                for(int[] vertex : indices) {
                    Vertex v = new Vertex(vertex);
                    
                    if(vertexHashMap.containsKey(v)) {
                        faceIndicesList.add(vertexHashMap.get(v));
                        continue;
                    }
                    
                    vertexHashMap.put(v, nextFaceIndex);
                    faceIndicesList.add(nextFaceIndex);
                    vertexArrayList.add(v);
                    nextFaceIndex++;
                }
            }
        }
        objScanner.close();
        
        // create vertex data array
        VertexAttributes attributes = TesterState.get().getVertexAttributes();
        int attributeMask = attributes.getAttributeMask();
        float[] vertexData = new float[vertexArrayList.size() * attributes.getValueCount()];
        int vertexDataIndex = 0;
        for(Vertex v : vertexArrayList) { // insert vertex data into array
            if((attributeMask & 0b1000) > 0) for(int i = 0; i < 3; i++) vertexData[vertexDataIndex++] = positionsAndColors.get(v.ids[0] - 1)[i]; // - 1 because obj vertex property indices start at 1
            if((attributeMask & 0b0100) > 0) for(int i = 0; i < 3; i++) vertexData[vertexDataIndex++] = normals.get(v.ids[2] - 1)[i]; // obj vertex property index order: position+color, uv, normal
            if((attributeMask & 0b0010) > 0) for(int i = 3; i < 6; i++) vertexData[vertexDataIndex++] = positionsAndColors.get(v.ids[0] - 1)[i];
            if((attributeMask & 0b0001) > 0) for(int i = 0; i < 2; i++) vertexData[vertexDataIndex++] = uvs.get(v.ids[1] - 1)[i];
        }
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
    
    private static class Vertex {
        public int[] ids;
        
        public Vertex(int ids[]) {
            this.ids = ids;
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(ids);
        }
        
        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof Vertex)) return false;
            return Arrays.equals(ids, ((Vertex) obj).ids);
        }
    }
    
    private static class MeshProperties {
        public String meshFile;
    }
    
}
