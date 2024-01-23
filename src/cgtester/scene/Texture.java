package cgtester.scene;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import cgtester.GLEvents;
import cgtester.Util;

public class Texture extends Resource {
    
    private GL3 gl;
    // private TextureProperties properties;
    private int textureID;
    
    private Texture(GL3 gl, BufferedImage textureData, TextureProperties properties) {
        this.gl = gl;
        // this.properties = properties;
        
        // create texture
        IntBuffer ib = GLBuffers.newDirectIntBuffer(1);
        gl.glGenTextures(1, ib);
        textureID = ib.get(0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, textureID);
        
        // set parameters
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, properties.textureWrapS);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, properties.textureWrapT);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, properties.textureMinFilter);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, properties.textureMagFilter);
        
        // load image data
        Buffer textureBuffer = GLBuffers.newDirectByteBuffer(((DataBufferByte) textureData.getData().getDataBuffer()).getData());
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGB, textureData.getWidth(), textureData.getHeight(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, textureBuffer);
        gl.glGenerateMipmap(GL3.GL_TEXTURE_2D);
    }
    
    public static Texture fromJsonFile(File jsonFile) throws IOException {
        // create TextureProperties
        TextureProperties properties = Util.loadFileObject(jsonFile, TextureProperties.class);
        
        // load image
        BufferedImage bi = ImageIO.read(new File(properties.imageFile));
        
        return new Texture(GLEvents.getGL(), bi, properties);
    }
    
    public void bindTexture() {
        gl.glBindTexture(GL3.GL_TEXTURE_2D, textureID);
    }
    
    @Override
    public void onDispose() {
        IntBuffer ib = GLBuffers.newDirectIntBuffer(new int[] {textureID});
        gl.glDeleteTextures(1, ib);
    }
    
    private static class TextureProperties {
        public String imageFile;
        
        public int textureWrapS;
        public int textureWrapT;
        public int textureMinFilter;
        public int textureMagFilter;
    }
    
}
