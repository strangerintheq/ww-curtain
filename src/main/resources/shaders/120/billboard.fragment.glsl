#version 120

uniform vec4 u_color;
uniform sampler2D u_texture;
uniform float u_threshold;

varying vec2 v_textureCoordinate;

void main() {
    float c = texture2D(u_texture, v_textureCoordinate).a;
    float res = smoothstep(.5-u_threshold, .5+u_threshold, c);
    
    gl_FragColor = u_color;
    gl_FragColor.a = u_color.a - 1 + res;
}