#version 400 core

in vec4 polygonColor;

out vec4 fragColor;

void main() {
    fragColor = polygonColor;
}