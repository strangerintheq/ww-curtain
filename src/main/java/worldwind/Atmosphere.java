package worldwind;

import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import gov.nasa.worldwind.layers.SkyGradientLayer;
import gov.nasa.worldwind.render.DrawContext;

public class Atmosphere extends SkyGradientLayer {

	private int program;

	@Override
    public void doRender(DrawContext dc) {
		GL2 gl = dc.getGL().getGL2();
		if (program == 0) {
			program = gl.glCreateProgram();
			shader(gl, GL2.GL_VERTEX_SHADER, "c=gl_Color;gl_Position=ftransform();gl_Position.z=0.;");
			shader(gl, GL2.GL_FRAGMENT_SHADER, "gl_FragColor=c;");
			gl.glLinkProgram(program);
			IntBuffer intBuffer = IntBuffer.allocate(1);
			gl.glGetProgramiv(program, GL2.GL_LINK_STATUS, intBuffer);
			if (intBuffer.get(0) == GL2.GL_FALSE) throw new IllegalStateException("shader link error");
		} 
		gl.glUseProgram(program);		
		super.doRender(dc);
		gl.glUseProgram(0);
	}

	private void shader(GL2 gl, int type, String src) {
		int shader = gl.glCreateShader(type);
		gl.glShaderSource(shader, 1, new String[] {String.format("varying vec4 c;void main() {%s}", src)}, null, 0);
		gl.glCompileShader(shader);
		gl.glAttachShader(program, shader);
	}
}
