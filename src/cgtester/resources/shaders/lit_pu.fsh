#version 330 core

in vec2 texCoord;

uniform sampler2D normalSampler;
uniform sampler2D colorSampler;
uniform vec3 sunDirection;

out vec4 FragColor;

void main()
{
    vec3 normal = texture(normalSampler, texCoord).xyz * 2 - vec3(1.0, 1.0, 1.0);
    FragColor = texture(colorSampler, texCoord) * clamp(dot(normal, sunDirection) * -1.0 + 0.25, 0.25, 1.0);
}