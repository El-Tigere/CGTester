package cgtester;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class GLEvents implements GLEventListener {
    
    private TesterState testerState;
    
    public GLEvents(TesterState testerState) {
        this.testerState = testerState;
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        System.out.println(testerState.keyStates[KeyEvent.VK_H]);
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
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
