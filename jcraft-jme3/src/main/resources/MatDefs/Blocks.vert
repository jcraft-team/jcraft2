#import "Common/ShaderLib/Skinning.glsllib"

uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;

#if defined(HAS_COLORMAP) || (defined(HAS_LIGHTMAP) && !defined(SEPARATE_TEXCOORD))
    #define NEED_TEXCOORD1
#endif

attribute vec2 inTexCoord;
attribute vec2 inTexCoord2;
attribute vec4 inColor;
attribute vec3 inNormal;

varying vec2 texCoord1;
varying vec2 texCoord2;
varying float light;
varying vec4 vertColor;

uniform float m_dayNightLighting;
uniform float lightTable[16] = float[](0.05, 0.067, 0.085, 0.106, 0.129, 0.156, 0.186, 0.221, 0.261, 0.309, 0.367, 0.437, 0.525, 0.638, 0.789, 1.0);

void main()
{
	float skyLight = lightTable[int(inColor.a)] * m_dayNightLighting;
	float blockLight = lightTable[int(inColor.g)];
	light = max(blockLight, skyLight);
	if(inNormal.x != 0)
	{
		light*=0.8;
	}
	else if(inNormal.z != 0)
	{
		light*=0.6;
	}
	else if(inNormal.y == -1)
	{
		light*=0.5;
	}
	
    #ifdef NEED_TEXCOORD1
        texCoord1 = inTexCoord;
    #endif

    #ifdef SEPARATE_TEXCOORD
        texCoord2 = inTexCoord2;
    #endif

    #ifdef HAS_VERTEXCOLOR
        vertColor = inColor;
		vertColor.r = light;
		vertColor.g = light;
		vertColor.b = light;
		vertColor.a = 1.0;
        
    #endif

    vec4 modelSpacePos = vec4(inPosition, 1.0);
    #ifdef NUM_BONES
        Skinning_Compute(modelSpacePos);
    #endif
    gl_Position = g_WorldViewProjectionMatrix * modelSpacePos;
}