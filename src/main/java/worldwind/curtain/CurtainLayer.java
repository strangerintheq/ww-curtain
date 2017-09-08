package worldwind.curtain;

import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;

import javax.media.opengl.GL2;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class CurtainLayer extends RenderableLayer {

    private int shaderProgram = 0;
    private CurtainControlLayer controller;


    private int frameBuffer;
    private int renderedTexture;
    int[] tmpHandle = new int[1];

    @Override
    protected void doRender(DrawContext drawContext) {
        GL2 gl = drawContext.getGL().getGL2();
//        if (null != controller) {
//            if (shaderProgram == 0) createProgram(gl);
//            gl.glUseProgram(shaderProgram);
//            setFloatUniform(gl, "cx", controller.getCx());
//            setFloatUniform(gl, "angle", (float) Math.PI*controller.getAngle() / 180);
//            setFloatUniform(gl, "width", drawContext.getDrawableWidth());
//            setFloatUniform(gl, "height", drawContext.getDrawableHeight());
//        }

        if (frameBuffer == 0) {
            gl.glGenFramebuffers(1, tmpHandle, 0);
            frameBuffer = tmpHandle[0];
        }

        if (renderedTexture == 0) {
            gl.glGenTextures(1, tmpHandle, 0);
            renderedTexture = tmpHandle[0];
        }

        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, frameBuffer);
        gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_TEXTURE_2D, renderedTexture, 0);


        super.doRender(drawContext);
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);



        //gl.glUseProgram(0);
    }

    private void setFloatUniform(GL2 gl, String uniformName, float value) {
        int uniformId = gl.glGetUniformLocation(shaderProgram, uniformName);
        if (uniformId > -1) gl.glUniform1f(uniformId, value);
    }

    private void createProgram(GL2 gl) {
        shaderProgram = gl.glCreateProgram();
        int vertexShader = vertexShader(gl);
        int fragmentShader = fragmentShader(gl);
        gl.glAttachShader(shaderProgram, vertexShader);
        gl.glAttachShader(shaderProgram, fragmentShader);
        gl.glLinkProgram(shaderProgram);
        gl.glValidateProgram(shaderProgram);
        String status = getProgramStatus(gl, shaderProgram, GL2.GL_LINK_STATUS);
        if (null == status) return;
        if (shaderProgram > 0) gl.glDeleteProgram(shaderProgram);
        if (vertexShader > 0) gl.glDeleteShader(vertexShader);
        if (fragmentShader > 0) gl.glDeleteShader(fragmentShader);
        shaderProgram = 0;
        throw new IllegalStateException(status);
    }

    private int vertexShader(GL2 gl) {
        return shader(gl, GL2.GL_VERTEX_SHADER);
    }

    private int fragmentShader(GL2 gl) {
        return shader(gl, GL2.GL_FRAGMENT_SHADER);
    }

    private int shader(GL2 gl, int type) {
        int id = gl.glCreateShader(type);
        String typeName = type == GL2.GL_VERTEX_SHADER ? "vertex" : "fragment";
        String path = String.format("shaders/curtain.%s.glsl", typeName);
        String[] src = { FileUtil.readText(path) };
        gl.glShaderSource(id, 1, src, null, 0);
        gl.glCompileShader(id);
        return id;
    }

    private static String getProgramStatus(GL2 gl, int program, int status) {
        IntBuffer intBuffer = IntBuffer.allocate(1);
        gl.glGetProgramiv(program, status, intBuffer);

        if (intBuffer.get(0) != 1) {
            gl.glGetProgramiv(program, GL2.GL_INFO_LOG_LENGTH, intBuffer);

            int size = intBuffer.get(0);
            if (size > 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                gl.glGetProgramInfoLog(program, size, intBuffer, byteBuffer);
                return new String(byteBuffer.array());
            } else {
                return "Unknown";
            }
        }

        return null;
    }

    public void setController(CurtainControlLayer controller) {
        this.controller = controller;
    }
}
