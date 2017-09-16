uniform sampler2D u_texture;

uniform float cx;
uniform float angle;
uniform float width;
uniform float height;

varying vec4 tex;

void main() {

    gl_FragColor = texture2D(u_texture, tex.xy);

    bool discardFragment =
        // if fragment outside texture coordinates
        tex.x < 0.0 || tex.x > 1.0 || tex.y < 0.0 || tex.y > 1.0 ||
        // if fragment in right half of curtain
        tan(angle)*(gl_FragCoord.y - height*.5) > cx*width - gl_FragCoord.x;

    if (discardFragment) gl_FragColor.a = 0.0;
    // todo mesure perfomance of this discard
//    if (discardFragment) discard;
}
