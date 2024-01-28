#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec3 aColor;

uniform mat4 matr;

out vec3 normal;
out vec3 color;

void main()
{
    normal = aNormal;
    color = aColor;
    gl_Position = matr * vec4(aPos.x, aPos.y, aPos.z, 1.0);
}