#version 120

uniform sampler2D u_texture;
uniform float u_threshold;

varying vec2 v_textureCoordinates; 

void main() {
    if (((v_textureCoordinates.x >=0.) && (v_textureCoordinates.x <=1.)) && ((v_textureCoordinates.y >=0.) && (v_textureCoordinates.y <=1.))) {
    	vec4 color = texture2D(u_texture, v_textureCoordinates);
    	color.a = smoothstep(.5-u_threshold, .5+u_threshold, color.a);
    
    	gl_FragColor = color;
    	//gl_FragColor = vec4(v_textureCoordinates.x, v_textureCoordinates.y, 0, 1);
    } else {
    	gl_FragColor = vec4(0, 0, 0, 0);
    }
}