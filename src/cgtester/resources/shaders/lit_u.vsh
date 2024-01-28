#version 330 core

layout (location = 0) in vec2 aTexCoord;

uniform sampler2D positionSampler;
uniform mat4 matr;

out vec2 texCoord;

void main()
{
    texCoord = aTexCoord;
    vec3 modelPosition = texture(positionSampler, aTexCoord).rgb * 2 - vec3(1.0, 1.0, 1.0);
    gl_Position = matr * vec4(modelPosition, 1.0);
}