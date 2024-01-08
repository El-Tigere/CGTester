#version 330 core

in vec3 normal;
in vec2 texCoord;

uniform sampler2D colorSampler;
uniform vec3 sunDirection;

out vec4 FragColor;

void main()
{
    FragColor = texture(colorSampler, texCoord) * clamp(dot(normal, sunDirection) * -1.0 + 0.25, 0.25, 1.0);
}