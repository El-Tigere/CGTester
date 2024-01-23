package cgtester.scene;

import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.math.Vec3f;

import cgtester.GLEvents;
import cgtester.Util;

public class Material extends Resource {
    
    private GL3 gl;
    private ShaderProgram shaderProgram;
    private Texture[] textures;
    
    public Material(GL3 gl, ShaderProgram shaderProgram, Texture[] textures) {
        this.gl = gl;
        this.shaderProgram = shaderProgram;
        this.textures = textures;
    }
    
    public static Material fromJsonFile(File jsonFile) throws IOException {
        // create MaterialProperties
        MaterialProperties properties = Util.loadFileObject(jsonFile, MaterialProperties.class);
        
        ShaderProgram shaderProgram = ResourceManager.getFromName(properties.shaderProgram, ShaderProgram.class);
        Texture[] textures = new Texture[properties.textures.length];
        for(int i = 0; i < textures.length; i++) {
            textures[i] = ResourceManager.getFromName(properties.textures[i], Texture.class);
        }
        
        return new Material(GLEvents.getGL(), shaderProgram, textures);
    }
    
    public void use(Matrix4f matrix, Vec3f sunDirection) {
        // bind textures
        for(int i = 0; i < textures.length; i++) {
            gl.glActiveTexture(GL3.GL_TEXTURE0 + i);
            textures[i].bindTexture();
        }
        
        shaderProgram.use(matrix, sunDirection);
    }
    
    @Override
    public void onDispose() {
        shaderProgram.dispose();
        for(Texture t : textures) {
            t.dispose();
        }
    }
    
    private static class MaterialProperties {
        public String shaderProgram;
        public String[] textures;
    }
    
}
