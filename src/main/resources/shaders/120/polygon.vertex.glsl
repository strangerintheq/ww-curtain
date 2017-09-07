#version 120

attribute vec3 a_pos;
attribute vec3 a_extrude;

attribute float a_dist;

uniform float u_lineheight;
uniform vec4 u_color;

// shared
varying vec4 v_color;

void main() {
    v_color = u_color;
    float dist = (a_extrude.y > 0.0) ? a_dist + u_lineheight : a_dist;
    gl_Position =  gl_ModelViewProjectionMatrix * vec4(a_pos * dist, 1.0);
}
