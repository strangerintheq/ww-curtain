#version 110

//precision mediump float;

//uniform float opacity;
//varying float distance;
//varying float join;
uniform vec3 color;

void main() {
    gl_FragColor = vec4(color,1.0);
//  if (join > 0.0) {
//    gl_FragColor.b = 1.0;gl_FragColor.r = 0.0;
//  }
//  gl_FragColor.a = 0.9;
//  if (opacity == 0.0)
//     discard;
//  gl_FragColor.r = 1.0 - abs(distance);
}
