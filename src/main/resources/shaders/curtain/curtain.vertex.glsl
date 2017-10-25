#version 110

varying vec4 tex;

void main() {

    tex = gl_TextureMatrix[0] * gl_MultiTexCoord0;

    // produces some kind of z-fighting on some graphics hardware, when 
    // using standard pipeline(wihout shaders) together with shader enabled pipeline
    // for co-planar polygons
    // fixed with integrated polygon_offset_fill  
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

    // analog of top calculations but performed by standard graphics pipeline
    // in this case helps to avoid some kind on z-fighting on my GTX1050ti+OS Windows
    // bug is not reproduced on intel integrated graphics core
    //gl_Position = ftransform();
}
