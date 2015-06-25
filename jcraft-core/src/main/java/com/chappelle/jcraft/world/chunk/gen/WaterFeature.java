package com.chappelle.jcraft.world.chunk.gen;

import java.util.Random;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Feature;

public class WaterFeature implements Feature
{
	private int waterLevel;
	private Random rand;
	
	public WaterFeature(int waterLevel)
	{
		this.waterLevel = waterLevel;
		this.rand = new Random();
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, int[][][] blockTypes, int[][] heightMap)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y <= waterLevel; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					if(blockTypes[x][y][z] == 0)
					{
						blockTypes[x][y][z] = Block.water.blockId;
					}
				}
			}
		}
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y <= waterLevel; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					addSand(blockTypes, x, y, z, 1.0f);
				}
			}
		}
	}

	private void addSand(int[][][] blockTypes, int x, int y, int z, float probability)
	{
		if(rand.nextFloat() < probability)
		{
			if(blockTypes[x][y][z] != Block.water.blockId)
			{
				blockTypes[x][y][z] = Block.sand.blockId;
			}
			
			//THIS IS REALLY SLOW
//			if(blockTypes[(x+1)&15][y][z] != Block.water.blockId)
//			{
//				addSand(blockTypes, (x+1)&15, y, z, probability*0.7f);
//			}
//			if(x > 0 && blockTypes[x-1][y][z] != Block.water.blockId)
//			{
//				addSand(blockTypes, x-1, y, z, probability*0.7f);
//			}
//			if(blockTypes[x][y][(z+1)&15] != Block.water.blockId)
//			{
//				addSand(blockTypes, x, y, (z+1)&15, probability*0.7f);
//			}
//			if(z > 0 && blockTypes[x][y][z-1] != Block.water.blockId)
//			{
//				addSand(blockTypes, x, y, z-1, probability*0.7f);
//			}
		}
	}
}