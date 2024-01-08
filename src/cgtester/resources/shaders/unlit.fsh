#version 330 core

in vec3 normal;
in vec2 texCoord;

uniform sampler2D colorSampler;

out vec4 FragColor;

void main()
{
    FragColor = texture(colorSampler, texCoord) /*vec4(color, 1.0f)*/;
}