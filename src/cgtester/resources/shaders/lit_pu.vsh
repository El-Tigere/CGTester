#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;

uniform mat4 matr;

out vec2 texCoord;

void main()
{
    texCoord = aTexCoord;
    gl_Position = matr * vec4(aPos.x, aPos.y, aPos.z, 1.0);
}