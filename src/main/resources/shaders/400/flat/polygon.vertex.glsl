#version 400 core

layout(location = 0) in vec3  position;
layout(location = 1) in vec3  extrude;
layout(location = 2) in float dist;

layout (packed) uniform PolygonParams {
    vec4 color;
};

uniform float lineHeight;
uniform mat4 matrix;

out vec4 polygonColor;

void main() {
    polygonColor = color;
    float actualDist = (extrude.y > 0.0) ? dist + lineHeight : dist;
    gl_Position = matrix * vec4(position * actualDist, 1.0);
}
