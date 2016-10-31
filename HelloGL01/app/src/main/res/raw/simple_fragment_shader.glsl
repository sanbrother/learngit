precision mediump float;

uniform sampler2D u_Texture;    // The input texture.

varying vec4 fColor;
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

void main() {
    // gl_FragColor = fColor;
    vec2 flipped_texcoord = vec2(v_TexCoordinate.x, 1.0 - v_TexCoordinate.y);
    gl_FragColor = texture2D(u_Texture, flipped_texcoord);
}