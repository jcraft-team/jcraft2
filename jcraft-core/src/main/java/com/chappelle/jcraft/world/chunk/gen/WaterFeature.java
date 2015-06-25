package com.chappelle.jcraft.world.chunk.gen;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Feature;

public class WaterFeature implements Feature
{
	private int waterLevel;
	
	public WaterFeature(int waterLevel)
	{
		this.waterLevel = waterLevel;
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, int[][][] blockTypes, int[][] heightMap)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				if(blockTypes[x][waterLevel][z] == 0)
				{
					blockTypes[x][waterLevel][z] = Block.water.blockId;
				}
			}
		}
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y <= waterLevel; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					if(blockTypes[x][y][z] != Block.water.blockId)
					{
						if(blockTypes[(x+1)&15][y][z] == Block.water.blockId)
						{
							blockTypes[x][y][z] = Block.sand.blockId;
						}
						if(x > 0 && blockTypes[x-1][y][z] == Block.water.blockId)
						{
							blockTypes[x][y][z] = Block.sand.blockId;
						}
						if(blockTypes[x][y][(z+1)&15] == Block.water.blockId)
						{
							blockTypes[x][y][z] = Block.sand.blockId;
						}
						if(z > 0 && blockTypes[x][y][z-1] == Block.water.blockId)
						{
							blockTypes[x][y][z] = Block.sand.blockId;
						}
//						if(blockTypes[x][y+1][z] == Block.water.blockId)
//						{
//							blockTypes[x][y][z] = Block.sand.blockId;
//						}
					}
				}
			}
		}
	}
}