
uniform sampler2D u_texture;
uniform float cx;
uniform float angle;
uniform float height;

varying vec4 v_texCoord;

void main() {
    if (v_texCoord.x < 0.) discard;
    if (v_texCoord.y < 0.) discard;
    if (v_texCoord.x > 1.) discard;
    if (v_texCoord.y > 1.) discard;





    if (gl_FragCoord.x > cx) discard;
    gl_FragColor = texture2D(u_texture, v_texCoord.xy);
}
