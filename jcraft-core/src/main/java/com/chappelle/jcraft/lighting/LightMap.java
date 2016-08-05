package com.chappelle.jcraft.lighting;

import com.chappelle.jcraft.Vector3Int;

public class LightMap
{
	public final static byte MIN_LIGHT = 0;
	public final static byte MAX_LIGHT = 15;
 
	private byte[][][] light;
	private Vector3Int chunkLocation;
	private Vector3Int chunkSize;
	
	public LightMap(Vector3Int chunkSize, Vector3Int chunkLocation)
	{
		this.light = new byte[chunkSize.y][chunkSize.z][chunkSize.x];
		this.chunkSize = chunkSize;
		this.chunkLocation = chunkLocation;
	}
	
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
	
	public void setLight(int x, int y, int z, LightType type, int lightVal)
	{
		if(x >= 0 && x < 16 && z >= 0 && z < 16)//sometimes get ArrayIndexOutOfBoundsExceptions. I think it's only when we are in negative land.
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
	}

	public void clearSunlight()
	{
		this.light = new byte[chunkSize.y][chunkSize.z][chunkSize.x];
//		for(int y = 0; y < chunkSize.y; y++)
//		{
//			for(int z = 0; z < chunkSize.z; z++)
//			{
//				for(int x = 0; x < chunkSize.x; x++)
//				{
//					setLight(x, y, z, LightType.SKY, 0);
//				}
//			}
//		}
	}

	private int getSunlight(int x, int y, int z)
	{
		return (light[y][z][x] >> 4) & 0xF;	// Get the bits XXXX0000
	}
	
	private int getBlocklight(int x, int y, int z)
	{
		return light[y][z][x] & 0xF;	// Get the bits 0000XXXX
	}
	
	@Override
	public String toString()
	{
		return "Lights at Chunk: " + chunkLocation + "\r\n" + light.toString();
	}
}