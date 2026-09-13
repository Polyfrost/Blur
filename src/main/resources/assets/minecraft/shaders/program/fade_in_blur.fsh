#version 120

uniform sampler2D DiffuseSampler;

// This program uses Minecraft 1.8.9's built-in "sobel" vertex shader.  That
// shader is GLSL 1.20 and exposes these as varyings, so the fragment shader
// must use the same GLSL interface to link on every supported driver.
varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform float Radius;
uniform float Progress;

void main() {
    vec4 blurred = vec4(0.0);
    float totalStrength = 0.0;
    float totalAlpha = 0.0;
    float totalSamples = 0.0;
    float progRadius = floor(Radius * Progress);
	// The first fade frame has a zero radius.  Sampling the source directly
	// avoids the r / progRadius division below becoming NaN.
	if (progRadius < 1.0) {
		gl_FragColor = texture2D(DiffuseSampler, texCoord);
		return;
	}
    for(float r = -progRadius; r <= progRadius; r += 1.0) {
        vec4 sample = texture2D(DiffuseSampler, texCoord + oneTexel * r * BlurDir);

		// Accumulate average alpha
        totalAlpha = totalAlpha + sample.a;
        totalSamples = totalSamples + 1.0;

		// Accumulate smoothed blur
        float strength = 1.0 - abs(r / progRadius);
        totalStrength = totalStrength + strength;
        blurred = blurred + sample;
    }
    gl_FragColor = vec4(blurred.rgb / (progRadius * 2.0 + 1.0), totalAlpha);
}
