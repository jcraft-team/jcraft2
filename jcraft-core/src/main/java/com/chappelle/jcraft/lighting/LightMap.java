package com.chappelle.jcraft.lighting;

import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.Block;

public class LightMap
{
	public final static byte MIN_LIGHT = 0;
	public final static byte MAX_LIGHT = 15;
 
	private byte[][][] light;
	private Vector3Int chunkLocation;
	private Vector3Int chunkSize;
	
	float[] lightTable = new float[]{0.05f, 0.067f, 0.085f, 0.106f, 0.129f, 0.156f, 0.186f, 0.221f, 0.261f, 0.309f, 0.367f, 0.437f, 0.525f, 0.638f, 0.789f, 1.0f};
	
	public LightMap(Vector3Int chunkSize, Vector3Int chunkLocation)
	{
		this.light = new byte[chunkSize.y][chunkSize.z][chunkSize.x];
		this.chunkSize = chunkSize;
		this.chunkLocation = chunkLocation;
	}
	
	public float getEffectiveLight(int x, int y, int z, Block block, Block.Face face)
	{
		int skyLight = getLight(x, y, z, LightType.SKY) - block.getBlockedSkylight();
		int blockLight = getLight(x, y, z, LightType.BLOCK);
		float faceConstant = 1.0f;
		if(face == Block.Face.Left || face == Block.Face.Right)
		{
			faceConstant = 0.8f;
		}
		else if(face == Block.Face.Front || face == Block.Face.Back)
		{
			faceConstant = 0.6f;
		}
		else if(face == Block.Face.Bottom)
		{
			faceConstant = 0.5f;
		}
		return lightTable[Math.max(blockLight, skyLight)] * faceConstant;
	}

	//OLD way. Leaving it for reference purposes for now
//	public float getEffectiveLight(int x, int y, int z, Block block, Block.Face face)
//	{
//		float result = ((float)getLight(x, y, z)/(float)MAX_LIGHT);
//		return Math.max(0.025f, result);
//	}
	
	public int getLight(int x, int y, int z, LightType lightType)
	{
		if(lightType == LightType.BLOCK)
		{
			return getBlocklight(x, y, z);
		}
		return getSunlight(x, y, z);
	}
	
	public int getLight(int x, int y, int z)
	{
		int blocklight = getBlocklight(x, y, z);
		int sunlight = getSunlight(x, y, z); 
		return Math.max(blocklight, sunlight);
	}
	
	private int getSunlight(int x, int y, int z)
	{
		return (light[y][z][x] >> 4) & 0xF;	// Get the bits XXXX0000
	}
	
	private int getBlocklight(int x, int y, int z)
	{
		return light[y][z][x] & 0xF;	// Get the bits 0000XXXX
	}
	
	public void setLight(int x, int y, int z, LightType type, int lightVal)
	{
		if(type == LightType.BLOCK)
		{
			// Set the bits 0000XXXX
			light[y][z][x] = (byte)((light[y][z][x] & 0xF0) | lightVal);
		}
		else
		{
			// Set the bits XXXX0000
			light[y][z][x] = (byte)((light[y][z][x] & 0xF) | (lightVal << 4));
		}
	}

	public void clearSunlight()
	{
		for(int y = 0; y < chunkSize.y; y++)
		{
			for(int z = 0; z < chunkSize.z; z++)
			{
				for(int x = 0; x < chunkSize.x; x++)
				{
					setLight(x, y, z, LightType.SKY, 0);
				}
			}
		}
	}
	@Override
	public String toString()
	{
		return "Lights at Chunk: " + chunkLocation + "\r\n" + light.toString();
	}
}