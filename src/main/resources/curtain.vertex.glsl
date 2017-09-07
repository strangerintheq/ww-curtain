
varying vec4 v_texCoord;

void main() {
    v_texCoord = gl_TextureMatrix[0] * gl_MultiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
