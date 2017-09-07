uniform sampler2D u_texture;

varying vec4 v_texCoord;

void main() {
    if (v_texCoord.x < 0.) discard;
    if (v_texCoord.y < 0.) discard;
    if (v_texCoord.x > 1.) discard;
    if (v_texCoord.y > 1.) discard;
    if (gl_FragCoord.x > 400) discard;
    vec4 c = texture2D(u_texture, v_texCoord.xy);
    c.r += 0.1;
    gl_FragColor = c;//vec4(1.0, 0.0, 0.0, 1.0);
}
