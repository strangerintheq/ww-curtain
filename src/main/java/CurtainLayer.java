import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;

import javax.media.opengl.GL2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class CurtainLayer extends RenderableLayer {

    private int shaderProgram = 0;
    private int vertexShader = 0;
    private int fragmentShader = 0;

    @Override
    protected void doRender(DrawContext drawContext) {
        GL2 gl = drawContext.getGL().getGL2();
        if (shaderProgram == 0) createProgram(gl);
        if (shaderProgram > 0) gl.glUseProgram(shaderProgram);
        super.doRender(drawContext);
        gl.glUseProgram(0);
    }

    private void createProgram(GL2 gl) {
        shaderProgram = gl.glCreateProgram();
        vertexShader = shader(gl, "curtain.vertex.glsl");
        fragmentShader = shader(gl, "curtain.fragment.glsl");
        gl.glAttachShader(shaderProgram, vertexShader);
        gl.glAttachShader(shaderProgram, fragmentShader);
        gl.glLinkProgram(shaderProgram);
        gl.glValidateProgram(shaderProgram);

        String status = getProgramStatus(gl, shaderProgram, GL2.GL_LINK_STATUS);
        if (null == status) return;

        cleanup(gl);

        throw new IllegalStateException(status);

    }

    private int shader(GL2 gl, String filename) {
        int id = gl.glCreateShader(filename.contains("vertex") ? GL2.GL_VERTEX_SHADER: GL2.GL_FRAGMENT_SHADER);
        gl.glShaderSource(id, 1, new String[]{readFile(filename)}, null, 0);
        gl.glCompileShader(id);
        return id;
    }

    private String readFile(String filename) {
        String src = "";
        BufferedReader reader = null;
        try {
            ClassLoader classLoader = Example.class.getClassLoader();
            File file = new File(classLoader.getResource(filename).getFile());
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                src += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return src;
    }

    void cleanup(GL2 gl) {
        if (shaderProgram > 0) {
            gl.glDeleteProgram(shaderProgram);
            shaderProgram = 0;
        }
        if (vertexShader > 0) {
            gl.glDeleteShader(vertexShader);
            vertexShader = 0;
        }
        if (fragmentShader > 0) {
            gl.glDeleteShader(fragmentShader);
            fragmentShader = 0;
        }
    }

    public static String getProgramStatus(GL2 gl, int program, int status) {
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
}
