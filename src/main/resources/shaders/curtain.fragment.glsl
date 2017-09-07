
uniform sampler2D u_texture;

uniform float cx;
uniform float angle;
uniform float width;
uniform float height;

varying vec4 v_texCoord;

void main() {
    if (v_texCoord.x < 0.) discard;
    if (v_texCoord.y < 0.) discard;
    if (v_texCoord.x > 1.) discard;
    if (v_texCoord.y > 1.) discard;
    float tan = tan(angle);
    if (gl_FragCoord.x + tan*gl_FragCoord.y > cx*width + height*tan*.5) discard;
    gl_FragColor = texture2D(u_texture, v_texCoord.xy);
}
