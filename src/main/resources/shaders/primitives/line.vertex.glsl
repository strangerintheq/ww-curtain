#version 110

//attribute vec4 vertexPoint; // x, y, dir, shift
//uniform mat4 mvpMatrix;
uniform float aspectRatio;
//varying float distance;
//varying float join;
uniform float width;
const float PI = 3.1416;
void main() {
   vec4 p = gl_Vertex;
   p.x = p.x*2. - 1.;
   p.y = p.y*2. - 1.;
   float angle = p.z / 180.0 * PI;
   float shift = p.w;
   p.z = 0.0;
   p.w = 1.0;
//      join = 1.0;
   if (shift > 0.0) {
//       join = 0.0;
       p.x += cos(angle) * width * shift;
       p.y += sin(angle) * width * shift * aspectRatio;
   }
   gl_Position = p;
//   if (angle > PI) angle -= 2.0*PI\n; +
//   distance = clamp(sign(angle), -1.0, 1.0);
}
