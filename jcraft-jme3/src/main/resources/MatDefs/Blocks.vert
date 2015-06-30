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
uniform float lightTable[] = float[16](0.05f, 0.067f, 0.085f, 0.106f, 0.129f, 0.156f, 0.186f, 0.221f, 0.261f, 0.309f, 0.367f, 0.437f, 0.525f, 0.638f, 0.789f, 1.0f);

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
	inColor.r = light;
	inColor.g = light;
	inColor.b = light;
	inColor.a = 1.0;
	
    #ifdef NEED_TEXCOORD1
        texCoord1 = inTexCoord;
    #endif

    #ifdef SEPARATE_TEXCOORD
        texCoord2 = inTexCoord2;
    #endif

    #ifdef HAS_VERTEXCOLOR
        vertColor = inColor;
    #endif

    vec4 modelSpacePos = vec4(inPosition, 1.0);
    #ifdef NUM_BONES
        Skinning_Compute(modelSpacePos);
    #endif
    gl_Position = g_WorldViewProjectionMatrix * modelSpacePos;
}