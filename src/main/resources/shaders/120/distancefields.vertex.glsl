#version 120

varying vec2 v_textureCoordinates; 

void main() {
    v_textureCoordinates = (gl_TextureMatrix[0] * gl_MultiTexCoord0).xy;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_Position.z -= 0.5;
}
