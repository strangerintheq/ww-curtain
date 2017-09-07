#version 120
// floor(127 / 2) == 63.0
// the maximum allowed miter limit is 2.0 at the moment. the extrude normal is
// stored in a byte (-128..127). we scale regular normals up to length 63, but
// there are also "special" normals that have a bigger length (of up to 126 in
// this case).
#define scale    0.015873016

attribute vec2 a_pos;
attribute vec2 a_extrude;
attribute vec2 a_textureCoordinate;

uniform vec3 u_referencepoint;
uniform float u_linewidth;

varying vec2 v_textureCoordinate;

void main() {    
    vec3 CameraRight_worldspace = vec3(gl_ModelViewMatrix[0][0], gl_ModelViewMatrix[1][0], gl_ModelViewMatrix[2][0]);
	vec3 CameraUp_worldspace = vec3(gl_ModelViewMatrix[0][1], gl_ModelViewMatrix[1][1], gl_ModelViewMatrix[2][1]);
	
	vec2 actualPos = a_pos + a_extrude * scale * u_linewidth;
	
	v_textureCoordinate = a_textureCoordinate;
    
    gl_Position =  gl_ModelViewProjectionMatrix * vec4(u_referencepoint + CameraRight_worldspace * actualPos.x + CameraUp_worldspace * actualPos.y, 1.0);
}