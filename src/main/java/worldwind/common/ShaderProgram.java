package worldwind.common;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

public class ShaderProgram {
	
	private final int programId;
	private final GL2 gl;

	public ShaderProgram(GL2 gl, String vs, String fs) {
		this.gl = gl;
		this.programId = gl.glCreateProgram();
		int vertexShader = shader(GL2.GL_VERTEX_SHADER, vs);
		int fragmentShader = shader(GL2.GL_FRAGMENT_SHADER, fs);
		gl.glAttachShader(programId, vertexShader);
		gl.glAttachShader(programId, fragmentShader);
		gl.glLinkProgram(programId);
		validateProgram();
	}

	void validateProgram() {
		gl.glValidateProgram(programId);
		IntBuffer intBuffer = IntBuffer.allocate(1);
		gl.glGetProgramiv(programId, GL2.GL_LINK_STATUS, intBuffer);
		if (intBuffer.get(0) == GL2.GL_TRUE)
			return;
		gl.glGetProgramiv(programId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
		int size = intBuffer.get(0);
		String status;
		if (size == 0) {
			status = "Unknown";
		} else {
			ByteBuffer byteBuffer = ByteBuffer.allocate(size);
			gl.glGetProgramInfoLog(programId, size, intBuffer, byteBuffer);
			status = new String(byteBuffer.array());
		}
		if (programId > 0)
			gl.glDeleteProgram(programId);
		throw new IllegalStateException(System.currentTimeMillis() + status);
	}
	
	public void useProgram() {
		gl.glUseProgram(programId);
	}

	public void disable() {
		gl.glUseProgram(0);
	}

	private int shader(int type, String src) {
		int id = gl.glCreateShader(type);
		gl.glShaderSource(id, 1, new String[] {src}, null, 0);
		gl.glCompileShader(id);
	    int status = getIntFromShader(id, GL2.GL_COMPILE_STATUS);
	    if (status == 0) {
	    	int logLength = getIntFromShader(id, GL2.GL_INFO_LOG_LENGTH);
	        ByteBuffer infoLog = ByteBuffer.allocate(logLength);
	        IntBuffer intValue = IntBuffer.allocate(1);
	        gl.glGetShaderInfoLog(id, logLength, intValue, infoLog);
	        int actualLength = intValue.get();
	        byte[] infoBytes = new byte[actualLength];
	        infoLog.get(infoBytes);
	        if (id > 0) 
	        	gl.glDeleteShader(id);
	        throw new IllegalStateException(System.currentTimeMillis() + new String(infoBytes));
	    }
		return id;
	}
	
	private int getIntFromShader(int id, int parameter){
        IntBuffer intValue = IntBuffer.allocate(1);
        gl.glGetObjectParameterivARB(id, parameter, intValue);
        return intValue.get();	
	}

	public void setVec2(String uniformName, double[] array) {
		if (null == array) 
			return;
		int uniformId = find(uniformName);
		if (uniformId > -1) gl.glUniform2f(uniformId, (float)array[0], (float)array[1]);
	}
	
	public void setVec3(String uniformName, double[] array) {
		if (null == array) 
			return;
		int uniformId = find(uniformName);
		if (uniformId > -1) gl.glUniform3f(uniformId, (float)array[0], (float)array[1], (float)array[2]);
	}

	private int find(String uniformName) {
		return gl.glGetUniformLocation(programId, uniformName);
	}

	public void setVec2Array(String uniformName, float[] b) {
		int uniformId = find(uniformName);
		if (uniformId > -1) gl.glUniform2fv(uniformId, b.length / 2, b, 0);
	}
	
	public void setVec3Array(String uniformName, float[] b) {
		int uniformId = find(uniformName);
		if (uniformId > -1) gl.glUniform3fv(uniformId, b.length / 3, b, 0);
	}

	public void setInt(String uniformName, int i) {
		int uniformId = find(uniformName);
		if (uniformId > -1) gl.glUniform1i(uniformId, i);
	}

    public void setFloat(String uniformName, float f) {
		int uniformId = find(uniformName);
		if (uniformId > -1) gl.glUniform1f(uniformId, f);
    }
}
