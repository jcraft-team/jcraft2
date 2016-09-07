package com.chappelle.world.terrain.gen;

import org.terasology.math.geom.Vector2f;
import org.terasology.utilities.procedural.*;

import com.chappelle.jcraft.blocks.Blocks;
import com.chappelle.jcraft.world.chunk.Feature;

public class TerasologyFeature implements Feature
{
	private final Noise surfaceNoise;
	private final int seaLevel;
	
	public TerasologyFeature(int seaLevel, long seed)
	{
		surfaceNoise = new SubSampledNoise(new SimplexNoise(seed), new Vector2f(0.01f, 0.01f), 1);
		this.seaLevel = seaLevel;
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, byte[][][] blockTypes)
	{
		float[][] noise = new float[16][16];
		int xOffset = chunkX*16;
		int zOffset = chunkZ*16;
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				noise[x][z] = surfaceNoise.noise(x+xOffset, (z+zOffset)) * 20 + seaLevel;
			}
		}

		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				for(int y = 0; y < 256; y++)
				{
					float noiseVal = noise[x][z];
					if(y < noiseVal)
					{
						blockTypes[x][y][z] = Blocks.grass.blockId;
					}
				}
			}
		}
		
	}

}
