import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;

import javax.media.opengl.GL2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CurtainLayer extends RenderableLayer {

    private int shaderprogram = -1;

    @Override
    protected void doRender(DrawContext drawContext) {

        GL2 gl = drawContext.getGL().getGL2();
        if (shaderprogram == -1) {
            shaderprogram = gl.glCreateProgram();
            gl.glAttachShader(shaderprogram, shader(gl, "curtain.vertex.glsl"));
            gl.glAttachShader(shaderprogram, shader(gl, "curtain.fragment.glsl"));
            gl.glLinkProgram(shaderprogram);
            gl.glValidateProgram(shaderprogram);
        }

        gl.glUseProgram(shaderprogram);
        super.doRender(drawContext);
        gl.glUseProgram(0);
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
}
