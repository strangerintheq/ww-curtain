#version 110

uniform vec2 knob1;
uniform vec2 knob2;
uniform vec2 center;
uniform vec2 mouse;
uniform vec3 color1;
uniform vec3 color2;


float fillMask(float dist) {
	return clamp(-dist, 0.0, 1.0);
}

float innerBorderMask(float dist, float width) {
	float alpha1 = clamp(dist + width, 0.0, 1.0);
	float alpha2 = clamp(dist, 0.0, 1.0);
	return alpha1 - alpha2;
}

float outerBorderMask(float dist, float width){
	float alpha1 = clamp(dist, 0.0, 1.0);
	float alpha2 = clamp(dist - width, 0.0, 1.0);
	return alpha1 - alpha2;
}

vec3 rgb(float r, float g, float b) {
	return vec3(r / 255.0, g / 255.0, b / 255.0);
}

vec3 rgb(vec3 c) {
	return rgb(c.r, c.g, c.b);
}

float substract(float d1, float d2) {
	return max(-d1, d2);
}

float smoothJoin(float d1, float d2, float k) {
    float h = clamp(0.5 + 0.5*(d2 - d1)/k, 0.0, 1.0);
    return mix(d2, d1, h) - k * h * (1.0-h);
}

float join(float d1, float d2) {
    return min(d2, d1);
}

float circle(vec2 uv, vec2 pos, float rad) {
	return length(pos - uv) - rad;
}

float line(vec2 p, vec2 start, vec2 end, float width) {
//	p.x += 5.0*asin(sin(p.y));
	if (start == end || width < 0.1) return 1e9;
	vec2 dir = start - end;
	float lngth = length(dir);
	dir /= lngth;
	vec2 proj = max(0.0, min(lngth, dot(start - p, dir))) * dir;
	return length(start - p - proj) - (width / 2.0);
}

float factor(float value) {
	return 1.0 - clamp(value/128.0, 0.0, 1.0);
}

void main() {
	vec2 uv = gl_FragCoord.xy;

	float f1 = factor(length(knob1 - mouse)) * 3.0; // knob 1 size factor
	float f2 = factor(length(knob2 - mouse)) * 3.0; // knob 2 size factor
	float lf = factor(line(mouse, knob1, knob2, 10.0)); // line width factor

	float d = line(uv, knob1, knob2, 5.5 * lf);
	d = smoothJoin(d, circle(uv, knob1, 12.0 + f1), 5.0);
	d = smoothJoin(d, circle(uv, knob2, 12.0 + f2), 5.0);
//	d = smoothJoin(d, circle(uv, mouse, 10.), 5.0);
	d = join(d, circle(uv, center, 7.0));
//	d = substract(circle(uv, knob1, 7.0 + f1), d);
//	d = substract(circle(uv, knob2, 7.0 + f2), d);
	d = substract(circle(uv, center, 3.0), d);
	vec4 col = vec4(0.0);
	
	float dMouse = circle(uv, mouse, 2.5);
	float smoothMouse = 35.0;
	vec3 ui = mix(rgb(color1), rgb(color2), 1.0 - clamp(dMouse, 0.0, smoothMouse)/smoothMouse);
	col = mix(col, vec4(ui, 1.0), fillMask(d));
//	col = mix(col, vec4(0.0, 0.0, 0.0, 1.0), innerBorderMask(d, 0.3));
	gl_FragColor = clamp(col, 0.0, 1.0);

	float smoothing = 0.5;
	float t = clamp(d, 0.0, smoothing);
	gl_FragColor.a = 1.0 - t/smoothing;

}
