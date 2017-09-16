varying vec4 tex;

void main() {

    tex = gl_TextureMatrix[0] * gl_MultiTexCoord0;

    // produces some kind of z-figting on some graphics hardware
    // gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

    // analogue of top calculations but performed by standard graphics pipeline
    // in this case helps to avoid some kind on z-fighting on my GTX1050ti
    // bug is not reproduced on intel integrated graphics core
    gl_Position = ftransform();
}
