#version 120
// floor(127 / 2) == 63.0
// the maximum allowed miter limit is 2.0 at the moment. the extrude normal is
// stored in a byte (-128..127). we scale regular normals up to length 63, but
// there are also "special" normals that have a bigger length (of up to 126 in
// this case).
#define scale    0.015873016

attribute vec3 a_pos;
attribute vec3 a_extrude;
attribute float a_arg1;

attribute float a_dist;

uniform float u_linewidth;
uniform float u_lineheight;
uniform vec4 u_capcolor;
uniform vec4 u_sidecolor;

// shared
varying vec4 v_color;

void main() {
    // Color
    v_color = (a_arg1 != 0.0) ? u_sidecolor : u_capcolor;

    // Scale the extrusion vector down to a normal and then up by the line width
    // of this vertex.
    vec4 extrudeDist = vec4(u_linewidth * a_extrude * scale, 0.0);
    
    float dist = (a_arg1 != 0.0) ? a_dist : a_dist + u_lineheight;

    // Remove the texture normal bit of the position before scaling it with the
    // model/view matrix. Add the extrusion vector *after* the model/view matrix
    // because we're extruding the line in pixel space, regardless of the current
    // tile's zoom level.
    gl_Position =  gl_ModelViewProjectionMatrix * (vec4(a_pos * dist, 1.0) + extrudeDist);
}