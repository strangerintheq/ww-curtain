#version 110

uniform sampler2D u_texture;

uniform vec2 knob1;
uniform vec2 knob2;

varying vec4 tex;

void main() {
	
	float alpha = 1.0;

    // line equation is (x2-x1)*(y-y1) > (y2-y1)*(x-x1)
    
	vec2 vCur = knob2 - knob1;          // vector of curtain	        
	vec2 vPx = gl_FragCoord.xy - knob1; // vector to pixel from knob1
	
	// if vector to pixel in right side of vector of curtain
    if (vCur.x*vPx.y > vCur.y*vPx.x) alpha = 0.0;

    if (tex.x < 0.0 || tex.x > 1.0 || tex.y < 0.0 || tex.y > 1.0) {
    	alpha = 0.0;
    }

    if (alpha > 0.0) 
    	gl_FragColor = texture2D(u_texture, tex.xy);
    else 
    	gl_FragColor = vec4(0.0);

}
