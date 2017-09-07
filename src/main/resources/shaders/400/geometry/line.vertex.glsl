#version 400 core
// floor(127 / 2) == 63.0
// the maximum allowed miter limit is 2.0 at the moment. the extrude normal is
// stored in a byte (-128..127). we scale regular normals up to length 63, but
// there are also "special" normals that have a bigger length (of up to 126 in
// this case).
#define scale 0.015873016

layout(location = 0) in vec3  position;
layout(location = 1) in vec3  extrude;
layout(location = 2) in float dist;
layout(location = 4) in int   arg;

layout (packed) uniform LineParams {
    vec4  capColor;
    vec4  sideColor;
    float lineWidth;
};

uniform mat4 matrix;

out vec4 color;

void main() {
    //Last bit
    bool lastBit = bool(arg & 1);
    
    color = lastBit ? sideColor : capColor;

    // Scale the extrusion vector down to a normal and then up by the line width
    // of this vertex.
    vec4 extrudeDist = vec4(lineWidth * extrude * scale, 0.0);
    
    float atctualDist = lastBit ? dist : dist + 50;

    // Remove the texture normal bit of the position before scaling it with the
    // model/view matrix. Add the extrusion vector *after* the model/view matrix
    // because we're extruding the line in pixel space, regardless of the current
    // tile's zoom level.
    gl_Position = matrix * (vec4(position * atctualDist, 1.0) + extrudeDist);
}