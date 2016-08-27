#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Skinning.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"

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

void main(){
	light=0.0;
	float lightTable[16];
	lightTable[0] = 0.05;
	lightTable[1] = 0.067;
	lightTable[2] = 0.085;
	lightTable[3] = 0.106;
	lightTable[4] = 0.129;
	lightTable[5] = 0.156;
	lightTable[6] = 0.186;
	lightTable[7] = 0.221;
	lightTable[8] = 0.261;
	lightTable[9] = 0.309;
	lightTable[10] = 0.367;
	lightTable[11] = 0.437;
	lightTable[12] = 0.525;
	lightTable[13] = 0.638;
	lightTable[14] = 0.789;
	lightTable[15] = 1.0;
	
	
	float skyLight = lightTable[int(inColor.a)] * m_dayNightLighting;
	float blockLight = lightTable[int(inColor.g)];
	light = max(blockLight, skyLight);
	if(inNormal.x != float(0))
	{
		light*=0.8;
	}
	else if(inNormal.z != float(0))
	{
		light*=0.6;
	}
	else if(inNormal.y == float(-1))
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
//        vec3 lightColor = vec3(1.0,197/255.0,143/255.0);
        vec3 lightColor = vec3(1.0,1.0,1.0);
	    vertColor = clamp(vec4(light * lightColor, inColor.a), vec4(0,0,0,0), vec4(1,1,1,1));
//		vertColor.r = light;
//		vertColor.g = light;
//		vertColor.b = light;
//		vertColor.a = 1.0;
    #endif

    vec4 modelSpacePos = vec4(inPosition, 1.0);
    #ifdef NUM_BONES
        Skinning_Compute(modelSpacePos);
    #endif

    gl_Position = TransformWorldViewProjection(modelSpacePos);
}