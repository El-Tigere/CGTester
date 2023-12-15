#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aColor;
uniform mat4 matr;
out vec3 color;

void main()
{
    color = aColor;
    gl_Position = matr * vec4(aPos.x, aPos.y, aPos.z, 1.0);
}