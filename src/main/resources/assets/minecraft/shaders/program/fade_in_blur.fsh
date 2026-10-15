#version 120

uniform sampler2D DiffuseSampler;

// This program uses Minecraft 1.8.9's built-in "sobel" vertex shader.  That
// shader is GLSL 1.20 and exposes these as varyings, so the fragment shader
// must use the same GLSL interface to link on every supported driver.
varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 BlurDir;
uniform float Radius;
uniform float Progress;

void main() {
    float progRadius = floor(Radius * Progress);
	if (progRadius < 1.0) {
		gl_FragColor = vec4(texture2D(DiffuseSampler, texCoord).rgb, 1.0);
		return;
	}
    vec2 texelDir = oneTexel * BlurDir;
    vec4 blurred = vec4(0.0);
    for (float r = -progRadius + 0.5; r <= progRadius; r += 2.0) {
        blurred += texture2D(DiffuseSampler, texCoord + texelDir * r);
    }
    blurred += texture2D(DiffuseSampler, texCoord + texelDir * progRadius) / 2.0;
    gl_FragColor = vec4(blurred.rgb / (progRadius + 0.5), 1.0);
}
