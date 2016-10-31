uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.

attribute vec2 vPosition;
attribute vec4 vColor;
attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in.

varying vec4 fColor;
varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.

void main() {
	// Pass through the texture coordinate.
	v_TexCoordinate = a_TexCoordinate;

    gl_Position = u_MVPMatrix * vec4(vPosition, 0.0, 1.0);
    fColor = vColor;
}