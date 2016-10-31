uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.

attribute vec2 vPosition;
attribute vec4 vColor;
varying vec4 fColor;

void main() {
    gl_Position = u_MVPMatrix * vec4(vPosition, 0.0, 1.0);
    fColor = vColor;
}