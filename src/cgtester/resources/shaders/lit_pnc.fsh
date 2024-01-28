#version 330 core

in vec3 normal;
in vec3 color;

uniform vec3 sunDirection;

out vec4 FragColor;

void main()
{
    FragColor = vec4(color, 1.0) * clamp(dot(normal, sunDirection) * -1.0 + 0.25, 0.25, 1.0);
}