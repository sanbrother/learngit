precision mediump float;

uniform sampler2D u_Texture;    // The input texture.

varying vec4 fColor;
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

void main() {
    // gl_FragColor = fColor;
    gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
}