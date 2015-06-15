package com.chappelle.jcraft.world.chunk.gen;

import java.util.Random;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.ChunkFeatureGenerator;

public class WaterFeatureGenerator implements ChunkFeatureGenerator
{
	private Random rand;
	
	public WaterFeatureGenerator(long seed)
	{
		this.rand = new Random(seed);
	}

	@Override
	public void addFeatures(int[][][] blockTypes, boolean[][][] blocks_IsOnSurface)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < 254; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					if(blocks_IsOnSurface[x][y][z])
					{
						if(rand.nextInt(50) == 25)
						{
							addWater(blockTypes, x, y+1, z);
						}
					}
				}
			}
		}
	}

	private void addWater(int[][][] blockTypes, int x, int y, int z)
	{
		if(y-1 > 0)
		{
			if(blockTypes[x][y][z] == 0 && blockTypes[x][y-1][z] > 0)
			{
				blockTypes[x][y][z] = Block.water.blockId;
				if(x+1 < 16)
				{
					addWater(blockTypes, x+1, y, z);
				}
				if(x-1 > 0)
				{
					addWater(blockTypes, x-1, y, z);
				}
				if(z+1 < 16)
				{
					addWater(blockTypes, x, y, z+1);
				}
				if(z-1 > 0)
				{
					addWater(blockTypes, x, y, z-1);
				}
				if(y-1 > 0)
				{
					addWater(blockTypes, x, y-1, z);
				}
			}
		}
	}
}